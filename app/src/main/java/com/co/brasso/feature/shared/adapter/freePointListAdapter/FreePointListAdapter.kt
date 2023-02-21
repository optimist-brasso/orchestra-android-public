package com.co.brasso.feature.shared.adapter.freePointListAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterFreePointListBinding
import com.co.brasso.databinding.LayoutLoadingBinding
import com.co.brasso.feature.shared.adapter.viewholder.FreePointListViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.LoadingViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils

class FreePointListAdapter(
    private var pointList: MutableList<FreePointListResponse>?,
    var itemClicked: (position: Int?) -> Unit,
    var onLastIndexReached: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    lateinit var binding: AdapterFreePointListBinding
    var isFirst: Boolean = true

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {

        when (viewType) {
            Constants.playerListViewType -> {
                binding = AdapterFreePointListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FreePointListViewHolder(binding) { itemClicked(it) }
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.playerListViewType -> {
                holder as FreePointListViewHolder
                val pointListData = pointList?.get(position)

                pointListData?.date?.let {
                    holder.txvExpiryDate.text =
                        DateUtils.getBillHistoryDateYear(
                            pointListData.date
                        ) + "年" +
                                DateUtils.getBillHistoryDateMonth(
                                    pointListData.date
                                ) + "月" +
                                DateUtils.getBillHistoryDateDay(
                                    pointListData.date


                                ) + "日" + binding.root.context.getString(R.string.until)
                }
                    ?: kotlin.run {
                        holder.txvExpiryDate.viewGone()
                    }

                pointListData?.point?.let {
                    holder.txvPoints.viewVisible()
                    holder.txvPoints.text = "%,d".format(pointListData.point)
                }
                    ?: kotlin.run {
                        holder.txvPoints.viewGone()
                    }

                if (position % 2 != 0) {
                    holder.rltMain.setBackgroundResource(R.color.recyclerViewOdd)
                }
            }
            else -> {
                populateProgress(position)
            }
        }
    }

    override fun getItemCount() = pointList?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (pointList?.get(position)?.viewType) {
            Constants.playerListView -> Constants.playerListViewType
            else -> Constants.showLoadingSection
        }
    }

    private fun populateProgress(position: Int) {
        if (position == ((pointList?.count()
                ?: 0) - 1) && pointList?.lastOrNull()?.hasNext == true
        ) {
            onLastIndexReached()
        }
    }
}