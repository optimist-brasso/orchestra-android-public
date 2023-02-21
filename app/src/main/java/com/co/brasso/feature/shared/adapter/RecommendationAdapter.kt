package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterBusinessRestructuringBinding
import com.co.brasso.databinding.AdapterRecommendationBinding
import com.co.brasso.feature.shared.adapter.viewholder.RecommendationBusinessViewHolder
import com.co.brasso.feature.shared.adapter.viewholder.RecommendationViewHolder
import com.co.brasso.feature.shared.base.BaseViewHolder
import com.co.brasso.feature.shared.model.response.home.Recommendation
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.RoundedTransformation
import com.squareup.picasso.Picasso

class RecommendationAdapter(
    var recommendedList: MutableList<Recommendation>?,
    var onItemClicked: (recommendation: Recommendation?) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    lateinit var binding: AdapterRecommendationBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        when (viewType) {

            Constants.recommendedOrchestra -> {
                binding = AdapterRecommendationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return RecommendationViewHolder(binding) {
                    onItemClicked(recommendedList?.get(it ?: 0))
                }
            }

            else -> {
                val binding = AdapterBusinessRestructuringBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return RecommendationBusinessViewHolder(binding)
            }

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when(getItemViewType(position)){
            Constants.recommendedOrchestra->{
                holder as RecommendationViewHolder
                val recommended = recommendedList?.get(position)
                val t = RoundedTransformation(20f, 0f)
                Picasso.get().load(recommended?.image).placeholder(R.drawable.ic_thumbnail).transform(t)
                    .into(holder.imvRecommended)
                holder.txvRecommendedTitle.text = recommended?.title
                holder.txvRecommendedDescription.text = recommended?.titleJp
                holder.txvRecommendedTime.text =
                    DateUtils.convertIntToRecordTime(recommended?.duration ?: 0)
                if (recommended?.tags.isNullOrEmpty()) {
                    holder.txvNewLabel.viewGone()
                    holder.txvPrLabel.viewGone()
                } else {
                    if (recommended?.tags?.size == 1) {
                        holder.txvPrLabel.viewVisible()
                        holder.txvNewLabel.viewGone()
                        holder.txvPrLabel.text = recommended.tags?.get(0)
                    } else if ((recommended?.tags?.size ?: 0) >= 2) {
                        holder.txvPrLabel.viewVisible()
                        holder.txvNewLabel.viewVisible()
                        holder.txvPrLabel.text = recommended?.tags?.get(0)
                        holder.txvNewLabel.text = recommended?.tags?.get(1)
                    }
                }
                holder.txvRecommendedDate.text = recommended?.releaseDate

            }
        }
    }

    override fun getItemCount() = recommendedList?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (recommendedList?.get(position)?.viewType) {
            Constants.recommendedOrchestraSection -> Constants.recommendedOrchestra
            else -> Constants.recommendedBusiness
        }
    }

}