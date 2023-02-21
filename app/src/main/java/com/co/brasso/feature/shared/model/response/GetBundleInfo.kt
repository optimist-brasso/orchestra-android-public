package com.co.brasso.feature.shared.model.response

import com.co.brasso.feature.shared.model.response.bundleList.BundleListResponse
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse

data class GetBundleInfo(
    var profileResponse: MyProfileResponse? = null,
    var products: BundleListResponse? = null
)