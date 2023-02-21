package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.model.response.purchaseList.PartItem
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class PartPurchaseListViewHolder(
    var binding: AdapterPurchaseListBinding,
    itemClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.rllMain.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindData(partItem: PartItem?) {

        Picasso.get().load(partItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvPurchaseList)

        partItem?.duration?.let {
            binding.txvLength.text =
                DateUtils.convertIntToRecordTime(partItem.duration.toLong())
            binding.txvLength.viewVisible()
        }
            ?: kotlin.run { binding.txvLength.viewGone() }

        partItem?.title?.let {
            binding.txvTitle.text = partItem.title
            binding.txvTitleDescription.viewVisible()
        }
            ?: kotlin.run { binding.txvTitle.viewGone() }

        partItem?.titleJp?.let {
            binding.txvTitleDescription.text = partItem.titleJp
            binding.txvTitleDescription.viewVisible()
        }
            ?: kotlin.run { binding.txvTitleDescription.viewGone() }

        partItem?.duration?.let {
            binding.txvTime.text = partItem.instrumentTitle
            binding.txvTime.viewVisible()
        }
            ?: kotlin.run { binding.txvTime.viewGone() }

        partItem?.releaseDate?.let {
            binding.txvDate.text = partItem.releaseDate
            binding.txvDate.viewVisible(); binding.txvDate.viewVisible()
        }
            ?: kotlin.run { binding.txtDate.viewGone(); binding.txvDate.viewGone() }

//        purchaseItem?.instrumentTitle?.let {
//            binding.txvInstrument.text = purchaseItem.instrumentTitle
//            binding.txvInstrument.viewVisible()
//        }
//            ?: kotlin.run { binding.txvInstrument.viewGone() }
    }
}