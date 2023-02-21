package com.co.brasso.feature.shared.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.squareup.picasso.Picasso

class ImageListAdapter(
    private var list: ArrayList<String>
) :
    RecyclerView.Adapter<ImageListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageList = list[position]

        Picasso.get().load(imageList).into(holder.imageView)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView? = null

        init {
            imageView = itemView.findViewById(R.id.imvImageView)
        }

    }

}