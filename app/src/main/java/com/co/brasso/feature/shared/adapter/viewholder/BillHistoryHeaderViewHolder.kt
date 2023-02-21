package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import com.co.brasso.R
import com.co.brasso.databinding.AdapterBillHistoryHeaderBinding
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.billinghistory.BillingHistoryResponse
import com.co.brasso.utils.extension.viewGone

class BillHistoryHeaderViewHolder(var binding: AdapterBillHistoryHeaderBinding) :
    BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(data: BillingHistoryResponse?) {

        if (data?.date.isNullOrEmpty()) {
            binding.tvDate.viewGone()
        } else {
            binding.tvDate.text = data?.date
        }

        if (data?.price == null) {
            binding.tvPrice.text =
                binding.root.context.getString(R.string.zero) + " " + binding.root.context.getString(
                    R.string.points
                )
        } else {
            binding.tvPrice.text =
                "%,d".format(data.price.toInt()) + " " + binding.root.context.getString(R.string.points)
        }
    }

}