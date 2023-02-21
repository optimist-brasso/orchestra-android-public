package com.co.brasso.feature.shared.adapter.favourite

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FavouritePagerAdapter(var fragmentList: MutableList<Fragment>?, activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = fragmentList?.size ?: 0

    override fun createFragment(position: Int) = fragmentList?.get(position) ?: Fragment()

}