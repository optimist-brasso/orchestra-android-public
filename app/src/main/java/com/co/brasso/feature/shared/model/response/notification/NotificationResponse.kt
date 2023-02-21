package com.co.brasso.feature.shared.model.response.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.co.brasso.utils.constant.DbConstants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = DbConstants.tableNotifications)
data class NotificationResponse(
    @PrimaryKey
    val id: Int?,
    val title: String?=null,
    val body: String?=null,
    val image: String?=null,
    @SerializedName("created_at")
    val createdAt: String?=null,
    val color: String?=null,
    @SerializedName("seen_status")
    var seenStatus: Boolean = false
) : Serializable