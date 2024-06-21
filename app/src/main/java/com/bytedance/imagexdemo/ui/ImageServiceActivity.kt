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

import android.os.Bundle
import com.bytedance.imagexdemo.databinding.ActivityImageServiceBinding
import com.bytedance.imagexdemo.ui.base.BaseActivity
import com.bytedance.imagexdemo.utils.jumpToActivity

/**
 * 服务页面
 */
class ImageServiceActivity : BaseActivity<ActivityImageServiceBinding>() {

    override fun initViewBinding(): ActivityImageServiceBinding {
        return ActivityImageServiceBinding.inflate(
            layoutInflater
        )
    }

    override fun initViews() {


        binding.btDecode.setOnClickListener {
            jumpToActivity<MainActivity>(data = Bundle().apply {
                putInt(TAB_TAG, TAB_DECODE)
            })
        }

        binding.btAnimateControl.setOnClickListener {
            jumpToActivity<MainActivity>(data = Bundle().apply {
                putInt(TAB_TAG, TAB_ANIMATE_IMAGE_CONTROL)
            })
        }

        binding.btProgressiveLoad.setOnClickListener {
            jumpToActivity<MainActivity>(data = Bundle().apply {
                putInt(TAB_TAG, TAB_PROGRESSIVE_LOAD)
            })
        }

        binding.btSettings.setOnClickListener {
            jumpToActivity<SettingsActivity>()
        }
    }

}