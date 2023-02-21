package com.co.brasso.feature.landing.home

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.home.Banner
import com.co.brasso.feature.shared.model.response.home.HomeData
import com.co.brasso.feature.shared.model.response.home.Recommendation

interface HomeView : BaseView {
    fun setBanner(banners: List<Banner>?)
    fun setRecommendation(recommendations : MutableList<Recommendation>?)
    fun setHomeData(it: HomeData?)
    fun showProgressBar()
    fun hideProgressBar()
    fun updateNotificationCount()
    fun setBundleList(bundleList: GetBundleInfo)
}