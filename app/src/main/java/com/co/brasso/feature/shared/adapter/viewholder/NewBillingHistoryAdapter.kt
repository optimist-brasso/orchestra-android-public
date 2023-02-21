package com.co.brasso.feature.shared.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.NewAdapterBillingHistoryBinding
import com.co.brasso.feature.shared.model.response.billinghistory.BillingHistoryResponse
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse

class NewBillingHistoryAdapter(
    var list: MutableList<NewBillingHistoryResponse>?
) :
    RecyclerView.Adapter<NewBillingHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBillingHistoryViewHolder {
        val binding = NewAdapterBillingHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewBillingHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewBillingHistoryViewHolder, position: Int) {
        holder.bindData(list?.get(position))
    }

    override fun getItemCount() = list?.size ?: 0
}