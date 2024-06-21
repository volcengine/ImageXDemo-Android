/*
 * Copyright (2024) Beijing Volcano Engine Technology Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Create Date : 2024/05/14
 */

package com.bytedance.imagexdemo.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.imagexdemo.ImageXApplication
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.FragmentImageBinding
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_ANIMATION_CONTROL
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_DECODE
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_PROGRESSIVE
import com.bytedance.imagexdemo.model.IMAGE_TYPE_UNKNOWN
import com.bytedance.imagexdemo.model.ImageTabType
import com.bytedance.imagexdemo.model.ImageType
import com.bytedance.imagexdemo.model.adapter.ImageAdapter
import com.bytedance.imagexdemo.ui.ImageDetailActivity
import com.bytedance.imagexdemo.ui.base.BaseVMFragment
import com.bytedance.imagexdemo.ui.fragment.dialog.UrlInputDialogFragment
import com.bytedance.imagexdemo.utils.CacheConfig
import com.bytedance.imagexdemo.utils.DPUtils
import com.bytedance.imagexdemo.utils.jumpToActivity
import com.bytedance.imagexdemo.utils.setVisible
import com.bytedance.imagexdemo.utils.toast
import com.bytedance.imagexdemo.viewmodel.ImageViewModel
import com.bytedance.imagexdemo.viewmodel.ImageViewModelFactory
import com.optimize.statistics.ImageTraceListener
import org.json.JSONObject


/**
 * 图片列表Fragment
 */
class ImageFragment : BaseVMFragment<FragmentImageBinding, ImageViewModel>(), ImageTraceListener {

    private val imageType: Int by lazy {
        arguments?.getInt(ARG_IMAGE_TYPE, IMAGE_TYPE_UNKNOWN) ?: IMAGE_TYPE_UNKNOWN
    }

    private val tabType: Int by lazy {
        arguments?.getInt(ARG_IMAGE_TAB_TYPE, IMAGE_TAB_TYPE_DECODE) ?: IMAGE_TAB_TYPE_DECODE
    }

    private val imageLogMap = HashMap<String, JSONObject?>()

    private var ignoreMemoryCache = false

    private var ignoreDiscCache = false

    private var cacheVersion = 0


