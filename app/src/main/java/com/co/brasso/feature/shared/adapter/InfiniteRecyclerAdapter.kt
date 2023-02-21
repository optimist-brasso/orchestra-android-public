package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.FragmentBannerBinding
import com.co.brasso.feature.shared.model.response.home.Banner
import com.squareup.picasso.Picasso

class InfiniteRecyclerAdapter(var originalList: MutableList<Banner>, var bannerClicked:(banner:Banner)->Unit) :
    RecyclerView.Adapter<InfiniteRecyclerAdapter.InfiniteRecyclerViewHolder>() {

    private lateinit var binding: FragmentBannerBinding

    private val newList: List<Banner> =
        listOf(originalList.last()) + originalList + listOf(originalList.first())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfiniteRecyclerViewHolder {
        binding = FragmentBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InfiniteRecyclerViewHolder(binding){
            bannerClicked(newList[it])
        }
    }

    override fun onBindViewHolder(holder: InfiniteRecyclerViewHolder, position: Int) {
        val banner = newList[position]
        Picasso.get().load(banner.image).placeholder(R.drawable.ic_thumbnail).into(holder.imvBanner)
    }

    override fun getItemCount(): Int {
        return newList.size
    }

    class InfiniteRecyclerViewHolder(itemView: FragmentBannerBinding,var itemClicked:(position:Int)->Unit) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            itemView.imvBanner.setOnClickListener {
                itemClicked(adapterPosition)
            }
        }
        var imvBanner = itemView.imvBanner
    }

}