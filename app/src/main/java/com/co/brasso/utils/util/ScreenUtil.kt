package com.co.brasso.utils.util

import android.content.res.Resources

object ScreenUtil {

    fun getScreenWidth(): Int =
         Resources.getSystem().displayMetrics.widthPixels

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }


}