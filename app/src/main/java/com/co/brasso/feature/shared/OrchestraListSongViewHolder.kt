package com.co.brasso.feature.shared

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterOrchestraSearchBinding

class OrchestraListSongViewHolder(
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
    var txvDescription = itemView.txvDescription

    fun setFavourite(context: Context?, status: Boolean?) {
        if (context == null)
            return
        if (status == true) {
            imvFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_favourite_selected
                )
            )
        } else {
            imvFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_favourite_session
                )
            )
        }
    }
}