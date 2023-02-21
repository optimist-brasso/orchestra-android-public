package com.co.brasso.feature.shared.adapter.viewholder


import com.co.brasso.databinding.AdapterRecommendationBinding
import com.co.brasso.feature.shared.base.BaseViewHolder

class RecommendationViewHolder(
    itemView: AdapterRecommendationBinding,
    var itemClicked: (position: Int?) -> Unit
) : BaseViewHolder(itemView.root) {

    init {
        itemView.rllMain.setOnClickListener {
            itemClicked(adapterPosition)
        }
    }

    var imvRecommended = itemView.imvRecommendation
    var txvRecommendedTitle = itemView.txvRecommendedTitle
    var txvRecommendedDescription = itemView.txvRecommendedDescription
    var txvRecommendedTime = itemView.txvRecommendedTime
    var txvRecommendedDate = itemView.txvRecommendedDate
    var txvNewLabel = itemView.txvNewLabel
    var txvPrLabel = itemView.txvPrLabel
}