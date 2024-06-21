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

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bytedance.applog.AppLog
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.ActivitySplashBinding
import com.bytedance.imagexdemo.datastore.SPUtil
import com.bytedance.imagexdemo.model.constant.PRIVACY_PAGE_URL
import com.bytedance.imagexdemo.ui.base.BaseActivity
import com.bytedance.imagexdemo.utils.ImageXInitHelper
import com.bytedance.imagexdemo.utils.jumpToActivity
import com.bytedance.imagexdemo.utils.toast
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Splash and login page
 */
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    companion object {
        private const val REQUEST_READ_STORAGE = 1
    }

    public override fun initViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun beforeOnCreateView() {
        if (null != intent && intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noGranted = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            noGranted.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED
        ) {
            noGranted.add(Manifest.permission.INTERNET)
        }
        if (noGranted.size > 0) {
            ActivityCompat.requestPermissions(this, noGranted.toTypedArray(), 1)
        }
        requestStoragePermission()
    }


    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_STORAGE
        )
    }

    override fun initViews() {

        val appId = SPUtil.commPreference.appId
        binding.etInput.setText(appId)

        binding.tvProtocol.setOnClickListener {
            val uri = Uri.parse(PRIVACY_PAGE_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.btExperienceImmediately.setOnClickListener { _ ->
            if (binding.etInput.text.isNullOrEmpty() || binding.etInput.text.isBlank()) {
                toast(getString(R.string.please_input_app_id_first))
                return@setOnClickListener
            }
            if (!binding.checkbox.isChecked) {
                toast(R.string.user_privacy_agreement_tips)
                return@setOnClickListener
            }

            if (ImageXInitHelper.TOKEN.isEmpty() || ImageXInitHelper.AUTH_CODE.isEmpty()) {
                MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.token_authcode_required))
                    .setPositiveButton(
                        R.string.sure
                    ) { _, _ -> }
                    .show()
                return@setOnClickListener
            }

            val appID = binding.etInput.text
            if (!TextUtils.isEmpty(appID)) {
                SPUtil.commPreference.appId = appID.toString()
                ImageXInitHelper.initAppLog(this, appID.toString(), "debug")
            }
            // 用户同意隐私协议后才能开启。
            AppLog.start()
            jumpToActivity<ImageServiceActivity>()
            finish()
        }
    }

}