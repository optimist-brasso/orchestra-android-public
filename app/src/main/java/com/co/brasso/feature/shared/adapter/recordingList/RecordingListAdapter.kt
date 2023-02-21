package com.co.brasso.feature.shared.adapter.recordingList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterRecordingListBinding
import com.co.brasso.databinding.LayoutLoadingBinding
import com.co.brasso.feature.shared.RecordingListClick
import com.co.brasso.feature.shared.adapter.viewholder.LoadingViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.RecordingListViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.utils.constant.Constants

class RecordingListAdapter(
    private var recordingList: List<RecordingListResponse>?,
    var listner: RecordingListClick,
    var onLastIndexReached: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            Constants.playerListViewType -> {
                val binding =
                    AdapterRecordingListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return RecordingListViewHolder(binding, listner)
            }
            else -> {
                val binding =
                    LayoutLoadingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return LoadingViewHolder(binding)
            }

        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.playerListViewType -> {
                holder as RecordingListViewHolder
                holder.bindData(recordingList?.get(position))
            }
            else -> {
                populateProgress(position)
            }
        }

    }

    override fun getItemCount() = recordingList?.size ?: 0


    override fun getItemViewType(position: Int): Int {
        return when (recordingList?.get(position)?.viewType) {
            Constants.playerListView -> Constants.playerListViewType
            else -> Constants.showLoadingSection
        }
    }

    private fun populateProgress(position: Int) {
        if (position == ((recordingList?.count()
                ?: 0) - 1) && recordingList?.lastOrNull()?.hasNext == true
        ) {
            onLastIndexReached()
        }
    }
}