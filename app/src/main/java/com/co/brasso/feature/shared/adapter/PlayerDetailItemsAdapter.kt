package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.FragmentPlayerProfileBinding
import com.co.brasso.feature.shared.adapter.viewholder.PlayerDetailViewHolder

class PlayerDetailItemsAdapter(val images: List<String?>?) :
    RecyclerView.Adapter<PlayerDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerDetailViewHolder {
        val binding =
            FragmentPlayerProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerDetailViewHolder, position: Int) {
        holder.bindData(images?.get(position))
    }

    override fun getItemCount(): Int {
        return images?.size ?: 1
    }
}