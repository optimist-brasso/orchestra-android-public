package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.LayoutAdapterConductorBinding
import com.co.brasso.feature.shared.ClickListner
import com.co.brasso.feature.shared.adapter.viewholder.ConductorViewHolder
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

class ConductorAdapter(var list: List<HallSoundResponse>, var listner: ClickListner,val fragmentType:String?) :
    RecyclerView.Adapter<ConductorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConductorViewHolder {
        val binding = LayoutAdapterConductorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConductorViewHolder(binding, listner)
    }

    override fun onBindViewHolder(holder: ConductorViewHolder, position: Int) {
        holder.bindData(list[position],fragmentType)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}