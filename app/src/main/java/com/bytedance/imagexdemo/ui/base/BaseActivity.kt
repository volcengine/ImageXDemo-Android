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

package com.bytedance.imagexdemo.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bytedance.imagexdemo.utils.StatusBarUtil

/**
 * BaseActivity
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    open val statusBarFontDark: Boolean = true

    open val portraitScreen: Boolean = true

    open lateinit var binding: VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setAndroidNativeLightStatusBar(this, statusBarFontDark)
        binding = initViewBinding()
        beforeOnCreateView()
        setContentView(binding.root)
        StatusBarUtil.setFitsSystemWindows(this, true)
        requestedOrientation = if (portraitScreen)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        initData()
        initViews()
    }

    open fun beforeOnCreateView(){}

    open fun initViews() {}

    open fun initData() {}

    protected abstract fun initViewBinding(): VB
}