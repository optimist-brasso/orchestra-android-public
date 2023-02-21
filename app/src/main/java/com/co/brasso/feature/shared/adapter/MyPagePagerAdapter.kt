package com.co.brasso.feature.shared.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.co.brasso.feature.landing.myPage.tab.MyPageMoreFragmentTab
import com.co.brasso.feature.landing.myPage.tab.MyPageProfileFragmentTab
import com.co.brasso.feature.landing.myPage.tab.MyPageSettingFragmentTab


class MyPagePagerAdapter(
    private var fragmentList: MutableList<Fragment>?, activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return fragmentList?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList?.get(position) ?: Fragment()
    }
}