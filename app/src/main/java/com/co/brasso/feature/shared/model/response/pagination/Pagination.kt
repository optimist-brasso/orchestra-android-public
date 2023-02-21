package com.co.brasso.feature.shared.model.response.pagination

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("current_page")
    val page: Int? = null,

    @SerializedName("per_page")
    val perPage: Int? = null,

    @SerializedName("total_data")
    val totalData: Int? = null,

    @SerializedName("total_pages")
    val totalPages: Int? = null
)