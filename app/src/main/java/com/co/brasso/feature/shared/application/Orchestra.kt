package com.co.brasso.feature.shared.application

import android.app.Application
import android.util.Log
import com.facebook.FacebookSdk
import io.reactivex.plugins.RxJavaPlugins

class Orchestra : Application() {
    private val tag = "==>: " + "BaseApplication"
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        handleRxCrash()

    }

    private fun handleRxCrash() {
        RxJavaPlugins.setErrorHandler { t -> Log.d(tag, "Message ${t.localizedMessage}") }
    }


}