package com.co.brasso.feature.shared.adapter.pointListAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPointListBinding
import com.co.brasso.feature.shared.adapter.viewholder.PointListViewHolder
import com.co.brasso.feature.shared.model.response.bundleList.BundleListItem
import com.co.brasso.utils.extension.viewGone

class PointListAdapter(
    private var pointList: MutableList<BundleListItem>?,
    var onItemClicked: (purchaseItem: BundleListItem?) -> Unit
) :
    RecyclerView.Adapter<PointListViewHolder>() {

    lateinit var binding: AdapterPointListBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PointListViewHolder {
        binding = AdapterPointListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointListViewHolder(binding) {
            onItemClicked(pointList?.get(it ?: 0))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PointListViewHolder, position: Int) {
        val pointListData = pointList?.get(position)

        pointListData?.paidPoint?.let {
            holder.txvPoints.text =
                "%,d".format(pointListData.paidPoint)
        }
            ?: kotlin.run {
                holder.txvPoints.text =
                    binding.root.context.getString(
                        R.string.zero
                    )
            }

        pointListData?.price?.let {
            holder.txvBuy.text =
                "¥ " + "%,d".format(pointListData.price)
        }
            ?: kotlin.run {
                holder.txvBuy.text =
                    "¥ " + binding.root.context.getString(
                        R.string.zero
                    )
            }

        pointListData?.freePoint?.let {
            holder.txvDescription.text = "+" +
                    "%,d".format(pointListData.freePoint) + binding.root.context.getString(
                R.string.service
            )
        } ?: kotlin.run {
            holder.txvDescription.viewGone()
        }
    }

    override fun getItemCount() = pointList?.size ?: 0
}