package com.co.brasso.feature.shared.adapter.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterOrchestraSearchBinding
import com.co.brasso.feature.shared.OrchestraSearchViewHolder
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class OrchestraSearchAdapter(
    var orchestraList: MutableList<HallSoundResponse>?,
    var itemClicked: (hallSoundResponse: HallSoundResponse?) -> Unit,
    var favouriteClicked: (position: Int?) -> Unit
) :
    RecyclerView.Adapter<OrchestraSearchViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrchestraSearchViewHolder {
        context = parent.context
        val binding =
            AdapterOrchestraSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OrchestraSearchViewHolder(binding, {
            itemClicked(orchestraList?.get(it))

        },{
            favouriteClicked(it)
        })
    }

    override fun onBindViewHolder(holder: OrchestraSearchViewHolder, position: Int) {
        val favouriteData = orchestraList?.get(position)

        if (context == null)
            return

        holder.txvTitle.text = favouriteData?.title

        holder.txvTitleJp.text = favouriteData?.titleJp

        Picasso.get().load(favouriteData?.image).placeholder(R.drawable.ic_thumbnail)
            .into(holder.imvFavouriteOrchestra)

        holder.txvLength.text = DateUtils.convertIntToRecordTime(favouriteData?.duration ?: 0)

        when (favouriteData?.type) {
            Constants.conductor -> {
                holder.imvFavourite.viewVisible()
                if (favouriteData.isConductorFavourite == true) {
                    holder.imvFavourite.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favourite_selected
                        )
                    )
                } else {
                    holder.imvFavourite.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favourite_session
                        )
                    )
                }
            }

            Constants.hallSound -> {
                holder.imvFavourite.viewVisible()
                if (favouriteData.isHallsoundFavourite == true) {
                    holder.imvFavourite.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favourite_selected
                        )
                    )
                } else {
                    holder.imvFavourite.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_favourite_session
                        )
                    )
                }
            }
        }
    }

    override fun getItemCount() = orchestraList?.size ?: 0
}