package com.co.brasso.feature.shared.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterNotificationListingBinding

class NotificationListViewHolder(
    binding: AdapterNotificationListingBinding,
    var itemClicked: (position: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.rllNotification.setOnClickListener {
            itemClicked(absoluteAdapterPosition)
        }
    }

    var imvNotification = binding.imvNotification
    var txvTitle = binding.txvNotificationTitle
    var txvDate = binding.txvNotificationDate
    var txvNotificationDescription = binding.txvNotificationDescription
    var imvIndicatorVertical=binding.imvIndicatorVertical
    var crdIndicator=binding.crdIndicator
}