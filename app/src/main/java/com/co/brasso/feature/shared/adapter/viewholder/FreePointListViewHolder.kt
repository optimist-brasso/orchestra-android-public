package com.co.brasso.feature.shared.adapter.viewholder

import com.co.brasso.databinding.AdapterFreePointListBinding
import com.co.brasso.feature.shared.base.BaseViewHolder

class FreePointListViewHolder(
    itemView: AdapterFreePointListBinding,
    var itemClicked: (position: Int?) -> Unit
) :
    BaseViewHolder(itemView.root) {
    init {
        itemView.rltMainLayout.setOnClickListener {
            itemClicked(bindingAdapterPosition)
        }
    }

    var rltMain = itemView.rltMainLayout
    var txvPoints = itemView.txvPoints
    var txvExpiryDate = itemView.txvExpiryDate
}