package com.co.brasso.feature.shared.adapter.viewholder

import android.graphics.Bitmap
import com.co.brasso.R
import com.co.brasso.databinding.AdapterPlayerListBinding
import com.co.brasso.feature.shared.PlayerListClick
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DensityUtils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.lang.Exception


class PlayerListViewHolder(
    var binding: AdapterPlayerListBinding, var listner: PlayerListClick
) : BaseViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            listner.onClick(bindingAdapterPosition)
        }
    }

    fun bindData(playerListResponse: PlayerDetailResponse?) {

        val screenHeight =
            (DensityUtils.getActualScreenWidth(binding.root.context) / 3f) / (2f / 3f)

        binding.imgPLayer.layoutParams.height = screenHeight.toInt()

        if (playerListResponse?.name.isNullOrEmpty()) {
            binding.txtPlayerName.viewGone()
        } else {
            binding.txtPlayerName.text = playerListResponse?.name
        }
        if (playerListResponse?.instrument?.name.isNullOrEmpty()) {
            binding.txtPlayerInstrument.viewGone()
        } else {
            binding.txtPlayerInstrument.viewVisible()
            binding.txtPlayerInstrument.text = playerListResponse?.instrument?.name
        }
        if(playerListResponse?.image.isNullOrEmpty()){
            binding.imgProgress.viewGone()
            Picasso.get().load(R.drawable.ic_playerlist_thumbnail).into(binding.imgPLayer)
        }else{
            binding.imgProgress.viewVisible()
            Picasso.get().load(playerListResponse?.image).error(R.drawable.ic_playerlist_thumbnail)
                .transform(object : Transformation {
                    override fun transform(source: Bitmap?): Bitmap? {
                        val result =
                            source?.let {
                                Bitmap.createBitmap(
                                    it,
                                    0,
                                    0,
                                    source.width,
                                    source.height / 2
                                )
                            }
                        source?.recycle()
                        return result
                    }

                    override fun key(): String {
                        return System.currentTimeMillis().toString()
                    }
                }).fit().into(binding.imgPLayer,object:Callback{
                    override fun onSuccess() {
                        binding.imgProgress.viewGone()
                    }

                    override fun onError(e: Exception?) {
                        binding.imgProgress.viewGone()
                    }

                })
        }

        binding.root.setOnClickListener {
            listner.onClick(playerListResponse?.id)
        }
    }
}