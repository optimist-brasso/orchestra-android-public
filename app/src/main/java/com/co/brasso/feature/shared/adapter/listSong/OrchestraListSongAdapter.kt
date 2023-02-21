package com.co.brasso.feature.shared.adapter.listSong

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterOrchestraSearchBinding
import com.co.brasso.feature.shared.OrchestraListSongViewHolder
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class OrchestraListSongAdapter(
    private var orchestraList: MutableList<HallSoundResponse>?,
    var itemClicked: (position: Int?) -> Unit,
    var favouriteClicked: (position: Int?) -> Unit,
    var fragmentType: String?
) :
    RecyclerView.Adapter<OrchestraListSongViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrchestraListSongViewHolder {
        context = parent.context
        val binding =
            AdapterOrchestraSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OrchestraListSongViewHolder(binding, {
            itemClicked(it)

        }, {
            favouriteClicked(it)
        })
    }

    override fun onBindViewHolder(holder: OrchestraListSongViewHolder, position: Int) {
        val favouriteData = orchestraList?.get(position)

        if (context == null)
            return

        holder.txvTitle.text = favouriteData?.title
        holder.txvTitleJp.text = favouriteData?.titleJp

        when (fragmentType) {
            Constants.hallSound -> {
                Picasso.get().load(favouriteData?.venueDiagram).placeholder(R.drawable.ic_thumbnail)
                    .into(holder.imvFavouriteOrchestra)
            }
            Constants.conductor -> {
                Picasso.get().load(favouriteData?.conductorImage)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(holder.imvFavouriteOrchestra)
            }
        }

        holder.txvLength.text = DateUtils.convertIntToRecordTime(favouriteData?.duration ?: 0)

        when (favouriteData?.type) {
            Constants.conductor -> {
                holder.imvFavourite.viewVisible()
                holder.setFavourite(context, favouriteData.isConductorFavourite)
            }

            Constants.session -> {
                holder.imvFavourite.viewGone()
            }

            Constants.hallSound -> {
                holder.imvFavourite.viewVisible()
                holder.setFavourite(context, favouriteData.isHallsoundFavourite)
            }
        }
    }

    override fun getItemCount() = orchestraList?.size ?: 0
}