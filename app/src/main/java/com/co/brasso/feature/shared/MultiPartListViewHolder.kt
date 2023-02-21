package com.co.brasso.feature.shared

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterBuyMultiPartBinding
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class MultiPartListViewHolder(
    itemView: AdapterBuyMultiPartBinding
) : RecyclerView.ViewHolder(itemView.root) {

    var imgPlayerImage = itemView.imvPlayerImage
    var txvPlayerInstrument = itemView.txvPlayerInstrument
    var txvPlayerName = itemView.txvPlayerName
    var chbBuyButton = itemView.chbBuyButton
    var rllCheckBox = itemView.rllCheckBox
    var rllBoughtBox = itemView.rllBoughtBox
    var rllCannotBuy = itemView.rllCannotBuyBox

    fun changeCheckBoxStatus(context: Context, status: Boolean) {
        chbBuyButton.isChecked = status
        if (status) {
            rllCheckBox.background =
                ContextCompat.getDrawable(context, R.drawable.checkbox_selected)
            chbBuyButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        } else {
            rllCheckBox.background =
                ContextCompat.getDrawable(context, R.drawable.checkbox_unselected)
            chbBuyButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.light_grey_text
                )
            )
        }
    }

    fun changeCheckBoxStatusForBought(status: Boolean) {
        if (status) {
            rllCheckBox.viewGone()
            rllBoughtBox.viewVisible()
        } else {
            rllCheckBox.viewVisible()
            rllBoughtBox.viewGone()
        }
    }

    fun changePremiumCheckBoxStatusForBought(partBought: Boolean, premiumBought: Boolean) {
        if (partBought && premiumBought) {
            rllCheckBox.viewGone()
            rllCannotBuy.viewGone()
            rllBoughtBox.viewVisible()
        } else if (!partBought && !premiumBought) {
            rllCheckBox.viewVisible()
            rllCannotBuy.viewGone()
            rllBoughtBox.viewGone()
        } else {
            rllCheckBox.viewGone()
            rllCannotBuy.viewVisible()
            rllBoughtBox.viewGone()
        }
    }
}