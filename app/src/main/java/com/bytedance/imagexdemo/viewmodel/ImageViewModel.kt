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

package com.bytedance.imagexdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.imagexdemo.model.AWEBP
import com.bytedance.imagexdemo.model.BMP
import com.bytedance.imagexdemo.model.GIF
import com.bytedance.imagexdemo.model.HEIC
import com.bytedance.imagexdemo.model.HEIF
import com.bytedance.imagexdemo.model.ICO
import com.bytedance.imagexdemo.model.IMAGE_TYPE_UNKNOWN
import com.bytedance.imagexdemo.model.ImageDataBean
import com.bytedance.imagexdemo.model.ImageLogCommonData
import com.bytedance.imagexdemo.model.ImageType
import com.bytedance.imagexdemo.model.JPEG
import com.bytedance.imagexdemo.model.PNG
import com.bytedance.imagexdemo.model.WEBP
import com.bytedance.imagexdemo.model.constant.NULL
import com.bytedance.imagexdemo.viewmodel.base.BaseViewModel
import com.optimize.statistics.FrescoMonitorConst
import com.optimize.statistics.FrescoTraceListener
import org.json.JSONException
import org.json.JSONObject


/**
 * 图片ViewModel
 */
class ImageViewModel(@ImageType val imageType: Int) : BaseViewModel() {

