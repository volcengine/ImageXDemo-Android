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

package com.bytedance.imagexdemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageLogCommonData(
    // 图片url
    val url: String?,
    // 图片类型
    val imageType: String?,
    // 图片大小
    val fileSize: String?,
    // 分辨率
    val resolution: String?,
    /**
     * 图片加载源：
     *          1 : NetworkFetchProducer
     *          2 : PartialDiskCacheProducer
     *          3 : DiskCacheReadProducer
     *          4 : EncodedMemoryCacheProducer
     *          5 : BitmapMemoryCacheGetProducer/BitmapMemoryCacheProducer
     *          6 : PostprocessedBitmapMemoryCacheProducer
     *          7 : IMAGE_ORIGIN_UNKNOWN
     *          0 : IMAGE_ORIGIN_UNKNOWN
     */
    val loadSource: String?,
    // 网络耗时
    val networkDuration: String?,
    // 解码耗时
    val decodeDuration: String?,
    // 加载状态
    val loadStatus: String,
    // 错误码
    val errorCode: String?,
    // 错误描述
    val errorDes: String?
) : Parcelable
