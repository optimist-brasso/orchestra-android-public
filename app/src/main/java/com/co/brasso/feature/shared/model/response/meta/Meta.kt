package com.co.brasso.feature.shared.model.response.meta

import com.co.brasso.feature.shared.model.response.pagination.Pagination
import com.co.brasso.utils.constant.Constants

data class Meta(
    val copyright: String = Constants.metaCopyright,
    val email: String = Constants.metaEmail,
    val api: MetaApi = MetaApi(),
    val pagination: Pagination? = null
)

data class MetaApi(
    val version: String = Constants.metaApiVersion
)