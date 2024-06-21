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

package com.bytedance.imagexdemo.ui.fragment.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.DialogInputUrlBinding
import com.bytedance.imagexdemo.model.IMAGE_TYPE_UNKNOWN
import com.bytedance.imagexdemo.model.ImageType
import com.bytedance.imagexdemo.ui.base.BaseDialogFragment
import com.bytedance.imagexdemo.utils.toast
import com.bytedance.imagexdemo.viewmodel.ImageViewModel
import com.bytedance.imagexdemo.viewmodel.ImageViewModelFactory


/**
 * url 输入弹窗
 */
class UrlInputDialogFragment : BaseDialogFragment<DialogInputUrlBinding>(
    inflate = { layoutInflater, viewGroup, attach ->
        DialogInputUrlBinding.inflate(layoutInflater, viewGroup, attach)
    }
) {

    companion object {
        private const val IMAGE_TYPE = "imageType"
        private const val URL_INPUT_DIALOG = "urlInputDialog"
        private fun newInstance(@ImageType imageType: Int): UrlInputDialogFragment {
            return UrlInputDialogFragment().apply {
                val bundle = Bundle()
                bundle.putInt(IMAGE_TYPE, imageType)
                arguments = bundle
            }
        }

        @SuppressLint("CommitTransaction")
        fun showUrlInputDialog(
            activity: FragmentActivity,
            @ImageType imageType: Int
        ): UrlInputDialogFragment {
            val ft: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            val prev: Fragment? =
                activity.supportFragmentManager.findFragmentByTag(URL_INPUT_DIALOG)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val instance = newInstance(imageType)
            instance.show(ft, URL_INPUT_DIALOG)
            return instance
        }
    }

    private var imageViewModel: ImageViewModel? = null

    fun setImageViewModel(viewModel: ImageViewModel) {
        imageViewModel = viewModel
    }

    override suspend fun initView() {
        mBinding.tvCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        mBinding.tvSure.setOnClickListener {
            val urls = getUrls()
            urls ?: return@setOnClickListener
            // 提交到 ViewModel中
            imageViewModel?.appendUrls(urls)
            dismissAllowingStateLoss()
        }
    }


    /**
     * 从输入框中提取输入的url列表
     */
    private fun getUrls(): List<String>? {
        val urls = mBinding.etUrl.text?.toString()
        if (urls.isNullOrEmpty()) {
            context?.toast(R.string.please_input_url)
            return null
        }
        val urlArray = urls.split(",")
        urlArray.forEach {
            if (!isUri(it)) {
                context?.toast(R.string.please_input_legal_url)
                return null
            }
        }
        return urlArray.toList()
    }


    /**
     * 判断uri的格式合法性
     */
    private fun isUri(str: String): Boolean {
        val regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
        return str.matches(regex.toRegex())
    }

}