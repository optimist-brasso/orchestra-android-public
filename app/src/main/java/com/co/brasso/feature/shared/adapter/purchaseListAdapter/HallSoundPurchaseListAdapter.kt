package com.co.brasso.feature.shared.adapter.purchaseListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.adapter.viewholder.HallSoundPurchaseListViewHolder
import com.co.brasso.feature.shared.model.response.purchaseList.HallSoundItem

class HallSoundPurchaseListAdapter(
    var list: List<HallSoundItem>?,
    var itemClicked: (position: Int?) -> Unit
) : RecyclerView.Adapter<HallSoundPurchaseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HallSoundPurchaseListViewHolder {
        val binding = AdapterPurchaseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HallSoundPurchaseListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HallSoundPurchaseListViewHolder, position: Int) {
        holder.bindData(list?.get(position),itemClicked)
    }

    override fun getItemCount() = list?.size ?: 0

}