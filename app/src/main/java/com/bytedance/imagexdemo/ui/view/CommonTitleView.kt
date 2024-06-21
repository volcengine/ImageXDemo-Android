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

package com.bytedance.imagexdemo.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.CommonTitleViewLayoutBinding

/**
 * 标题组件
 */
class CommonTitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CommonTitleViewLayoutBinding

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleView)
        val title = typedArray.getString(R.styleable.CommonTitleView_title)
        val ivRight = typedArray.getResourceId(
            R.styleable.CommonTitleView_iv_right,
            0
        )
        typedArray.recycle()
        binding = CommonTitleViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        binding.tvTitle.text = title
        if (ivRight != 0) {
            binding.ivIc.setImageResource(ivRight)
        }

        binding.ivBack.setOnClickListener {
            (context as? Activity)?.finish()
        }
    }

    fun setOnRightIvClickListener(callback: () -> Unit) {
        binding.ivIc.setOnClickListener {
            callback.invoke()
        }
    }

    /**
     * 设置左侧图标点击事件
     */
    fun setOnExitClickListener(callback: () -> Unit) {
        binding.ivBack.setOnClickListener {
            callback.invoke()
        }
    }

}