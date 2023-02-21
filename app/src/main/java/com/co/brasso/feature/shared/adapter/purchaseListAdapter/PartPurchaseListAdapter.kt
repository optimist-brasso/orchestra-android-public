package com.co.brasso.feature.shared.adapter.purchaseListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.adapter.viewholder.PartPurchaseListViewHolder
import com.co.brasso.feature.shared.model.response.purchaseList.PartItem

class PartPurchaseListAdapter(
    var list: MutableList<PartItem>?,
    var itemClicked: (position: Int?) -> Unit
) : RecyclerView.Adapter<PartPurchaseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartPurchaseListViewHolder {
        val binding = AdapterPurchaseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PartPurchaseListViewHolder(binding,itemClicked)
    }

    override fun onBindViewHolder(holder: PartPurchaseListViewHolder, position: Int) {
        holder.bindData(list?.get(position))
    }

    override fun getItemCount() = list?.size ?: 0

}