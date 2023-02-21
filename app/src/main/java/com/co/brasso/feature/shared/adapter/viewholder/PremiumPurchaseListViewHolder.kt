package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.model.response.purchaseList.PremiumItem
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class PremiumPurchaseListViewHolder(
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
    fun bindData(premiumItem: PremiumItem?) {

        Picasso.get().load(premiumItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvPurchaseList)

        Picasso.get().load(premiumItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvPurchaseList)

        premiumItem?.duration?.let {
            binding.txvLength.text =
                DateUtils.convertIntToRecordTime(premiumItem.duration.toLong())
            binding.txvLength.viewVisible()
        }
            ?: kotlin.run { binding.txvLength.viewGone() }

        premiumItem?.title?.let {
            binding.txvTitle.text = premiumItem.title
            binding.txvTitleDescription.viewVisible()
        }
            ?: kotlin.run { binding.txvTitle.viewGone() }

        premiumItem?.titleJp?.let {
            binding.txvTitleDescription.text = premiumItem.titleJp
            binding.txvTitleDescription.viewVisible()
        }
            ?: kotlin.run { binding.txvTitleDescription.viewGone() }

        premiumItem?.duration?.let {
            binding.txvTime.text = premiumItem.instrumentTitle
            binding.txvTime.viewVisible()
        }
            ?: kotlin.run { binding.txvTime.viewGone() }

        premiumItem?.releaseDate?.let {
            binding.txvDate.text = premiumItem.releaseDate
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