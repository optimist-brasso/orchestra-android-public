package com.co.brasso.feature.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.OnboardingItemContainerBinding
import com.co.brasso.feature.shared.adapter.viewholder.OnBoardingListViewHolder
import com.co.brasso.feature.shared.model.OnboardingItem

class OnBoardingItemsAdapter(private val onBoardingItems: List<OnboardingItem>?) :
    RecyclerView.Adapter<OnBoardingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingListViewHolder {
        val binding =
            OnboardingItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OnBoardingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingListViewHolder, position: Int) {
        holder.bindData(onBoardingItems?.get(position))
    }

    override fun getItemCount() = onBoardingItems?.size ?: 0

}