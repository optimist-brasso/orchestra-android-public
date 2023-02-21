package com.co.brasso.feature.shared.adapter.multiPartList

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterBuyMultiPartBinding
import com.co.brasso.feature.shared.MultiPartListViewHolder
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.utils.constant.StringConstants
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class MultiPartListAdapter(
    private var multiPartList: MutableList<MultiPartListResponseItem>?
) :
    RecyclerView.Adapter<MultiPartListViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiPartListViewHolder {
        context = parent.context
        val binding =
            AdapterBuyMultiPartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MultiPartListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MultiPartListViewHolder, position: Int) {
        val multiPart = multiPartList?.get(position)

        if (context == null)
            return

        holder.chbBuyButton.setOnClickListener {
            multiPart?.isChecked = holder.chbBuyButton.isChecked
            holder.changeCheckBoxStatus(context!!, multiPart?.isChecked ?: false)
        }

        holder.txvPlayerInstrument.text = multiPart?.name

        holder.txvPlayerName.text = multiPart?.player?.name

        Picasso.get().load(multiPart?.player?.image).transform(object : Transformation {
            override fun transform(source: Bitmap?): Bitmap? {
                val result =
                    source?.let { Bitmap.createBitmap(it, 0, 0, source.width, source.height / 2) }
                source?.recycle()
                return result
            }

            override fun key(): String {
                return System.currentTimeMillis().toString()
            }
        }).placeholder(R.drawable.ic_playerlist_thumbnail).into(holder.imgPlayerImage)


        if (multiPart?.fromPage?.equals(StringConstants.partPage) == true) {
            holder.changeCheckBoxStatus(context!!, multiPart.isChecked ?: false)

            holder.changeCheckBoxStatusForBought(multiPart.isPartBought ?: false)
        } else {

            holder.changePremiumCheckBoxStatusForBought(
                multiPart?.isPartBought ?: false,
                multiPart?.isPremiumBought ?: false
            )
        }
    }

    override fun getItemCount() = multiPartList?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return multiPartList?.get(position)?.id?.toLong() ?: 0L
    }

    fun isAllPartPurchase(): Boolean {
        return multiPartList?.none { it.isPartBought == false } == true
    }

    fun isAllMultiPartPurchase(): Boolean {
        return multiPartList?.none { it.isPremiumBought == false } == true
    }
}