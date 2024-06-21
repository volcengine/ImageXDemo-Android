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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * 带参数跳转的Activity
 *
 * @param [T]            跳转的 Activity
 * @param [data]         携带的数据  可为空
 * @param [requestCode]  请求 code 当不为0时，startActivityForResult
 */
inline fun <reified T : Activity> Context.jumpToActivity(
    data: Bundle? = null,
    requestCode: Int? = 0
) {
    val intent = Intent(this, T::class.java)
    if (data != null) {
        intent.putExtras(data)
    }
    if (requestCode != 0) {
        requestCode?.let { (this as Activity).startActivityForResult(intent, it) }
    } else {
        this.startActivity(intent)
    }

}
