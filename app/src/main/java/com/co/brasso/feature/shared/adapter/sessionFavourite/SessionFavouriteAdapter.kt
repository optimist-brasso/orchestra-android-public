package com.co.brasso.feature.shared.adapter.sessionFavourite

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterSessionFavouriteBinding
import com.co.brasso.databinding.LayoutLoadingBinding
import com.co.brasso.feature.shared.adapter.viewholder.LoadingViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.SessionFavouriteViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class SessionFavouriteAdapter(
    var playerFavouriteList: MutableList<FavouriteSessionResponse>?,
    var favouritePlayerClicked: (position: Int?) -> Unit,
    var itemClicked: (position: Int?) -> Unit,
    var onLastIndexReached: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    lateinit var binding: AdapterSessionFavouriteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        when (viewType) {
            Constants.playerListViewType -> {
                binding = AdapterSessionFavouriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SessionFavouriteViewHolder(binding, {
                    favouritePlayerClicked(it)
                }, { itemClicked(it) })
            }

            else -> {
                val binding =
                    LayoutLoadingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.playerListViewType -> {
                holder as SessionFavouriteViewHolder
                val favouritePlayer = playerFavouriteList?.get(position)
                Picasso.get().load(favouritePlayer?.musician?.image)
                    .transform(object : Transformation {
                        override fun transform(source: Bitmap?): Bitmap? {
                            val result =
                                source?.let {
                                    Bitmap.createBitmap(
                                        it,
                                        0,
                                        0,
                                        source.width,
                                        source.height / 2
                                    )
                                }
                            source?.recycle()
                            return result
                        }

                        override fun key(): String {
                            return System.currentTimeMillis().toString()
                        }
                    }).placeholder(R.drawable.ic_playerlist_thumbnail)
                    .into(holder.imvPlayerProfile)

                if (favouritePlayer?.orchestra?.title.isNullOrEmpty())
                    holder.txvSessionName.viewGone()
                else {
                    holder.txvSessionName.viewVisible()
                    holder.txvSessionName.text = favouritePlayer?.orchestra?.title
                }

                if (favouritePlayer?.orchestra?.titleJp.isNullOrEmpty())
                    holder.txvSessionNameJap.viewGone()
                else {
                    holder.txvSessionNameJap.viewVisible()
                    holder.txvSessionNameJap.text = favouritePlayer?.orchestra?.titleJp
                }

                if (favouritePlayer?.musician?.name.isNullOrEmpty())
                    holder.txvPlayerName.viewGone()
                else {
                    holder.txvPlayerName.viewVisible()
                    holder.txvPlayerName.text = favouritePlayer?.musician?.name
                }

                if (favouritePlayer?.instrument?.name.isNullOrEmpty())
                    holder.txvInstrument.viewGone()
                else {
                    holder.txvInstrument.viewVisible()
                    holder.txvInstrument.text = favouritePlayer?.instrument?.name
                }

            }
            else -> {
                populateProgress(position)
            }
        }

    }

    override fun getItemCount() = playerFavouriteList?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (playerFavouriteList?.get(position)?.viewType) {
            Constants.playerListView -> Constants.playerListViewType
            else -> Constants.showLoadingSection
        }
    }

    private fun populateProgress(position: Int) {
        if (position == ((playerFavouriteList?.count()
                ?: 0) - 1) && playerFavouriteList?.lastOrNull()?.hasNext == true
        ) {
            onLastIndexReached()
        }
    }

}