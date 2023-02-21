package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterPerformanceMusicBinding
import com.co.brasso.feature.shared.adapter.viewholder.PerformanceMusicViewHolder
import com.co.brasso.feature.shared.model.response.playerdetail.PerformancesItem

class PerformanceMusicAdapter(
    var performances: List<PerformancesItem>?,
    var onItemClicked: (position: Int?) -> Unit
) :
    RecyclerView.Adapter<PerformanceMusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceMusicViewHolder {
        val binding =
            AdapterPerformanceMusicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PerformanceMusicViewHolder(binding) {
            onItemClicked(it ?: 0)
        }
    }

    override fun onBindViewHolder(holder: PerformanceMusicViewHolder, position: Int) {
        holder.bindData(performances?.get(position))
    }

    override fun getItemCount() = performances?.size ?: 0

}