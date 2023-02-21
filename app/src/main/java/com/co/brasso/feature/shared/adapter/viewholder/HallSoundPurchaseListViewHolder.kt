package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPurchaseListBinding
import com.co.brasso.feature.shared.model.response.purchaseList.HallSoundItem
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class HallSoundPurchaseListViewHolder(
    var binding: AdapterPurchaseListBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(hallSoundItem: HallSoundItem?, itemClicked: (position: Int?) -> Unit) {
//        binding.txvInstrument.viewGone()

        binding.rllMain.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }

        Picasso.get().load(hallSoundItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvPurchaseList)

        hallSoundItem?.duration?.let {
            binding.txvLength.text =
                DateUtils.convertIntToRecordTime(hallSoundItem.duration.toLong())
        }
            ?: kotlin.run { binding.txvLength.viewGone() }

        hallSoundItem?.title?.let { binding.txvTitle.text = hallSoundItem.title }
            ?: kotlin.run { binding.txvTitle.viewGone() }

        hallSoundItem?.titleJp?.let { binding.txvTitleDescription.text = hallSoundItem.titleJp }
            ?: kotlin.run { binding.txvTitleDescription.viewGone() }

//        purchaseItem?.duration?.let {
//            binding.txvTime.text = DateUtils.convertIntToRecordTime(purchaseItem.duration.toLong())
//        }
//            ?: kotlin.run { binding.txvTime.viewGone() }

        binding.txvTime.viewGone()

        hallSoundItem?.releaseDate?.let { binding.txvDate.text = hallSoundItem.releaseDate }
            ?: kotlin.run { binding.txvDate.viewGone(); binding.txvDate.viewGone() }
    }
}