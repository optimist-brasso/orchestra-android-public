package com.co.brasso.feature.shared.adapter.viewholder

import android.graphics.Bitmap
import com.co.brasso.R
import com.co.brasso.databinding.AdapterFavouritePlayerBinding
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DensityUtils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.lang.Exception

class PlayerFavouriteViewHolder(
    var binding: AdapterFavouritePlayerBinding,
    var favouritePlayerClicked: (position: Int?) -> Unit,
    var itemClicked: (position: Int?) -> Unit

) : BaseViewHolder(binding.root) {

    init {
        binding.imvPlayerFavourite.setOnClickListener {
            favouritePlayerClicked(bindingAdapterPosition)
        }

        binding.cnlMainLayout.setOnClickListener {
            itemClicked(bindingAdapterPosition)
        }
    }


    var txvPlayerName = binding.txvPlayerName



    fun bindData(favouritePlayer: PlayerDetailResponse?) {
        binding.imgProgress.viewVisible()
        val screenHeight =
            (DensityUtils.getActualScreenWidth(binding.root.context) / 3f) / (2f / 3f)

        binding.imvPlayerProfile.layoutParams.height = screenHeight.toInt()
        if(favouritePlayer?.image.isNullOrEmpty()){
            binding.imgProgress.viewGone()
            Picasso.get().load(R.drawable.ic_playerlist_thumbnail).into(binding.imvPlayerProfile)
        }else{
            binding.imgProgress.viewVisible()
            Picasso.get().load(favouritePlayer?.image).error(R.drawable.ic_playerlist_thumbnail).transform(object : Transformation {
                override fun transform(source: Bitmap?): Bitmap? {
                    val result =
                        source?.let { Bitmap.createBitmap(it, 0, 0, source.width, source.height/2) }
                    source?.recycle()
                    return result
                }

                override fun key(): String {
                    return System.currentTimeMillis().toString()
                }
            }).fit().into(binding.imvPlayerProfile,object: Callback {
                override fun onSuccess() {
                    binding.imgProgress.viewGone()
                }

                override fun onError(e: Exception?) {
                    binding.imgProgress.viewGone()
                }

            })
        }
        binding.txvPlayerName.text = favouritePlayer?.name
        if(favouritePlayer?.instrument?.name.isNullOrEmpty())
            binding.txvPlayerInstrument.viewGone()
        else{
            binding.txvPlayerInstrument.viewVisible()
            binding.txvPlayerInstrument.text = favouritePlayer?.instrument?.name
        }

    }
}