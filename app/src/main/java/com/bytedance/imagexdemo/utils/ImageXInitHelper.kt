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

package com.bytedance.imagexdemo.utils

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import com.bytedance.applog.AppLog
import com.bytedance.applog.util.UriConstants
import com.bytedance.fresco.cloudcontrol.CloudControl
import com.bytedance.fresco.cloudcontrol.InitConfig
import com.bytedance.fresco.heif.HeifDecoder
import com.bytedance.fresco.heif.HeifDecoder.HeifFormatChecker
import com.bytedance.fresco.heif.HeifDecoder.HeifFormatDecoder
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.datastore.SPUtil
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.interfaces.DraweePlaceHolderConfig
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.ImageDecoderConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.net.FrescoTTNetFetcher
import com.optimize.statistics.FrescoMonitor
import com.optimize.statistics.FrescoTraceListener
import com.optimize.statistics.ImageTraceListener

/**
 * ImageX 初始化配置类
 */
object ImageXInitHelper {

    private const val APP_ID = ""

    // 如果没有设置，那么弹窗提醒客户
    internal const val TOKEN = ""

    internal const val AUTH_CODE = ""

    private const val CHANNEL = ""

    fun imageXCommonInit(context: Application) {
        val appId = SPUtil.commPreference.appId
        val aId = appId.ifEmpty { APP_ID }
        // 如果您需要使用日志上报扩展功能，则请按照以下顺序初始化 AppLog，并在已获得用户授权并进入业务场景后调用 AppLog.start() 接口手动启动。如果无需使用日志上报请注释或删除 Applog 相关代码。
        initAppLog(context, aId, CHANNEL)
        // 初始化 BDFresco
        initFresco(context, aId)
    }

    fun initAppLog(context: Context, appId: String, channel: String) {
        // 开始初始化 Applog，如果无需使用日志上报扩展功能，请注释或删除 Applog 相关代码

        // appid和渠道，channel 不能为空
        val config =
            com.bytedance.applog.InitConfig(appId, channel)

        // 国内上报地址
        config.setUriConfig(UriConstants.DEFAULT)
        // 是否在控制台输出日志，可用于观察用户行为日志上报情况，上线请关闭该能力
        config.setLogEnable(true)
        config.setLogger { s, _ ->
            //指定为 debug 包才会打印日志
            FLog.d("AppLog------->: ", "" + s)
        }
        //关闭内嵌 H5 页面的无埋点事件
        config.setH5CollectEnable(false)
        // 加密开关。false 为关闭加密，上线时建议设置为 true
        AppLog.setEncryptAndCompress(true)

        // 用户同意隐私协议之前需要设置位 false，
        // 当用户同意了之后可以使用 AppLog.start() 重新开启。
        config.setAutoStart(false)
        AppLog.init(context, config)
        // Applog 初始化结束
    }

    private fun initFresco(context: Application, appId: String) {

        // 注意须保持以下顺序！
        // 初始化云控及授权
        val initConfig = initCloudConfig(context, appId)

        // TTNet
        val frescoTTNetFetcher = FrescoTTNetFetcher(initConfig)
        // 统计功能
        val listeners: MutableSet<RequestListener> = HashSet()
        listeners.add(FrescoTraceListener())
        // 设置监控
        initMonitor(context)
        // 设置渐进式
        initProgressive()
        // 设置网络库
        val imagePipelineBuilder = ImagePipelineConfig.newBuilder(context)
            .setNetworkFetcher(frescoTTNetFetcher)
            .setRequestListeners(listeners) // HEIF功能配置
            .setImageDecoderConfig(
                // 添加 heic 解码器
                ImageDecoderConfig.newBuilder().addDecodingCapability(
                    HeifDecoder.HEIF_FORMAT,
                    HeifFormatChecker(),
                    // true表示优先使用硬解，false表示优先使用软解，推荐设置false
                    HeifFormatDecoder(false)
                ).build()
            )

        val draweeConfigBuilder = DraweeConfig.newBuilder()
            .setDraweePlaceHolderConfig(getDefaultPlaceHolderConfig())
            .setDraweeHierarchyDefaultFadeDuration(100)
        Fresco.initialize(context, imagePipelineBuilder.build(), draweeConfigBuilder.build())
    }