    companion object {
        private const val ARG_IMAGE_TYPE = "ImageType"
        private const val ARG_IMAGE_TAB_TYPE = "ImageTabType"

        @JvmStatic
        fun newInstance(@ImageTabType tabType: Int, @ImageType pageType: Int): ImageFragment {
            return ImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_TYPE, pageType)
                    putInt(ARG_IMAGE_TAB_TYPE, tabType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initImageLog
        ImageXApplication.instance().addTraceListener(this)
        ignoreMemoryCache = CacheConfig.ignoreMemoryCache
        ignoreDiscCache = CacheConfig.ignoreDisCache
        cacheVersion = CacheConfig.cacheConfigChangeVersion
    }

    override fun onResume() {
        super.onResume()
        checkCacheSettingChange()
    }

    /**
     * 缓存设置变更后，重新加载一下图片。
     */
    private fun checkCacheSettingChange() {
        if (CacheConfig.cacheConfigChangeVersion == 0 ||
            cacheVersion == CacheConfig.cacheConfigChangeVersion
        ) {
            return
        }
        ignoreMemoryCache = CacheConfig.ignoreMemoryCache
        ignoreDiscCache = CacheConfig.ignoreDisCache
        cacheVersion = CacheConfig.cacheConfigChangeVersion
        val adapter = binding.recyclerView.adapter as? ImageAdapter ?: return
        adapter.reloadCacheSettings(
            ignoreMemoryCache, ignoreDiscCache
        )
        adapter.submitList(emptyList())
        val list = mViewModel.imageListData.value ?: return
        adapter.submitList(list)
    }

    override fun initViewModel(storeOwner: ViewModelStoreOwner?) {
        storeOwner ?: return
        mViewModel =
            ViewModelProvider(storeOwner, ImageViewModelFactory(imageType))[viewModelClass()]
    }

    override fun viewModelClass(): Class<ImageViewModel> {
        return ImageViewModel::class.java
    }

    override fun initViewBinding(): FragmentImageBinding {
        return FragmentImageBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        binding.btAddImage.setOnClickListener {
            UrlInputDialogFragment.showUrlInputDialog(activity as FragmentActivity, imageType)
                .apply {
                    setImageViewModel(mViewModel)
                }
        }
        binding.tvWeakNetTips.setVisible(tabType == IMAGE_TAB_TYPE_PROGRESSIVE)

        val imageAdapter =
            ImageAdapter(tabType, imageType, ignoreMemoryCache, ignoreDiscCache).apply {
                setImageItemClickListener { bean, requestId ->
                    val url = bean.url
                    if (TextUtils.isEmpty(requestId)) {
                        context?.toast(getString(R.string.image_not_ready))
                        return@setImageItemClickListener
                    }
                    val log = imageLogMap[url + requestId]
                    if (log == null) {
                        context?.toast(getString(R.string.image_not_ready))
                        return@setImageItemClickListener
                    }
                    context?.jumpToActivity<ImageDetailActivity>(data = Bundle().apply {
                        putString(ImageDetailActivity.IMAGE_URL, url)
                        log.let { jsonObject ->
                            putString(ImageDetailActivity.IMAGE_LOG, jsonObject.toString(4))
                            putInt(ImageDetailActivity.IMAGE_TAB, tabType)
                            val imageData = mViewModel.getCommonImageData(jsonObject)
                            putParcelable(ImageDetailActivity.IMAGE_COMMON_LOG_DATA, imageData)
                        }
                    })
                }
            }
        initRecyclerView(imageAdapter)
    }

    private fun initRecyclerView(imageAdapter: ImageAdapter) {
        with(binding.recyclerView) {
            val isAnimateTab = tabType == IMAGE_TAB_TYPE_ANIMATION_CONTROL
            layoutManager =
                GridLayoutManager(this@ImageFragment.context, if (isAnimateTab) 1 else 2)
            adapter = imageAdapter
            setHasFixedSize(true)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    val position = parent.getChildAdapterPosition(view)
                    if (isAnimateTab) {
                        outRect.set(
                            DPUtils.dp2Px(5f),
                            if (position == 0) 0 else DPUtils.dp2Px(6f),
                            DPUtils.dp2Px(5f),
                            DPUtils.dp2Px(6f)
                        )
                        return
                    }
                    if (position % 2 == 1) {
                        // 右侧 Item
                        outRect.set(
                            DPUtils.dp2Px(5f),
                            if (position == 1) 0 else DPUtils.dp2Px(6f),
                            0,
                            DPUtils.dp2Px(6f)
                        )
                    } else {
                        // 左侧 Item
                        outRect.set(
                            0,
                            if (position == 0) 0 else DPUtils.dp2Px(6f),
                            DPUtils.dp2Px(5f),
                            DPUtils.dp2Px(6f)
                        )
                    }
                }
            })
            addOnLayoutChangeListener { _, left, _, right, _, _, _, _, _ ->
                val spanCount = (layoutManager as GridLayoutManager).spanCount
                val itemSize = (right - left - DPUtils.dp2Px(10f)) / spanCount
                imageAdapter.setResizeOptions(itemSize)
            }
        }
    }

    override fun initData() {
        mViewModel.fetchImageData()
    }


    override fun initObserve() {
        mViewModel.imageListData.observe(viewLifecycleOwner) { imageList ->
            imageList ?: return@observe
            (binding.recyclerView.adapter as? ImageAdapter)?.submitList(imageList)
        }
    }

    /**
     * 图片性能日志回调
     */
    override fun onImageLoaded(isSuccess: Boolean, requestId: String?, jsonObj: JSONObject?) {
        val uri = jsonObj?.optString("uri") ?: return
        if (requestId.isNullOrEmpty()) return
        imageLogMap[uri + requestId] = jsonObj
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageXApplication.instance().removeTraceListener(this)
    }
}