    companion object {

        private const val KB = 1024
        private const val MB = 1024 * KB

        private val heifImages = listOf(
            "http://imagex-sdk.volcimagextest.com/response.heif?params=1",
            "http://imagex-sdk.volcimagextest.com/r.heif?params=2",
            "http://imagex-sdk.volcimagextest.com/response.heif?params=3",
            "http://imagex-sdk.volcimagextest.com/r.heif?params=4",
            "http://imagex-sdk.volcimagextest.com/response.heif?params=5",
            "http://imagex-sdk.volcimagextest.com/r.heif?params=6",
            "http://imagex-sdk.volcimagextest.com/response.heif?params=7",
            "http://imagex-sdk.volcimagextest.com/r.heif?params=8",
            "http://imagex-sdk.volcimagextest.com/response.heif?params=9",
            "http://imagex-sdk.volcimagextest.com/r.heif?params=10"
        )
        private val jpegImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_1.jpeg?params=1",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.jpeg?params=2",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.jpeg?params=3",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.jpeg?params=4",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.jpeg?params=5",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.jpeg?params=6",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.jpeg?params=7",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.jpeg?params=8",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.jpeg?params=9",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.jpeg?params=10"
        )
        private val pngImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_1.png?params=1",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.png?params=2",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.png?params=4",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.png?params=5",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.png?params=6",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.png?params=7",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.png?params=8",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.png?params=9",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.png?params=10",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.png?params=11"
        )
        private val gifImages = listOf(
            "http://imagex-sdk.volcimagextest.com/r.gif?params=1",
            "http://imagex-sdk.volcimagextest.com/response.gif?params=2",
            "http://imagex-sdk.volcimagextest.com/r.gif?params=3",
            "http://imagex-sdk.volcimagextest.com/response.gif?params=4",
            "http://imagex-sdk.volcimagextest.com/r.gif?params=5",
            "http://imagex-sdk.volcimagextest.com/response.gif?params=6",
            "http://imagex-sdk.volcimagextest.com/r.gif?params=7",
            "http://imagex-sdk.volcimagextest.com/response.gif?params=8",
            "http://imagex-sdk.volcimagextest.com/r.gif?params=9",
            "http://imagex-sdk.volcimagextest.com/response.gif?params=10"
        )
        private val heicImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_2_1.heic?params=1",
            "http://imagex-sdk.volcimagextest.com/demo_image_1_1.heic?params=2",
            "http://imagex-sdk.volcimagextest.com/demo_image_2_1.heic?params=3",
            "http://imagex-sdk.volcimagextest.com/demo_image_1_1.heic?params=4",
            "http://imagex-sdk.volcimagextest.com/demo_image_2_1.heic?params=5",
            "http://imagex-sdk.volcimagextest.com/demo_image_1_1.heic?params=6",
            "http://imagex-sdk.volcimagextest.com/demo_image_2_1.heic?params=7",
            "http://imagex-sdk.volcimagextest.com/demo_image_1_1.heic?params=8",
            "http://imagex-sdk.volcimagextest.com/demo_image_2_1.heic?params=9",
            "http://imagex-sdk.volcimagextest.com/demo_image_1_1.heic?params=10"
        )

        private val webPImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_1.webp?params=1",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.webp?params=2",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.webp?params=3",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.webp?params=4",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.webp?params=5",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.webp?params=6",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.webp?params=7",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.webp?params=8",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.webp?params=9",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.webp?params=10"
        )

        private val aWebPImages = listOf(
            "http://imagex-sdk.volcimagextest.com/r.webp?params=1",
            "http://imagex-sdk.volcimagextest.com/response.webp?params=2",
            "http://imagex-sdk.volcimagextest.com/r.webp?params=3",
            "http://imagex-sdk.volcimagextest.com/response.webp?params=4",
            "http://imagex-sdk.volcimagextest.com/r.webp?params=5",
            "http://imagex-sdk.volcimagextest.com/response.webp?params=6",
            "http://imagex-sdk.volcimagextest.com/r.webp?params=7",
            "http://imagex-sdk.volcimagextest.com/response.webp?params=8",
            "http://imagex-sdk.volcimagextest.com/r.webp?params=9",
            "http://imagex-sdk.volcimagextest.com/response.webp?params=10"
        )

        private val bmpImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_1.bmp?params=1",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.bmp?params=2",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.bmp?params=3",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.bmp?params=4",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.bmp?params=5",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.bmp?params=6",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.bmp?params=7",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.bmp?params=8",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.bmp?params=9",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.bmp?params=10"
        )

        private val icoImages = listOf(
            "http://imagex-sdk.volcimagextest.com/demo_image_1.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_1.ico",
            "http://imagex-sdk.volcimagextest.com/demo_image_2.ico"
        )

    }

    val imageListData: MutableLiveData<List<ImageDataBean>> = MutableLiveData()

    fun fetchImageData() {
        when (imageType) {
            HEIF -> setHeifUrls()
            HEIC -> setHeicUrls()
            JPEG -> setJpegUrls()
            PNG -> setPngUrls()
            WEBP -> setWebPUrls()
            AWEBP -> setAWebPUrls()
            GIF -> setGifUrls()
            BMP -> setBmpUrls()
            ICO -> setIcoUrls()
            IMAGE_TYPE_UNKNOWN -> {
                // do nothing
            }
        }
    }

    private fun setIcoUrls() {
        imageListData.value = icoImages.map {
            ImageDataBean(it)
        }
    }

    private fun setBmpUrls() {
        imageListData.value = bmpImages.map {
            ImageDataBean(it)
        }
    }



    private fun setGifUrls() {
        imageListData.value = gifImages.map {
            ImageDataBean(it)
        }
    }

    private fun setAWebPUrls() {
        imageListData.value = aWebPImages.map {
            ImageDataBean(it)
        }
    }

    private fun setWebPUrls() {
        imageListData.value = webPImages.map {
            ImageDataBean(it)
        }
    }

    private fun setPngUrls() {
        imageListData.value = pngImages.map {
            ImageDataBean(it)
        }
    }

    private fun setJpegUrls() {
        imageListData.value = jpegImages.map {
            ImageDataBean(it)
        }
    }

    private fun setHeicUrls() {
        imageListData.value = heicImages.map {
            ImageDataBean(it)
        }
    }

    private fun setHeifUrls() {
        imageListData.value = heifImages.map {
            ImageDataBean(it)
        }
    }

    fun appendUrls(url: List<String>) {
        val list = imageListData.value
        val mutableList = ArrayList<ImageDataBean>().apply {
            addAll(list ?: emptyList())
            addAll(url.map {
                ImageDataBean(it)
            })
        }
        imageListData.value = mutableList
    }

    fun getCommonImageData(jsonObject: JSONObject): ImageLogCommonData? {
        val url = jsonObject.optString(FrescoMonitorConst.URI)
        val fileSize: Long = jsonObject.optLong(FrescoMonitorConst.FILE_SIZE, -1L)
        val imageType = jsonObject.optString(FrescoMonitorConst.IMAGE_TYPE, "IMAGE_TYPE_UNKNOWN")
        val decodeDuration = jsonObject.optLong(FrescoMonitorConst.DECODE_DURATION, -1L)
        val downloadDuration = jsonObject.optLong("download_duration", -1L)

        var imageSource = "0"
        val monitorData = jsonObject.optJSONObject(FrescoTraceListener.IMAGE_MONITOR_DATA)
        if (monitorData != null) {
            imageSource = (monitorData.opt("image_origin") ?: 0).toString()
        }
        val imageResolution = try {
            jsonObject.getString(FrescoMonitorConst.APPLIED_IMAGE_SIZE)
        } catch (e: JSONException) {
            NULL
        }
        val loadStatus = jsonObject.optString(FrescoMonitorConst.LOAD_STATUS)
        val errorCode = jsonObject.optInt(FrescoMonitorConst.ERR_CODE, -1)
        val errorDescription = jsonObject.optString(FrescoMonitorConst.ERR_DESC).let {
            val list = it.split(":")
            if (list.isNotEmpty()) {
                list[0]
            } else {
                ""
            }
        }
        return ImageLogCommonData(
            url,
            imageType,
            getImageSize(fileSize) ?: NULL,
            imageResolution,
            imageSource,
            if (downloadDuration < 0L) NULL else "${downloadDuration}ms",
            if (decodeDuration < 0L) NULL else "${decodeDuration}ms",
            loadStatus ?: NULL,
            if (errorCode < 0) NULL else errorCode.toString(),
            errorDescription
        )
    }


    private fun getImageSize(fileSize: Long): String? {
        var result: String? = null
        val suffix = if (fileSize > MB) {
            result = "%.2f".format(fileSize / (MB * 1.0))
            "M"
        } else if (fileSize > 1024) {
            result = "%.2f".format(fileSize / (KB * 1.0))
            "KB"
        } else if (fileSize > 0L) {
            result = fileSize.toString()
            "B"
        } else {
            return null
        }
        return buildString {
            append(result)
            append(suffix)
        }
    }

}


/**
 * 图片ViewModel工厂类
 */
class ImageViewModelFactory(private val imageType: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            return ImageViewModel(imageType = imageType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}