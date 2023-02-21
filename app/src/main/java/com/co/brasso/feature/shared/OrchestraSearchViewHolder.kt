package com.co.brasso.feature.shared

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterOrchestraSearchBinding

class OrchestraSearchViewHolder(
    itemView: AdapterOrchestraSearchBinding,
    var itemClicked: (position: Int) -> Unit,
    var favouriteClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(itemView.root) {
    init {
        itemView.rllMain.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
        itemView.imvFavourite.setOnClickListener {
            favouriteClicked(absoluteAdapterPosition)
        }
    }

    var txvLength = itemView.txvLength
    var txvTitle = itemView.txvTitle
    var txvTitleJp = itemView.txvTitleJp
    var imvFavouriteOrchestra = itemView.imvFavouriteOrchestra
    var imvFavourite = itemView.imvFavourite
}