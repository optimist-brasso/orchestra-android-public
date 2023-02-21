package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.model.response.purchaseList.ConductorItem
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class ConductorPurchaseListViewHolder(
    var binding: AdapterPurchaseListBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(conductorItem: ConductorItem?, itemClicked: (position: Int?) -> Unit) {

//        binding.txvInstrument.viewGone()

        binding.rllMain.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }

        Picasso.get().load(conductorItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvPurchaseList)

        conductorItem?.duration?.let {
            binding.txvLength.text =
                DateUtils.convertIntToRecordTime(conductorItem.duration.toLong())
        }
            ?: kotlin.run { binding.txvLength.viewGone() }

        conductorItem?.title?.let { binding.txvTitle.text = conductorItem.title }
            ?: kotlin.run { binding.txvTitle.viewGone() }

        conductorItem?.titleJp?.let { binding.txvTitleDescription.text = conductorItem.titleJp }
            ?: kotlin.run { binding.txvTitleDescription.viewGone() }

        //        purchaseItem?.duration?.let {
//            binding.txvTime.text = DateUtils.convertIntToRecordTime(purchaseItem.duration.toLong())
//        }
//            ?: kotlin.run { binding.txvTime.viewGone() }

        binding.txvTime.viewGone()

        conductorItem?.releaseDate?.let { binding.txvDate.text = conductorItem.releaseDate }
            ?: kotlin.run { binding.txtDate.viewGone(); binding.txvDate.viewGone() }
    }
}