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

package com.bytedance.imagexdemo.datastore

import android.content.Context
import com.bytedance.imagexdemo.ImageXApplication
import com.bytedance.imagexdemo.model.constant.APP_ID
import com.bytedance.imagexdemo.model.constant.IGNORE_DISC_CACHE_TAG
import com.bytedance.imagexdemo.model.constant.IGNORE_MEMORY_CACHE_TAG
import com.bytedance.imagexdemo.utils.SharedPreferencesDelegate

/**
 * SP代理类
 */
class CommonPreference(context: Context) {

    /**
     * 忽略内存缓存
     */
    var ignoreMemoryCache by SharedPreferencesDelegate(
        context,
        false,
        IGNORE_MEMORY_CACHE_TAG
    )


    /**
     * 忽略磁盘缓存
     */
    var ignoreDisCache by SharedPreferencesDelegate(
        context,
        false,
        IGNORE_DISC_CACHE_TAG
    )

    /**
     * appId
     */
    var appId by SharedPreferencesDelegate(
        context,
        "",
        APP_ID
    )
}

object SPUtil {
    val commPreference = CommonPreference(ImageXApplication.instance())
}
