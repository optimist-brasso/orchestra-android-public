package com.co.brasso.feature.shared.adapter.viewholder

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.FragmentPlayerProfileBinding
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DensityUtils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class PlayerDetailViewHolder(
    var binding: FragmentPlayerProfileBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        val width = DensityUtils.getScreenWidth(binding.root.context)
        val height = width * (3 / 2f)
        if (width > 0 && height > 0) {
            val params = ConstraintLayout.LayoutParams(width, height.toInt())
            binding.imgPlayerPic.layoutParams = params
        }
    }

    fun bindData(images: String?) {
        binding.imgProgress.viewVisible()
        Picasso.get().load(images)
            .into(binding.imgPlayerPic,object:Callback{
                override fun onSuccess() {
                    binding.imgProgress.viewGone()
                }

                override fun onError(e: Exception?) {
                    binding.imgProgress.viewGone()
                }

            })
    }
}