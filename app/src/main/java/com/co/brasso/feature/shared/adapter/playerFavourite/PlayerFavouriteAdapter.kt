package com.co.brasso.feature.shared.adapter.playerFavourite

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterFavouritePlayerBinding
import com.co.brasso.databinding.LayoutLoadingBinding
import com.co.brasso.feature.shared.adapter.viewholder.LoadingViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.PlayerFavouriteViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.lang.Exception

class PlayerFavouriteAdapter(
    var playerFavouriteList: MutableList<PlayerDetailResponse>?,
    var favouritePlayerClicked: (position: Int?) -> Unit,
    var itemClicked: (position: Int?) -> Unit,
    var onLastIndexReached: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    lateinit var binding: AdapterFavouritePlayerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        when (viewType) {
            Constants.playerListViewType -> {
                binding = AdapterFavouritePlayerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PlayerFavouriteViewHolder(binding, {
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
                holder as PlayerFavouriteViewHolder
                val favouritePlayer = playerFavouriteList?.get(position)
                holder.bindData(favouritePlayer)
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