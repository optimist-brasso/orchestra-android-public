package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.LayoutAdapterConductorBinding
import com.co.brasso.feature.shared.ClickListner
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.RoundedTransformation
import com.squareup.picasso.Picasso

class ConductorViewHolder(var binding: LayoutAdapterConductorBinding, var listner: ClickListner) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            listner.onClick(adapterPosition)
        }
    }

    fun bindData(data: HallSoundResponse, fragmentType: String?) {
        binding.txvTitle.text = data.title
        binding.txvTitleJp.text = data.titleJp
        binding.txvLength.text = DateUtils.convertIntToRecordTime(data.duration ?: 0)
        binding.txvTime.text = DateUtils.convertIntToRecordTime(data.duration ?: 0)
        binding.txvDate.text = data.releaseDate
        val t = RoundedTransformation(20f, 0f)
        when (fragmentType) {
            Constants.conductor -> Picasso.get()
                .load(data.conductorImage).placeholder(R.drawable.ic_thumbnail).transform(t)
                .into(binding.imvRecommendation)
            Constants.hallSound -> Picasso.get().load(data.venueDiagram)
                .placeholder(R.drawable.ic_thumbnail).transform(t)
                .into(binding.imvRecommendation)
            Constants.session -> Picasso.get().load(data.sessionImage)
                .placeholder(R.drawable.ic_thumbnail).transform(t)
                .into(binding.imvRecommendation)
        }
    }
}