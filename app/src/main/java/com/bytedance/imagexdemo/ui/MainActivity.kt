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


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.ActivityMainBinding
import com.bytedance.imagexdemo.model.AWEBP
import com.bytedance.imagexdemo.model.BMP
import com.bytedance.imagexdemo.model.GIF
import com.bytedance.imagexdemo.model.HEIC
import com.bytedance.imagexdemo.model.HEIF
import com.bytedance.imagexdemo.model.ICO
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_ANIMATION_CONTROL
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_DECODE
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_PROGRESSIVE
import com.bytedance.imagexdemo.model.ImageFuncBean
import com.bytedance.imagexdemo.model.ImageTypeBean
import com.bytedance.imagexdemo.model.JPEG
import com.bytedance.imagexdemo.model.PNG
import com.bytedance.imagexdemo.model.WEBP
import com.bytedance.imagexdemo.ui.base.BaseActivity
import com.bytedance.imagexdemo.ui.fragment.ImageTypesFragment
import com.bytedance.imagexdemo.utils.jumpToActivity
import com.google.android.material.tabs.TabLayoutMediator


const val TAB_TAG = "tabTag"
const val TAB_DECODE = 0
const val TAB_PROGRESSIVE_LOAD = 1
const val TAB_ANIMATE_IMAGE_CONTROL = 2

/**
 * 主页面
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val tabs by lazy {
        arrayOf(
            getString(R.string.image_decode),
            getString(R.string.progressive_load),
            getString(R.string.animate_image_control)
        )
    }
    val funcTag by lazy {
        arrayOf(
            // 图片解码
            ImageFuncBean(
                IMAGE_TAB_TYPE_DECODE,
                tabs[0], listOf(
                    ImageTypeBean(PNG, "png"),
                    ImageTypeBean(JPEG, "jpeg"),
                    ImageTypeBean(GIF, "gif"),
                    ImageTypeBean(WEBP, "webp"),
                    ImageTypeBean(HEIC, "heic"),
                    ImageTypeBean(HEIF, "heif"),
                    ImageTypeBean(AWEBP, "awebp"),
                    ImageTypeBean(BMP, "bmp"),
                    ImageTypeBean(ICO, "ico")
                )
            ),
            // 渐进式
            ImageFuncBean(
                IMAGE_TAB_TYPE_PROGRESSIVE,
                tabs[1], listOf(
                    ImageTypeBean(AWEBP, "awebp"),
                    ImageTypeBean(JPEG, "jpeg"),
                    ImageTypeBean(HEIC, "heic")
                )
            ),
            // 动图播放控制
            ImageFuncBean(
                IMAGE_TAB_TYPE_ANIMATION_CONTROL,
                tabs[2], listOf(
                    ImageTypeBean(AWEBP, "awebp"),
                    ImageTypeBean(GIF, "gif"),
                    ImageTypeBean(HEIF, "heif")
                )
            )
        )
    }

    private val showIndex by lazy {
        intent.getIntExtra(TAB_TAG, TAB_DECODE)
    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        binding.titleView.setOnRightIvClickListener {
            jumpToActivity<SettingsActivity>()
        }

        // 禁用预加载
        binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        // viewPager 绑定 Adapter
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabs.size
            }

            override fun createFragment(position: Int): Fragment {
                return ImageTypesFragment.newInstance(funcTag[position])
            }
        }

        // 绑定 TabLayout 和 ViewPager
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = funcTag[position].name
        }.attach()

        if (showIndex > 0) {
            binding.root.post {
                binding.viewPager.currentItem = showIndex
            }
        }
    }
}