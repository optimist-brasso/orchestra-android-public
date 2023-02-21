package com.co.brasso.feature.shared.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.R
import com.co.brasso.databinding.AdapterNotificationListingBinding
import com.co.brasso.feature.shared.adapter.viewholder.NotificationListViewHolder
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso

class NotificationListingAdapter(
    private var notificationList: MutableList<NotificationResponse>?,
    var itemClicked: (notificationResponse: NotificationResponse?) -> Unit
) : RecyclerView.Adapter<NotificationListViewHolder>() {

    lateinit var binding: AdapterNotificationListingBinding
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        this.context = parent.context
        binding = AdapterNotificationListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationListViewHolder(binding) {
            itemClicked(notificationList?.get(it ?: 0))
        }
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(
        holder: NotificationListViewHolder, position: Int
    ) {
        if (context == null)
            return

        val notification = notificationList?.get(position)


        Picasso.get().load(notification?.image).placeholder(R.drawable.ic_thumbnail)
            .into(holder.imvNotification)

        if (notification?.title.isNullOrEmpty()) {
            holder.txvTitle.viewGone()
        } else {
            holder.txvTitle.viewVisible()
            holder.txvTitle.text = notification?.title
        }

        if (notification?.body.isNullOrEmpty()) {
            holder.txvNotificationDescription.viewGone()
        } else {
            holder.txvNotificationDescription.viewVisible()
            holder.txvNotificationDescription.text = notification?.body
        }

        if (notification?.createdAt.isNullOrEmpty()) {
            holder.txvDate.viewGone()
        } else {
            holder.txvDate.text =
                notification?.createdAt
        }

        if (notification?.seenStatus == true)
            holder.crdIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )
        else
            holder.crdIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.red
                )
            )
        if (!notification?.color.isNullOrEmpty())
            holder.imvIndicatorVertical.setBackgroundColor(
                Color.parseColor(notification?.color)
            )
    }

    override fun getItemCount() = notificationList?.size ?: 0
}