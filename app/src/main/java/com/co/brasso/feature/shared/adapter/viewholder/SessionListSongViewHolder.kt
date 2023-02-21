package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterSessionFavouriteBinding

class SessionListSongViewHolder(
    itemView: AdapterSessionFavouriteBinding,
    var itemClicked: (position: Int) -> Unit,
    var favouriteClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(itemView.root) {
    init {
        itemView.rltMainLayout.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
        itemView.imvFavourite.setOnClickListener {
            favouriteClicked(absoluteAdapterPosition)
        }
    }

    var txvTitle = itemView.txvTitle
    var txvTitleJp = itemView.txvTitleJp
    var txvInstrument = itemView.txvInstrument
    var txvPlayerName = itemView.txvPlayerName
    var imvFavouriteOrchestra = itemView.imvFavouriteOrchestra
    var imvFavourite = itemView.imvFavourite
}