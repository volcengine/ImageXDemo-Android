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

package com.bytedance.imagexdemo.ui


import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.ActivityImageDetailBinding
import com.bytedance.imagexdemo.datastore.SPUtil
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_ANIMATION_CONTROL
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_DECODE
import com.bytedance.imagexdemo.model.ImageLogCommonData
import com.bytedance.imagexdemo.ui.base.BaseActivity
import com.bytedance.imagexdemo.utils.ClipboardUtils.copyToClipboard
import com.bytedance.imagexdemo.utils.ImageLoader
import com.bytedance.imagexdemo.utils.setVisible
import com.facebook.fresco.animation.drawable.AnimatedDrawable2
import com.facebook.imagepipeline.common.ResizeOptions


/**
 * 图片详情页
 */
class ImageDetailActivity : BaseActivity<ActivityImageDetailBinding>() {

    companion object {
        private const val TAG = "ImageDetailActivity"
        const val IMAGE_URL = "url"
        const val IMAGE_LOG = "imageLog"
        const val IMAGE_TAB = "imageTab"
        const val IMAGE_COMMON_LOG_DATA = "imageCommonLogData"
    }

    private var url: String? = null

    private var imageLog: String? = null

    private var imageTab: Int = IMAGE_TAB_TYPE_DECODE

    private var commonImageData: ImageLogCommonData? = null

    private var isPlaying = true

    private var ignoreMemoryCache = false
    private var ignoreDisCache = false

    override fun initViewBinding(): ActivityImageDetailBinding {
        return ActivityImageDetailBinding.inflate(layoutInflater)
    }

    override fun initData() {
        url = intent?.getStringExtra(IMAGE_URL)
        imageLog = intent?.getStringExtra(IMAGE_LOG)
        imageTab = intent?.getIntExtra(IMAGE_TAB, IMAGE_TAB_TYPE_DECODE) ?: IMAGE_TAB_TYPE_DECODE
        commonImageData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(IMAGE_COMMON_LOG_DATA, ImageLogCommonData::class.java)
        } else {
            intent?.getParcelableExtra(IMAGE_COMMON_LOG_DATA)
        }
        ignoreMemoryCache = SPUtil.commPreference.ignoreMemoryCache
        ignoreDisCache = SPUtil.commPreference.ignoreDisCache
    }

    override fun initViews() {
        val url = url ?: return
        binding.llControl.setVisible(imageTab == IMAGE_TAB_TYPE_ANIMATION_CONTROL)
        binding.simpleDraweeView.post {
            if (isDestroyed || isFinishing || TextUtils.isEmpty(url)) {
                return@post
            }

            val imageSize = binding.simpleDraweeView.height
            val resizeOptions = ResizeOptions(imageSize, imageSize)

            // 加载图片
            ImageLoader.commonLoadImage(
                url,
                true,
                binding.simpleDraweeView,
                resizeOptions,
                ignoreMemoryCache = ignoreMemoryCache,
                ignoreDiscCache = ignoreDisCache
            )
        }

        val basicBinding = binding.basicInfo

        commonImageData?.let {
            basicBinding.imageUrl.setItemDetail(it.url)
            basicBinding.imageUrl.setOnActionClick {
                copyToClipboard(this, it.url)
            }
            basicBinding.imageType.setSettingDataValue(it.imageType)
            basicBinding.fileSize.setSettingDataValue(it.fileSize)
            basicBinding.imageLoadSource.setSettingDataValue(it.loadSource)
            basicBinding.imageResolution.setSettingDataValue(it.resolution)
            basicBinding.networkDuration.setSettingDataValue(it.networkDuration)
            basicBinding.decodeDuration.setSettingDataValue(it.decodeDuration)
            basicBinding.loadStatus.setSettingDataValue(it.loadStatus)
            if (it.loadStatus != "success") {
                basicBinding.errorCode.setSettingDataValue(it.errorCode)
                basicBinding.errorInfo.setItemDetail(it.errorDes)
                basicBinding.errorCode.setVisible(true)
                basicBinding.errorInfo.setVisible(true)
            } else {
                basicBinding.errorCode.setVisible(false)
                basicBinding.errorInfo.setVisible(false)
            }
        }

        val logBinding = binding.logInfo
        logBinding.loadMonitor.setOnActionClick {
            copyToClipboard(this, imageLog)
        }
        logBinding.tvLogInfo.text = imageLog
        if (TextUtils.isEmpty(imageLog)) {
            logBinding.tvLogInfo.visibility = View.GONE
        }
        logBinding.loadMonitor.setOnClickListener {
            if (imageLog.isNullOrEmpty()) {
                return@setOnClickListener
            }
            val textVisible = logBinding.tvLogInfo.visibility == View.VISIBLE
            logBinding.tvLogInfo.visibility = if (textVisible) View.GONE else View.VISIBLE
        }

        binding.tvPlayPause.setOnClickListener {
            val animate = binding.simpleDraweeView.controller?.animatable
            if (!isPlaying) {
                animate?.start()
            } else {
                if (animate is AnimatedDrawable2) {
                    animate.pause()
                }
            }
            isPlaying = !isPlaying
            binding.tvPlayPause.text = getString(if (isPlaying) R.string.pause else R.string.play)
            binding.tvPlayPause.setTextColor(
                if (isPlaying) ContextCompat.getColor(
                    this,
                    R.color.color_111214
                ) else ContextCompat.getColor(this, R.color.color_main)
            )
        }

        binding.tvStop.setOnClickListener {
            val animate = binding.simpleDraweeView.controller?.animatable
            animate?.stop()
            binding.tvPlayPause.text = getString(R.string.play)
            binding.tvPlayPause.setTextColor(ContextCompat.getColor(this, R.color.color_main))
            isPlaying = false
        }
    }
}