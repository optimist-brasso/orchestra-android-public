package com.co.brasso.feature.shared.adapter.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterCartListBinding
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class CartListViewHolder(
    var binding: AdapterCartListBinding,
    var itemClicked: (position: Int?) -> Unit,
    var onItemAdd: (position: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.txtPurchaseCancel.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
        binding.chbPurchase.setOnClickListener {
            onItemAdd(absoluteAdapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindData(cartListResponse: CartListResponse?) {
        cartListResponse?.isChecked?.let {
            binding.chbPurchase.isChecked = it
        }

        when (cartListResponse?.type) {
            Constants.hallSoundText -> {
                binding.rllPlayerInfo.viewGone()

            }
            else -> {
                binding.rllPlayerInfo.viewVisible()
                val playerName = "(" + cartListResponse?.musicianName + ")"
                binding.txtPart.text = cartListResponse?.instrument + " " + playerName

            }
        }

        cartListResponse?.title?.let { binding.txtOriginalRecEng.text = cartListResponse.title }
            ?: kotlin.run { binding.txtOriginalRecEng.viewGone() }

        cartListResponse?.titleJp?.let { binding.txtOriginalRecJpn.text = cartListResponse.titleJp }
            ?: kotlin.run { binding.txtOriginalRecJpn.viewGone() }

        cartListResponse?.price?.let {
            binding.txtPrice.text =
                "%,d".format(cartListResponse.price.toInt()) + binding.root.context.getString(R.string.P)
        } ?: kotlin.run {
            binding.txtPrice.text =
                binding.root.context.getString(R.string.zero) + binding.root.context.getString(R.string.P)
        }

        var sessionType = ""
        cartListResponse?.sessionType?.let {
            sessionType = when (it) {
                Constants.premium -> {
                    binding.root.context.getString(R.string.premiumVideo)
                }
                Constants.comboType -> {
                    binding.root.context.getString(R.string.premiumSet)
                }
                else -> {
                    ""
                }
            }
        }

        cartListResponse?.type?.let {
            binding.txtType.text = "[ " + cartListResponse.type + " ] " + sessionType
        }
            ?: kotlin.run { binding.txtType.viewGone() }
    }
}