# ImageXDemo-Android

ImageX-Demo-android 是火山引擎视频云 veImageX SDK Android 端的开源 Demo。它基于 BDFresco 的图片 SDK 开发，目前完成了多种图片格式解码、渐进式加载、动图播放控制等基础能力展示，提供了一些示例使用方式，后续会持续迭代。帮助业务侧更快完成图片业务的快速搭建，减少接入过程中遇到的困难。

# 目录结构

```text
|--ImageXDemo-Android
|--|--app     // app 主工程
|--|--gradle     // gradle配置
|--|--|--lib.versions.toml     //版本管理配置文件
|--|--settings.gradle.kts     // 项目工程配置文件
```

# 编译运行
1. <b>Demo 需要设置 AppId 、 AuthCode、Token 才能成功运行，否则一些功能将不可使用。</b> 请联系火山引擎商务获取体验 AppId 、 AuthCode和Token。

设置方式：
> 修改 ImageXDemo-Android/com/bytedance/imagexdemo/utils/ImageXInitHelper.kt
```kotlin
object ImageXInitHelper {

    private const val APP_ID ="your app id"

    private const val TOKEN ="your token"

    private const val AUTH_CODE="your auth code"
    
    // ... 省略
}
```

2. Android Studio 打开 `ImageXDemo-Android` 文件夹，点击运行 `app`.

# Issue

有任何问题可以提交 github issue，我们会定期 check 解决。

# PullRequests

暂不接受 PullRequests。

# 火山引擎 veImageX SDK 官网文档
- [集成准备](https://www.volcengine.com/docs/508/65969)
- [快速开始](https://www.volcengine.com/docs/508/176049)
- [功能接入](https://www.volcengine.com/docs/508/176050)
- [常见问题](https://www.volcengine.com/docs/508/201173)
- [HTTPDNS 接入文档](https://www.volcengine.com/docs/508/78562)
- [ChangeLog](https://www.volcengine.com/docs/508/65963)

# License

```text
Copyright 2024 Beijing Volcano Engine Technology Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```