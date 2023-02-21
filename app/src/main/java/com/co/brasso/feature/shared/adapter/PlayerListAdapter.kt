package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPlayerListBinding
import com.co.brasso.databinding.LayoutLoadingBinding
import com.co.brasso.feature.shared.PlayerListClick
import com.co.brasso.feature.shared.adapter.viewholder.LoadingViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.PlayerListViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.constant.Constants

class PlayerListAdapter(
    private var playerListResponse: List<PlayerDetailResponse>?,
    var listner: PlayerListClick,
    var onLastIndexReached: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            Constants.playerListViewType -> {
                val binding =
                    AdapterPlayerListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return PlayerListViewHolder(binding, listner)
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
                holder as PlayerListViewHolder
                holder.bindData(playerListResponse?.get(position))
            }
            else -> {
                populateProgress(position)
            }
        }
    }

    override fun getItemCount() = playerListResponse?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (playerListResponse?.get(position)?.viewType) {
            Constants.playerListView -> Constants.playerListViewType
            else -> Constants.showLoadingSection
        }
    }

    private fun populateProgress(position: Int) {
        if (position == ((playerListResponse?.count()
                ?: 0) - 1) && playerListResponse?.lastOrNull()?.hasNext == true
        ) {
            onLastIndexReached()
        }
    }
}