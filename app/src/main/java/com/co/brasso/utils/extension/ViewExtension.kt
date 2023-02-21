package com.co.brasso.utils.extension

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.co.brasso.R

fun View?.enableView() {
    this?.isEnabled = true
}

fun View?.disableView() {
    this?.isEnabled = false
}

//disable view for 2 seconds so as to handle multiple clicks of view
fun View?.disableForTwoSeconds() {
    this?.disableView()
    Handler().postDelayed({
        this?.enableView()
    }, 2000)
}

fun View?.viewVisible() {
    this?.visibility = View.VISIBLE
}

fun View?.viewInVisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.viewGone() {
    this?.visibility = View.GONE
}

fun TextView?.setHeaderTextColorAccent(context : Context){
    this?.setTextColor(
        ContextCompat.getColor(
            context,
            R.color.colorAccent
        )
    )
}

fun TextView?.setHeaderTextColorGrey(context : Context){
    this?.setTextColor(
        ContextCompat.getColor(
            context,
            R.color.header_title
        )
    )
}


fun View?.isVisible() = this?.visibility == View.VISIBLE