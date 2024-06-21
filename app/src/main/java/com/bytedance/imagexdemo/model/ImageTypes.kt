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
import androidx.annotation.IntDef
import kotlinx.parcelize.Parcelize

const val HEIF = 1
const val HEIC = 2
const val JPEG = 3
const val PNG = 4
const val WEBP = 5
const val AWEBP = 6
const val GIF = 7
const val BMP = 8
const val ICO = 9

const val IMAGE_TYPE_UNKNOWN = 11

@Retention(AnnotationRetention.SOURCE)
@IntDef(HEIF, HEIC, JPEG, PNG, WEBP, AWEBP, GIF, BMP, ICO, IMAGE_TYPE_UNKNOWN)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD
)
annotation class ImageType


const val IMAGE_TAB_TYPE_DECODE = 1
const val IMAGE_TAB_TYPE_PROGRESSIVE = 2
const val IMAGE_TAB_TYPE_ANIMATION_CONTROL = 3


@Retention(AnnotationRetention.SOURCE)
@IntDef(IMAGE_TAB_TYPE_DECODE, IMAGE_TAB_TYPE_PROGRESSIVE, IMAGE_TAB_TYPE_ANIMATION_CONTROL)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class ImageTabType


@Parcelize
data class ImageTypeBean(@ImageType val imageType: Int, val name: String) : Parcelable

@Parcelize
data class ImageFuncBean(
    @ImageTabType val imageTabType: Int,
    val name: String,
    val imageTypes: List<ImageTypeBean>
) : Parcelable
