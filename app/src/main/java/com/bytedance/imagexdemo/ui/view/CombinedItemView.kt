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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.CombinedViewLayoutBinding

/**
 * 组合控件
 */
class CombinedItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding: CombinedViewLayoutBinding

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CombinedItemView)
        val ivLeft =
            typedArray.getResourceId(R.styleable.CombinedItemView_iv_left, R.drawable.animated_ic)
        val title = typedArray.getString(R.styleable.CombinedItemView_title)
        val ivRight = typedArray.getResourceId(
            R.styleable.CombinedItemView_iv_right,
            R.drawable.right_arrow_ic
        )
        typedArray.recycle()
        binding = CombinedViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        binding.tvTitle.text = title
        binding.ivLeft.setImageResource(ivLeft)
        binding.ivRight.setImageResource(ivRight)
    }

}