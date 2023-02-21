package com.co.brasso.feature.shared.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.co.brasso.feature.tab.PagerFragment
import com.co.brasso.feature.shared.model.response.home.Banner
import com.co.brasso.utils.constant.BundleConstant

class BannerPagerAdapter(var list: List<Banner>?, activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return list?.size?:0
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = PagerFragment()
        fragment.arguments = Bundle().apply {
            putSerializable(BundleConstant.banners, list?.get(position))
        }
        return fragment
    }

    }