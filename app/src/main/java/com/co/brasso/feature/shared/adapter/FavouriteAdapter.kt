package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.feature.shared.adapter.viewholder.FavouriteViewHolder
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

class FavouriteAdapter(var list: List<HallSoundResponse>) :
    RecyclerView.Adapter<FavouriteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_recommendation, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 22
    }

}