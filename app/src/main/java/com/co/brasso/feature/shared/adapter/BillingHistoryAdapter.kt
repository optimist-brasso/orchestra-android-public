package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterBillHistoryHeaderBinding
import com.co.brasso.databinding.AdapterBillingHistoryBinding
import com.co.brasso.feature.shared.ClickListner
import com.co.brasso.feature.shared.adapter.viewholder.BillHistoryHeaderViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.BillingHistoryViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.billinghistory.BillingHistoryResponse
import com.co.brasso.utils.constant.Constants

class BillingHistoryAdapter(
    var list: MutableList<BillingHistoryResponse>?,
    var listener: ClickListner
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {

            Constants.billHistorySection -> {
                val binding = AdapterBillingHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BillingHistoryViewHolder(binding, listener)
            }

            else -> {
                val binding = AdapterBillHistoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BillHistoryHeaderViewHolder(binding)
            }

        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.billHistorySection -> {
                holder as BillingHistoryViewHolder
                holder.bindData(list?.get(position))
            }

            else -> {
                holder as BillHistoryHeaderViewHolder
                holder.bindData(list?.get(position))
            }
        }
    }

    override fun getItemCount() = list?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (list?.get(position)?.viewType) {
            Constants.billHistory -> Constants.billHistorySection
            else -> Constants.billHistoryHeaderSection
        }
    }

}