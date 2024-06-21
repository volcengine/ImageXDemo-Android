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

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import com.bytedance.imagexdemo.viewmodel.base.BaseViewModel

/**
 * BaseVMFragment 提供了BaseViewModel模版
 */
abstract class BaseVMFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>() {

    open lateinit var mViewModel: VM

    /**
     * 是否使用宿主（Activity）作为 ViewModelStoreOwner
     */
    open val useHostViewModelScope = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel(useHostViewModelScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
        initData()
    }

    open fun initData() {

    }


    open fun initObserve() {


    }


    private fun initViewModel(
        activityScope: Boolean = false
    ) {
        initViewModel(
            if (activityScope) activity
            else this
        )
    }

    open fun initViewModel(
        storeOwner: ViewModelStoreOwner?
    ) {
        storeOwner ?: return
        mViewModel = ViewModelProvider(
            storeOwner
        )[viewModelClass()]
    }

    abstract fun viewModelClass(): Class<VM>

}