    private fun initCloudConfig(context: Application, appId: String): InitConfig {
        val aid = appId // App ID，应用管理获取
        val deviceId = "" // 设备 ID，根据实际业务填写
        val versionName = "0.0.1" // App 版本号，根据实际业务填写
        val versionCode = "1" // App 版本 code，根据实际业务填写
        val channel = "debug" // 渠道，根据实际业务填写
        val appName = "FrescoSample" // App 名称，业务方 app 名，根据实际业务填写
        val encodedAuthCode: MutableList<String> = ArrayList()
        encodedAuthCode.add(AUTH_CODE)
        val token = TOKEN
        val initConfig = InitConfig(
            context,
            aid,
            appName,
            channel,
            versionName,
            versionCode,
            deviceId,
            InitConfig.CHINA,  //国内版本设置为 CHINA；新加坡地区设置为 SINGAPORE；
            token,  // 传入获取的 Token 值
            encodedAuthCode,// 传入获取的授权码 List<String>
            true,
            false
        )

        CloudControl.init(initConfig)
        return initConfig
    }

    /**
     * 监控相关配置
     */
    private fun initMonitor(context: Application) {
        // 测试阶段建议开启，上线时建议关闭
        FrescoMonitor.setEnableAllSourceUriReport(true)
        // 控制是否打开收集ImageOrigin等信息。该能力依赖 Applog，若未完成初始化与启动 Applog，则无法使用该功能
        FrescoMonitor.setReportImageMonitorDataEnabled(true)
        // 开启大图监控日志上报。该能力依赖 Applog，若未完成初始化与启动 Applog，则无法使用该功能
        FrescoMonitor.setExceedTheLimitBitmapMonitorEnabled(true)

        if (context is ImageTraceListener) {
            // 添加性能日志回捞监听
            FrescoMonitor.addImageTraceListener(context)
        }
    }

    /**
     * 渐进式相关配置
     */
    private fun initProgressive() {
        // 开启静图渐进式（只支持jpeg），从模糊到清晰。
        ImagePipelineConfig.getDefaultImageRequestConfig()
            .isProgressiveRenderingEnabled = true

        // 开启动图渐进式（ 只支持aWebP格式）
        ImagePipelineConfig.getDefaultImageRequestConfig()
            .isProgressiveRenderingAnimatedEnabled = true

        // 开启heic静图渐进式。
        // heic先加载一张低分辨率的图，等大图准备好了，再显示大图。
        ImagePipelineConfig.getDefaultImageRequestConfig()
            .isProgressiveRenderingHeicEnabled = true
    }

    private fun getDefaultPlaceHolderConfig(): DraweePlaceHolderConfig {
        return object : DraweePlaceHolderConfig {
            override fun getPlaceHolderImageDrawableRes(): Int {
                return R.drawable.in_loading_ic
            }

            override fun getPlaceHolderImageColorRes(): Int {
                return 0
            }

            override fun getPlaceHolderDrawable(): Drawable? {
                return null
            }

            override fun getPlaceHolderScaleType(): ScalingUtils.ScaleType {
                return ScalingUtils.ScaleType.CENTER_CROP
            }

            override fun getFailureImageDrawableRes(): Int {
                return R.drawable.loading_error_ic
            }

            override fun getFailureImageColorRes(): Int {
                return 0
            }

            override fun getFailureDrawable(): Drawable? {
                return null
            }

            override fun getFailureScaleType(): ScalingUtils.ScaleType {
                return ScalingUtils.ScaleType.CENTER_CROP
            }
        }
    }

}