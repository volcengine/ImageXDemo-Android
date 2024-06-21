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

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bytedance.imagexdemo.R
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 基础Dialog封装
 */
abstract class BaseDialogFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : DialogFragment() {

    val TAG by lazy {
        this.javaClass.name
    }

    private var binding: VB? = null
    val mBinding: VB get() = binding!!

    lateinit var mContext: Context

    private var cancel: Boolean = true
    private var gravity: Int = Gravity.CENTER
    private var width: Int = MATCH_PARENT
    private var height: Int = MATCH_PARENT

    @StyleRes
    private var animation: Int = R.style.dialogAnimation

    @StyleRes
    private var style: Int = R.style.DialogTheme

    @DrawableRes
    private var background: Int = android.R.color.transparent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, style)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(animation)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCancelable(cancel)
            setCanceledOnTouchOutside(cancel)
            isCancelable = cancel
        }
        dialog?.window?.apply {
            setLayout(width, height)
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(background)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            initView()
            initData()
        }
    }

    fun initParams(
        cancel: Boolean = this.cancel,
        width: Int = this.width,
        height: Int = this.height,
        @DrawableRes backgroundResId: Int = this.background,
        gravity: Int = this.gravity,
        @StyleRes anim: Int = this.animation,
        @StyleRes style: Int = this.style
    ) = apply {
        this.animation = anim
        this.style = style
        this.cancel = cancel
        this.width = width
        this.height = height
        this.background = backgroundResId
        this.gravity = gravity
    }

    abstract suspend fun initView()

    open suspend fun initData() {}

    override fun onDestroy() {
        binding = null
        lifecycleScope.cancel()
        super.onDestroy()
    }

}
