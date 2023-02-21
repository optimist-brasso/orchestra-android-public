package com.co.brasso.feature.orchestraPlayer

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse

interface OrchestraPlayerListFragmentView : BaseView {
    fun success(playerListResponse: MutableList<PlayerDetailResponse>)

    fun showProgressBar()

    fun hideProgressBar()

    fun showErrorLayout()

    fun hideErrorLayout()
}