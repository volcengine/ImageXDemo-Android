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

package com.bytedance.imagexdemo.utils

import android.net.Uri
import com.bytedance.imagexdemo.model.GIF
import com.bytedance.imagexdemo.model.IMAGE_TYPE_UNKNOWN
import com.bytedance.imagexdemo.model.ImageType
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.listener.BaseRequestListener
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder


object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun commonLoadImage(
        url: String,
        autoPlay: Boolean = false,
        simpleDraweeView: SimpleDraweeView,
        resizeOptions: ResizeOptions? = null,
        @ImageType imageType: Int = IMAGE_TYPE_UNKNOWN,
        roundParams: RoundingParams? = null,
        // 和渐进式加载并无关系，设置它是为了使渐进式效果看上去更明显。
        fadeDuration: Int? = null,
        // 是否开启 heic 渐进式
        heicProgressive: Boolean = false,
        // 是否开启 jpeg 渐进式
        jpegProgressive: Boolean = false,
        // 是否开启 aWebP 动图渐进式
        aWebPProgressive: Boolean = false,
        customParam: MutableMap<String, String>? = null,
        ignoreMemoryCache: Boolean = false,
        ignoreDiscCache: Boolean = false,
        // 请求监听器
        requestListener: RequestListener? = null
    ) {
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
            .setResizeOptions(resizeOptions).apply {
                if (imageType == GIF) {
                    cacheChoice = ImageRequest.CacheChoice.SMALL
                }
            }.apply {
                if (ignoreMemoryCache) {
                    disableMemoryCache()
                }
                if (ignoreDiscCache) {
                    disableDiskCache()
                }
                if (requestListener != null) {
                    setRequestListener(requestListener)
                }
            }
            .setProgressiveRenderingAnimatedEnabled(aWebPProgressive)
            .setProgressiveRenderingHeicEnabled(heicProgressive)
            .setProgressiveRenderingEnabled(jpegProgressive)
            .setCustomParam(customParam)
            .build()
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .setOldController(simpleDraweeView.controller)
            .setAutoPlayAnimations(autoPlay)
            .build()
        simpleDraweeView.hierarchy.roundingParams = roundParams
        if (fadeDuration != null) {
            simpleDraweeView.hierarchy.fadeDuration = fadeDuration.toInt()
        }
        simpleDraweeView.controller = controller
    }
}

open class SimpleRequestListener : BaseRequestListener() {

    override fun onRequestSuccess(request: ImageRequest?, requestId: String?, isPrefetch: Boolean) {
        onRequestFinish(requestId)
    }

    override fun onRequestFailure(
        request: ImageRequest?,
        requestId: String?,
        throwable: Throwable?,
        isPrefetch: Boolean
    ) {
        onRequestFinish(requestId)
    }

    override fun onRequestCancellation(requestId: String?) {
        onRequestFinish(requestId)
    }

    open fun onRequestFinish(requestId: String?) {

    }
}