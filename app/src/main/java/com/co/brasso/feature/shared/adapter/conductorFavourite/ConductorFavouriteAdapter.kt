package com.co.brasso.feature.shared.adapter.conductorFavourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterConductorFavouriteBinding
import com.co.brasso.feature.shared.adapter.viewholder.ConductorFavouriteViewHolder
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class ConductorFavouriteAdapter(
    private var favouriteList: MutableList<FavouriteResponse>?,
    var favouriteClicked: (position: Int?) -> Unit,
    var itemClicked: (position: Int?) -> Unit,
    var fragmentType : String?
) :
    RecyclerView.Adapter<ConductorFavouriteViewHolder>() {

    lateinit var binding: AdapterConductorFavouriteBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConductorFavouriteViewHolder {
        binding = AdapterConductorFavouriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConductorFavouriteViewHolder(binding,
            { favouriteClicked(it) },
            { itemClicked(it) })
    }

    override fun onBindViewHolder(holder: ConductorFavouriteViewHolder, position: Int) {
        val favouriteData = favouriteList?.get(position)

        holder.txvTitle.text = favouriteData?.title
        holder.txvTitleJp.text = favouriteData?.titleJp

        when (fragmentType) {
            Constants.hallSound -> {
                Picasso.get().load(favouriteData?.venueDiagram).placeholder(R.drawable.ic_thumbnail)
                    .into(holder.imvFavouriteOrchestra)
                holder.txvLength.text = DateUtils.convertIntToRecordTime(favouriteData?.duration ?: 0)
            }
            Constants.conductor -> {
                Picasso.get().load(favouriteData?.conductorImage).placeholder(R.drawable.ic_thumbnail)
                    .into(holder.imvFavouriteOrchestra)
                holder.txvLength.text = DateUtils.convertIntToRecordTime(favouriteData?.duration ?: 0)
            }
        }

        if (favouriteData?.tags?.find { it.lowercase().trim() == Constants.premium } != null) {
            holder.txvPremium.viewVisible()
        } else {
            holder.txvPremium.viewGone()
        }
    }

    override fun getItemCount() = favouriteList?.size ?: 0
}