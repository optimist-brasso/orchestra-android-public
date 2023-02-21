package com.co.brasso.feature.player

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse

interface PlayerListFragmentView : BaseView {
    fun success(playerListResponse: MutableList<PlayerDetailResponse>)

    fun showProgressBar()

    fun hideProgressBar()

    fun showErrorLayout()

    fun hideErrorLayout()

    fun setBundleList(bundleList: GetBundleInfo)
}