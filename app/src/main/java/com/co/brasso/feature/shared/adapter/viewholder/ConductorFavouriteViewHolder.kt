package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterConductorFavouriteBinding

class ConductorFavouriteViewHolder(
    itemView: AdapterConductorFavouriteBinding,
    favouriteClicked: (position: Int?) -> Unit,
    itemClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(itemView.root) {
    init {
        itemView.imvFavourite.setOnClickListener {
            favouriteClicked(absoluteAdapterPosition)
        }

        itemView.rltMainLayout.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
    }

    var imvFavouriteOrchestra = itemView.imvFavouriteOrchestra
    var txvLength = itemView.txvLength
    var txvTitle = itemView.txvTitle
    var txvTitleJp = itemView.txvTitleJp
    var txvDescription = itemView.txvDescription
    var txvPremium = itemView.txvPremium
    var imvFavourite = itemView.imvFavourite
}