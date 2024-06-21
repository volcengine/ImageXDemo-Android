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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.imagexdemo.R

object ClipboardUtils {

    /**
     * 辅助内容到粘贴板
     * @param context 上下文对象
     * @param content 需要复制的内容
     */
    fun copyToClipboard(context: Context, content: String?) {
        if (TextUtils.isEmpty(content)) {
            context.toast(R.string.content_empty)
            return
        }
        //获取剪贴板管理器：
        val cm: ClipboardManager =
            context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText(null, content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        context.toast(R.string.already_copy_to_clipboard)
    }
}