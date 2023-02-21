package com.co.brasso.feature.landing.myPage.tab.myPage.points

import com.android.billingclient.api.ProductDetails
import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.GetBundleInfo

interface PointsListView : BaseView{
    fun showProgressBar()
    fun hideProgressBar()
    fun setBundleList(bundleList: GetBundleInfo)
    fun purchaseSuccess(message: String?)
    fun buyFromInAppPurchase(productDetailsList: MutableList<ProductDetails>?)
}