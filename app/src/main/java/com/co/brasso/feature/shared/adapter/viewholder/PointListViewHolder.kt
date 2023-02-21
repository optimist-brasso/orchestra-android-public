package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPointListBinding

class PointListViewHolder(
    itemView: AdapterPointListBinding,
    var itemClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(itemView.root) {
    init {
        itemView.lltBuy.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
    }
    var txvPoints = itemView.txvPoints
    var txvDescription = itemView.txvDescription
    var txvBuy = itemView.txvBuyPrice
}