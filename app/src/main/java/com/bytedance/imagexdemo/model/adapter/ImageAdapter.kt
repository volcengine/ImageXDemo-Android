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

package com.bytedance.imagexdemo.model.adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.imagexdemo.databinding.ItemImageBinding
import com.bytedance.imagexdemo.datastore.SPUtil
import com.bytedance.imagexdemo.model.AWEBP
import com.bytedance.imagexdemo.model.HEIC
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_PROGRESSIVE
import com.bytedance.imagexdemo.model.ImageDataBean
import com.bytedance.imagexdemo.model.ImageTabType
import com.bytedance.imagexdemo.model.ImageType
import com.bytedance.imagexdemo.model.JPEG
import com.bytedance.imagexdemo.utils.CacheConfig
import com.bytedance.imagexdemo.utils.DPUtils
import com.bytedance.imagexdemo.utils.ImageLoader
import com.bytedance.imagexdemo.utils.SimpleRequestListener
import com.facebook.common.logging.FLog
import com.facebook.drawee.generic.RoundingParams
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.listener.BaseRequestListener
import com.facebook.imagepipeline.request.ImageRequest


typealias ImageClickListener = (ImageDataBean, String?) -> Unit

/**
 * 图片列表 Adapter
 */
class ImageAdapter(
    @ImageTabType val tabType: Int,
    @ImageType val imageType: Int,
    private var ignoreMemoryCache: Boolean,
    private var ignoreDisCache: Boolean
) :
    ListAdapter<ImageDataBean, ImageAdapter.ViewHolder>(ImageDiffCallback()) {

    private var mResizeOptions: ResizeOptions? = null

    private var requestMap: SparseArray<String?> = SparseArray()


    private var mClickListener: ImageClickListener? = null
    fun setImageItemClickListener(clickListener: ImageClickListener) {
        mClickListener = clickListener
    }

    fun reloadCacheSettings(ignoreMemoryCache: Boolean, ignoreDisCache: Boolean) {
        this.ignoreMemoryCache = ignoreMemoryCache
        this.ignoreDisCache = ignoreDisCache
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResizeOptions(itemSize: Int) {
        if (mResizeOptions == null) {
            mResizeOptions = ResizeOptions(itemSize, itemSize)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ImageDataBean) {

            // 用户自定义埋点上报参数
            val customParam: MutableMap<String, String> = HashMap()
            customParam["key1"] = "value1"
            customParam["key2"] = "value2"

            requestMap.remove(adapterPosition)
            ImageLoader.commonLoadImage(
                model.url,
                true,
                binding.simpleDraweeView,
                mResizeOptions,
                imageType,
                RoundingParams().apply {
                    setCornersRadius(DPUtils.dp2Px(4f).toFloat())
                    // 如果不设置 paintFilterBitmap 可能会造成图片模糊。
                    paintFilterBitmap = true
                },
                fadeDuration = if (tabType == IMAGE_TAB_TYPE_PROGRESSIVE) 500 else null,
                heicProgressive = imageType == HEIC && tabType == IMAGE_TAB_TYPE_PROGRESSIVE,
                jpegProgressive = imageType == JPEG && tabType == IMAGE_TAB_TYPE_PROGRESSIVE,
                aWebPProgressive = imageType == AWEBP && tabType == IMAGE_TAB_TYPE_PROGRESSIVE,
                customParam,
                ignoreMemoryCache = ignoreMemoryCache,
                ignoreDiscCache = ignoreDisCache,
                requestListener = object : SimpleRequestListener() {
                    override fun onRequestFinish(
                        requestId: String?
                    ) {
                        requestMap.put(adapterPosition, requestId ?: "")
                    }
                }
            )

            itemView.setOnClickListener {
                mClickListener?.invoke(model, requestMap.get(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * 图片DiffUtil类
 */
class ImageDiffCallback : DiffUtil.ItemCallback<ImageDataBean>() {
    override fun areItemsTheSame(oldItem: ImageDataBean, newItem: ImageDataBean): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ImageDataBean, newItem: ImageDataBean): Boolean {
        return oldItem == newItem
    }
}