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
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.OperateInfoItemViewLayoutBinding

/**
 * 设置/操作复合组件
 */
class OperateInfoItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: OperateInfoItemViewLayoutBinding

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.OperateInfoItemView)
        val name = typedArray.getString(R.styleable.OperateInfoItemView_name)
        val actionBtColor = typedArray.getColor(
            R.styleable.OperateInfoItemView_action_bt_color,
            ContextCompat.getColor(getContext(), R.color.color_1664ff)
        )
        val content =
            typedArray.getString(R.styleable.OperateInfoItemView_content_info)
        val data = typedArray.getString(R.styleable.OperateInfoItemView_data)
        val action = typedArray.getString(R.styleable.OperateInfoItemView_action)
        val detail = typedArray.getString(R.styleable.OperateInfoItemView_detail)
        typedArray.recycle()
        binding = OperateInfoItemViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        binding.tvName.text = name
        binding.tvInfo.text = content
        binding.tvData.text = data
        binding.tvAction.text = action
        binding.tvAction.setTextColor(actionBtColor)
        setItemDetail(detail)
    }

    /**
     * 设置详细信息
     */
    fun setItemDetail(detail: String?) {
        if (TextUtils.isEmpty(detail)) {
            binding.tvDetail.visibility = View.GONE
            return
        }
        binding.tvDetail.visibility = View.VISIBLE
        binding.tvDetail.text = detail
    }

    /**
     * 设置项数据详情
     */
    fun setSettingDataValue(dataValue: String?) {
        dataValue ?: return
        binding.tvData.text = dataValue
    }

    /**
     * 设置action点击监听
     */
    fun setOnActionClick(actionClick: () -> Unit) {
        binding.tvAction.setOnClickListener {
            actionClick.invoke()
        }
    }

}