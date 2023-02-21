package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import com.co.brasso.R
import com.co.brasso.databinding.AdapterBillingHistoryBinding
import com.co.brasso.feature.shared.ClickListner
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.billinghistory.BillingHistoryResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils

class BillingHistoryViewHolder(
    var binding: AdapterBillingHistoryBinding,
    private var listener: ClickListner
) : BaseViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            listener.onClick(absoluteAdapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindData(data: BillingHistoryResponse?) {

        if (data?.date.isNullOrEmpty()) {
            binding.txvDateOfPurchase.viewGone()
            binding.txvPurchaseDate.viewGone()
        } else {
            binding.txvPurchaseDate.text = DateUtils.dateFormatYearDotMonthDotDay(data?.date)
        }

        when (data?.type) {
            Constants.hallSoundText -> {
                binding.txtMusicianName.viewGone()
            }
            else -> {
                binding.txtMusicianName.viewVisible()
                binding.txtMusicianName.text = "(" + data?.musicianName + ")"
            }
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

        if (data?.type.isNullOrEmpty()) {
            binding.tvSession.viewGone()
        } else {
            when (data?.type) {
                Constants.hallSoundText -> {
                    binding.tvSession.text = "[Hall Sound]"
                }
                Constants.session -> {
                    binding.tvSession.text = "[Session]"
                }
                else -> {
                    binding.tvSession.text = "[Conductor]"
                }
            }
        }

        if (data?.sessionType.isNullOrEmpty()) {
            binding.txvPremiumType.viewGone()
        } else {
            when (data?.sessionType) {
                Constants.comboType -> {
                    binding.txvPremiumType.text =
                        binding.root.context.getString(R.string.premiumSet)
                }
                Constants.premium -> {
                    binding.txvPremiumType.text =
                        binding.root.context.getString(R.string.premiumVideo)
                }
                else -> {
                    binding.txvPremiumType.viewGone()
                }
            }
        }

        if (data?.title.isNullOrEmpty()) {
            binding.tvOriginalRecording.viewGone()
        } else {
            if (data?.titleJP.isNullOrEmpty()) {
                binding.tvOriginalRecording.text =
                    data?.title
            } else {
                binding.tvOriginalRecording.text =
                    data?.title + "/" + data?.titleJP
            }
        }

        if (data?.instrument.isNullOrEmpty()) {
            binding.tvPartSong.viewGone()
            binding.txvPart.viewGone()
        } else {
            binding.tvPartSong.viewVisible()
            binding.txvPart.viewVisible()
            binding.tvPartSong.text =
                data?.instrument
        }

    }

}
