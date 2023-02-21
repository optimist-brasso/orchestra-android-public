package com.co.brasso.feature.shared.adapter.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import com.co.brasso.R
import com.co.brasso.databinding.AdapterSessionFavouriteBinding
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse

class SessionFavouriteViewHolder(
    itemView: AdapterSessionFavouriteBinding,
    var favouritePlayerClicked: (position: Int?) -> Unit,
    var itemClicked: (position: Int?) -> Unit

) : BaseViewHolder(itemView.root) {
    init {
        itemView.imvFavourite.setOnClickListener {
            favouritePlayerClicked(bindingAdapterPosition)
        }

        itemView.rltMainLayout.setOnClickListener {
            itemClicked(bindingAdapterPosition)
        }
    }

    var imvPlayerProfile = itemView.imvFavouriteOrchestra
    var txvSessionName = itemView.txvTitle
    var txvSessionNameJap = itemView.txvTitleJp
    var txvInstrument = itemView.txvInstrument
    var txvPlayerName = itemView.txvPlayerName
    var imvFavourite = itemView.imvFavourite

    fun setFavourite(context: Context,favouritePlayer:FavouriteSessionResponse?){
        if (favouritePlayer?.instrument?.isFavourite == true) {
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