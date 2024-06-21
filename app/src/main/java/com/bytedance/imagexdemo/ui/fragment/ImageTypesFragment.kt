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

package com.bytedance.imagexdemo.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.imagexdemo.R
import com.bytedance.imagexdemo.databinding.FragmentImageTypesBinding
import com.bytedance.imagexdemo.model.IMAGE_TAB_TYPE_ANIMATION_CONTROL
import com.bytedance.imagexdemo.model.ImageFuncBean
import com.bytedance.imagexdemo.ui.base.BaseFragment
import com.bytedance.imagexdemo.utils.DPUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * ViewPager fragment
 */
class ImageTypesFragment : BaseFragment<FragmentImageTypesBinding>() {

    companion object {

        private const val IMAGE_FUNC = "imageFunc"

        /**
         * 创建 ImageTypesFragment 实例
         */
        fun newInstance(imageFuncBean: ImageFuncBean): ImageTypesFragment {
            return ImageTypesFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(IMAGE_FUNC, imageFuncBean)
                arguments = bundle
            }
        }
    }

    private val tabBean: ImageFuncBean? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(IMAGE_FUNC, ImageFuncBean::class.java)
        } else {
            arguments?.getParcelable(IMAGE_FUNC)
        }
    }

    override fun initViewBinding(): FragmentImageTypesBinding {
        return FragmentImageTypesBinding.inflate(layoutInflater)
    }


    override fun initViews() {
        val tabBean = tabBean
        tabBean ?: return
        val tabs = tabBean.imageTypes
        val imageTabType = tabBean.imageTabType
        // 禁用预加载
        binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        // viewPager 绑定 Adapter
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabs.size
            }

            override fun createFragment(position: Int): Fragment {
                return ImageFragment.newInstance(imageTabType, tabs[position].imageType)
            }
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTab(tab?.customView as? TextView, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateTab(tab?.customView as? TextView, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        // 绑定 TabLayout 和 ViewPager
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = tabs[position].name
            tab.customView = createTab(tabs[position].name)
        }.attach()
    }

    private fun createTab(name: String): TextView {
        return TextView(context).apply {
            textSize = 13f
            text = name
            setPadding(
                DPUtils.dp2Px(12f),
                DPUtils.dp2Px(5f),
                DPUtils.dp2Px(12f),
                DPUtils.dp2Px(5f)
            )
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.color_86909c))
        }
    }

    private fun updateTab(tab: TextView?, isSelected: Boolean) {
        tab ?: return
        val context = context
        context ?: return
        if (isSelected) {
            tab.setBackgroundResource(R.drawable.image_type_tab)
            tab.setTextColor(ContextCompat.getColor(context, R.color.color_1d2129))
        } else {
            tab.background = null
            tab.setTextColor(ContextCompat.getColor(context, R.color.color_86909c))
        }
    }

}