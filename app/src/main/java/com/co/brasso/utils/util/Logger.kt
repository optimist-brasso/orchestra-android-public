package com.co.brasso.utils.util

import android.util.Log
import com.co.brasso.BuildConfig

object Logger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}