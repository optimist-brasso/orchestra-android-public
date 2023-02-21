package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.OnboardingItemContainerBinding
import com.co.brasso.feature.shared.model.OnboardingItem

class OnBoardingListViewHolder(
    var binding: OnboardingItemContainerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(onBoardingItem: OnboardingItem?) {
        binding.imgBackground.setImageResource(onBoardingItem?.background ?: 0)

        binding.imgFrame.setImageResource(onBoardingItem?.frame ?: 0)

    }
}