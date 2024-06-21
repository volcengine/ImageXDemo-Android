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

package com.bytedance.imagexdemo

import android.app.Application
import com.bytedance.imagexdemo.utils.ImageXInitHelper
import com.optimize.statistics.ImageTraceListener
import org.json.JSONObject
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * ImageX Application
 */
class ImageXApplication : Application(), ImageTraceListener {

    companion object {
        private var instance: ImageXApplication by Delegates.notNull()
        fun instance() = instance
    }

    private val traceListenerList = ArrayList<WeakReference<ImageTraceListener>>()

    /**
     * 添加性能日志监听
     */
    fun addTraceListener(imageTraceListener: ImageTraceListener) {
        traceListenerList.add(WeakReference(imageTraceListener))
    }

    /**
     * 移除性能日志监听
     */
    fun removeTraceListener(imageTraceListener: ImageTraceListener) {
        val result = traceListenerList.first {
            it.get() == imageTraceListener
        }
        traceListenerList.remove(result)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ImageXInitHelper.imageXCommonInit(
            this
        )
    }

    override fun onImageLoaded(isSuccess: Boolean, requestId: String?, jsonObj: JSONObject?) {
        traceListenerList.forEach {
            it.get()?.onImageLoaded(isSuccess, requestId, jsonObj)
        }
    }
}