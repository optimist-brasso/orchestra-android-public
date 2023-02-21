package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import com.co.brasso.R
import com.co.brasso.databinding.AdapterRecordingListBinding
import com.co.brasso.feature.shared.RecordingListClick
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class RecordingListViewHolder(
    var binding: AdapterRecordingListBinding, var listner: RecordingListClick
) : BaseViewHolder(binding.root) {

    private var recordingItem: RecordingListResponse? = null

    init {
        binding.root.setOnClickListener {
            listner.onClick(recordingItem)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindData(recordingItem: RecordingListResponse?) {

        this.recordingItem = recordingItem

        Picasso.get().load(recordingItem?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvRecording)

        binding.txvSongTitle.text = recordingItem?.songTitle

        binding.txvEditionDescription.text = recordingItem?.edition

        binding.txvDuration.text = DateUtils.convertIntToRecordTime(recordingItem?.duration?.div(
            1000
        ))

        binding.txtRec.text = recordingItem?.recordedDate

    }
}