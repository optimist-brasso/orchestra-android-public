package com.co.brasso.feature.shared.adapter.purchaseListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.adapter.viewholder.ConductorPurchaseListViewHolder
import com.co.brasso.feature.shared.model.response.purchaseList.ConductorItem

class ConductorPurchaseListAdapter(
    var list: List<ConductorItem>?,
    var itemClicked: (position: Int?) -> Unit
) : RecyclerView.Adapter<ConductorPurchaseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConductorPurchaseListViewHolder {
        val binding = AdapterPurchaseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConductorPurchaseListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConductorPurchaseListViewHolder, position: Int) {
        holder.bindData(list?.get(position),itemClicked)
    }

    override fun getItemCount() = list?.size ?: 0

}