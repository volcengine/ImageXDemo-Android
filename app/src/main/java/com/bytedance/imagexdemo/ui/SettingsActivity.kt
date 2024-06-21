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

import android.content.DialogInterface
import android.widget.EditText
import com.bytedance.fresco.cloudcontrol.CloudControl
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.ActivitySettingsBinding
import com.bytedance.imagexdemo.datastore.SPUtil
import com.bytedance.imagexdemo.ui.base.BaseActivity
import com.bytedance.imagexdemo.utils.CacheConfig
import com.bytedance.imagexdemo.utils.ClipboardUtils
import com.bytedance.imagexdemo.utils.ImageXInitHelper
import com.bytedance.imagexdemo.utils.toast
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * settings page
 */
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    override fun initViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun initViews() {

        showCache(showMemoryCache = true, showDiscCache = true)

        binding.swIgnoreMemoryCache.isChecked = SPUtil.commPreference.ignoreMemoryCache

        binding.swIgnoreDiscCache.isChecked = SPUtil.commPreference.ignoreDisCache

        initListeners()
    }

    private fun initListeners() {
        // 修改appId
        binding.settingItemAppId.setOnActionClick {
            val et = EditText(this)
            et.setText(SPUtil.commPreference.appId)
            MaterialAlertDialogBuilder(this).setMessage(R.string.input_app_id)
                .setView(et)
                .setPositiveButton(R.string.sure) { _, _ ->
                    val appId = et.text?.toString()
                    if (appId.isNullOrEmpty()) {
                        toast(R.string.input_app_id)
                        return@setPositiveButton
                    }
                    SPUtil.commPreference.appId = appId
                    ImageXInitHelper.initAppLog(
                        this@SettingsActivity.applicationContext,
                        appId,
                        "debug"
                    )
                    toast(R.string.setting_finished)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        // 云控配置
        binding.settingItemCloudControl.setOnActionClick {
            val cloudContent = CloudControl.getCloudControlConfig()
            // 查看云控配置
            MaterialAlertDialogBuilder(this)
                .setMessage(cloudContent)
                .setPositiveButton(
                    R.string.copy
                ) { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        // 复制云控数据到粘贴板
                        ClipboardUtils.copyToClipboard(this, cloudContent)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }


        // 磁盘缓存
        binding.settingItemDiscCache.setOnActionClick {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.clear_cache_warning)
                .setPositiveButton(
                    R.string.sure
                ) { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        // clear the cache
                        Fresco.getImagePipeline().clearDiskCaches()
                        toast(R.string.disc_cache_clear_finished)
                        showCache(showDiscCache = true, forceZeroDisc = true)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }


        // 内存缓存
        binding.settingItemMemoryCache.setOnActionClick {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.clear_memory_cache_warning)
                .setPositiveButton(
                    R.string.sure
                ) { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        // clear the cache
                        Fresco.getImagePipeline().clearMemoryCaches()
                        toast(R.string.memory_cache_clear_finished)
                        showCache(showMemoryCache = true)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        // 忽略内存缓存
        binding.swIgnoreMemoryCache.setOnCheckedChangeListener { _, isChecked ->
            SPUtil.commPreference.ignoreMemoryCache = isChecked
            CacheConfig.cacheConfigChangeVersion++
        }

        // 忽略磁盘缓存
        binding.swIgnoreDiscCache.setOnCheckedChangeListener { _, isChecked ->
            SPUtil.commPreference.ignoreDisCache = isChecked
            CacheConfig.cacheConfigChangeVersion++
        }
    }

    /**
     * 显示缓存数据
     * @param forceZeroDisc disc缓存是否强制显示为0
     * @param showMemoryCache 是否显示内存缓存数据
     * @param showDiscCache 是否显示磁盘缓存
     */
    private fun showCache(
        showMemoryCache: Boolean = false,
        showDiscCache: Boolean = false,
        forceZeroDisc: Boolean = false
    ) {
        if (showMemoryCache) {
            showMemoryCache()
        }
        if (showDiscCache) {
            showDiscCache(forceZeroDisc)
        }
    }

    private fun showDiscCache(forceZeroDisc: Boolean) {
        val diskSizeByte =
            Fresco.getImagePipelineFactory().mainFileCache.size + Fresco.getImagePipelineFactory().smallImageFileCache.size
        if (forceZeroDisc || diskSizeByte <= 0) {
            binding.settingItemDiscCache.setSettingDataValue("0MB")
            return
        }
        val diskSizeMB = diskSizeByte / (1024 * 1.0) / (1024 * 1.0)
        val discSize = "%.2f".format(diskSizeMB)
        binding.settingItemDiscCache.setSettingDataValue("${discSize}MB")
    }

    private fun showMemoryCache() {
        val memorySizeByte =
            Fresco.getImagePipelineFactory().bitmapCountingMemoryCache.sizeInBytes + Fresco.getImagePipelineFactory().encodedCountingMemoryCache.sizeInBytes
        if (memorySizeByte <= 0) {
            binding.settingItemMemoryCache.setSettingDataValue("0MB")
            return
        }
        val memorySizeMB = memorySizeByte / (1024 * 1.0) / (1024 * 1.0)
        val memorySize = "%.2f".format(memorySizeMB)
        binding.settingItemMemoryCache.setSettingDataValue("${memorySize}MB")
    }

}