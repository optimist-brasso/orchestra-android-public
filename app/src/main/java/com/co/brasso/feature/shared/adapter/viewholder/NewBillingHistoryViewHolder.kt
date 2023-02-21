package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import com.co.brasso.R
import com.co.brasso.databinding.NewAdapterBillingHistoryBinding
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.util.DateUtils

class NewBillingHistoryViewHolder(
    var binding: NewAdapterBillingHistoryBinding
) : BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(data: NewBillingHistoryResponse?) {

        if (data?.purchaseDate.isNullOrEmpty()) {
            binding.txvDateOfPurchase.viewGone()
            binding.txvPurchaseDate.viewGone()
        } else {
            binding.txvPurchaseDate.text =
              data?.purchaseDate
        }

        if (data?.price == null) {
            binding.txvPrice.text =
                "¥" + binding.root.context.getString(R.string.zero)
        } else {
            binding.txvPrice.text =
                "¥" + "%,d".format(data.price.toInt())
        }

        if (data?.paidPoint == null) {
            binding.txvPoints.text =
                binding.root.context.getString(R.string.zero) + " " + binding.root.context.getString(
                    R.string.points
                )
        } else {
            binding.txvPoints.text =
                "%,d".format(data.paidPoint.toInt()) + " " + binding.root.context.getString(R.string.points)
        }
    }
}
