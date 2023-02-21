package com.co.brasso.feature.shared.adapter.purchaseListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.adapter.viewholder.PremiumPurchaseListViewHolder
import com.co.brasso.feature.shared.model.response.purchaseList.PremiumItem

class PremiumPurchaseListAdapter(
    var list: List<PremiumItem>?,
    var itemClicked: (position: Int?) -> Unit
) : RecyclerView.Adapter<PremiumPurchaseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PremiumPurchaseListViewHolder {
        val binding = AdapterPurchaseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PremiumPurchaseListViewHolder(binding,itemClicked)
    }

    override fun onBindViewHolder(holder: PremiumPurchaseListViewHolder, position: Int) {
        holder.bindData(list?.get(position))
    }

    override fun getItemCount() = list?.size ?: 0

}