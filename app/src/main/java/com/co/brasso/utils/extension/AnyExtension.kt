package com.co.brasso.utils.extension

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.util.Utils
import com.co.brasso.utils.util.Utils.TWO_DIGITS
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun Any?.toJson(): RequestBody {
    val json = GsonBuilder().create().toJson(this)
    //Log.d("Request-$this", json)
    val applicationJson = ApiConstant.contentTypeJson.toMediaTypeOrNull()
    return json.toRequestBody(applicationJson)
}

fun Any?.toJsonString(): String {
    return GsonBuilder().create().toJson(this)
}

fun File.toMultiPartRequestBody(type: String, keyName: String): MultipartBody.Part {
    val reqFile = this.asRequestBody(type.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        keyName, this.name, reqFile
    )
}

fun String?.toTextRequestBody(): RequestBody? {
    return this?.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Int?.toPxFromDp(context: Context?): Int = context?.resources?.displayMetrics?.density?.let {
    this?.times(it)?.toInt()
} ?: (this ?: 0)

fun Float?.toPxFromDp(context: Context?): Int {
    val scale = context?.resources?.displayMetrics?.density ?: 0f
    return (this?.times(scale)?.plus(0.5f))?.toInt() ?: 0
}


fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun millisToString(millis: Long, text: Boolean, seconds: Boolean, large: Boolean): String {
    var millis = millis
    val sb = StringBuilder()
    if (millis < 0) {
        millis = -millis
        sb.append("-")
    }
    millis /= 1000
    val sec = (millis % 60).toInt()
    millis /= 60
    val min = (millis % 60).toInt()
    millis /= 60
    val hours = millis.toInt()
    if (text) {
        if (hours > 0) sb.append(hours).append('h').append(if (large) " " else "")
        if (min > 0) sb.append(min).append("min").append(if (large) " " else "")
        if ((seconds || sb.isEmpty()) && sec > 0) sb.append(sec).append("s")
            .append(if (large) " " else "")
    } else {
        if (hours > 0) sb.append(hours).append(':').append(if (large) " " else "")
            .append(TWO_DIGITS.get()?.format(min.toLong())).append(':')
            .append(if (large) " " else "").append(
                TWO_DIGITS.get()?.format(sec.toLong())
            ) else sb.append(min).append(':').append(if (large) " " else "")
            .append(TWO_DIGITS.get()?.format(sec.toLong()))
    }
    return sb.toString()
}

fun Activity.hasNotch() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && window.decorView.rootWindowInsets.displayCutout != null