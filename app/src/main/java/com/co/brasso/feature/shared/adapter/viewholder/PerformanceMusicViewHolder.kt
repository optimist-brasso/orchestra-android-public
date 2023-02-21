package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPerformanceMusicBinding
import com.co.brasso.feature.shared.model.response.playerdetail.PerformancesItem
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class PerformanceMusicViewHolder(
    var binding: AdapterPerformanceMusicBinding,
    var itemClicked: (position: Int?) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.rllMain.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
    }

    fun bindData(playerListResponse: PerformancesItem?) {
        Picasso.get().load(playerListResponse?.image).into(binding.imvRecommendation)

        if (playerListResponse?.duration.isNullOrEmpty()) {
            binding.txvLength.viewGone()
            binding.txvTime.viewGone()
        } else {
            val date = (playerListResponse?.duration ?: "").toLong()
            binding.txvLength.text =
                DateUtils.convertIntToRecordTime(date)
            binding.txvTime.text = DateUtils.convertIntToRecordTime(date)
        }

        if (playerListResponse?.recordTime.isNullOrEmpty()) {
            binding.txvDate.viewGone()
        } else {
            binding.txvDate.text = playerListResponse?.releaseDate
        }

        if (playerListResponse?.title.isNullOrEmpty()) {
            binding.txvTitle.viewGone()
        } else {
            binding.txvTitle.text = playerListResponse?.title
        }

        if (playerListResponse?.titleJp.isNullOrEmpty()) {
            binding.txvTitleDescription.viewGone()
        } else {
            binding.txvTitleDescription.text = playerListResponse?.titleJp
        }
    }
}