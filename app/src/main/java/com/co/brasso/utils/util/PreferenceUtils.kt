package com.co.brasso.utils.util

import android.content.Context
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.utils.constant.PreferenceConstants
import com.co.brasso.utils.constant.StringConstants


object PreferenceUtils {
    private fun getPreference(context: Context?) =
        context?.getSharedPreferences(PreferenceConstants.preferenceName, Context.MODE_PRIVATE)

    private fun getMainPreference(context: Context?) =
        context?.getSharedPreferences(PreferenceConstants.mainPreferenceName, Context.MODE_PRIVATE)

    fun clearAll(context: Context?) {
        getPreference(context)?.edit()?.clear()?.apply()
    }

    fun saveToken(context: Context?, login: Login) {
        val editor = getPreference(context)?.edit()
        editor?.putString(PreferenceConstants.tokenType, login.tokenType)
        editor?.putLong(
            PreferenceConstants.expiresIn,
            login.expiresIn?.toLong()?.times(1000) ?: System.currentTimeMillis()
        )
        editor?.putString(PreferenceConstants.accessToken, login.accessToken)
        editor?.putString(PreferenceConstants.refreshToken, login.refreshToken)
        editor?.putLong(PreferenceConstants.loginTime, System.currentTimeMillis())
        editor?.apply()
    }

    fun getToken(context: Context?): String? {
        val tokenType = getPreference(context)?.getString(PreferenceConstants.tokenType, null)
        val token = getPreference(context)?.getString(PreferenceConstants.accessToken, null)
        return if (tokenType.isNullOrBlank() && token.isNullOrBlank()) {
            null
        } else {
            "$tokenType $token"
        }
    }

    fun setLoginType(context: Context?, loginType: String?) {
        val editor = getPreference(context)?.edit()
        editor?.putString(PreferenceConstants.loginType, loginType)
        editor?.apply()
    }

    fun getLoginType(context: Context?) =
        getPreference(context)?.getString(PreferenceConstants.loginType, StringConstants.emailLogin)

    fun getRefreshToken(context: Context?) =
        getPreference(context)?.getString(PreferenceConstants.refreshToken, "") ?: ""

    fun isTokenValid(context: Context?) =
        (getPreference(context)?.getLong(PreferenceConstants.loginTime, 0)
            ?.plus(getPreference(context)?.getLong(PreferenceConstants.expiresIn, 0) ?: 0)
            ?: 0) > System.currentTimeMillis()

    fun setLoginState(context: Context?, state: Boolean) {
        val editor = getPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.isLoggedIn, state)
        editor?.apply()
    }

    fun getLoginState(context: Context?) =
        getPreference(context)?.getBoolean(PreferenceConstants.isLoggedIn, false) ?: false

    fun isFcmTokenRegistered(context: Context?) =
        getMainPreference(context)?.getBoolean(PreferenceConstants.isFcmTokenRegistered, false)
            ?: false

    fun setFcmTokenRegistered(context: Context?, state: Boolean) {
        val editor = getMainPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.isFcmTokenRegistered, state)
        editor?.apply()
    }

    fun getWalkTroughSeen(context: Context?) =
        getMainPreference(context)?.getBoolean(PreferenceConstants.walkThroughSeen, false)
            ?: false

    fun setWalkThroughSeen(context: Context?, state: Boolean) {
        val editor = getMainPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.walkThroughSeen, state)
        editor?.apply()
    }

    fun toSendReferrerDetails(context: Context?, bool: Boolean) {
        val editor = getPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.toSendReferrer, bool)
        editor?.apply()
    }

    fun checkToSendReferrerDetails(context: Context?) =
        getPreference(context)?.getBoolean(PreferenceConstants.toSendReferrer, false)
            ?: false

    fun saveReferrerCode(context: Context?, referrer: String?) {
        val editor = getPreference(context)?.edit()
        editor?.putString(PreferenceConstants.referrerCode, referrer)
        editor?.apply()
    }

    fun getReferrerCode(context: Context?) =
        getPreference(context)?.getString(PreferenceConstants.referrerCode, "")
            ?: "false"

    fun saveUserId(context: Context?, userId: String?) {
        val editor = getPreference(context)?.edit()
        editor?.putString(PreferenceConstants.userId, userId ?: "")
        editor?.apply()
    }

    fun getUserId(context: Context?): String =
        getPreference(context)?.getString(PreferenceConstants.userId, "") ?: ""

    fun saveRegistrationEmail(context: Context?, email: String?) {
        val editor = getMainPreference(context)?.edit()
        editor?.putString(
            PreferenceConstants.preferenceEmail, email
        )
        editor?.apply()
    }

    fun getRegistrationEmail(context: Context?): String? =
        getMainPreference(context)?.getString(PreferenceConstants.preferenceEmail, null)

    fun saveRegistrationPassword(context: Context?, password: String?) {
        val editor = getMainPreference(context)?.edit()
        editor?.putString(
            PreferenceConstants.preferencePassword, password
        )
        editor?.apply()
    }

    fun getRegistrationPassword(context: Context?): String? =
        getMainPreference(context)?.getString(PreferenceConstants.preferencePassword, null)

    fun setFileTypePosition(context: Context?, filetype: String?) {
        val editor = getPreference(context)?.edit()
        editor?.putString(PreferenceConstants.fileOptions, filetype)
        editor?.apply()
    }

    fun getFileTypePosition(context: Context?): String =
        getPreference(context)?.getString(PreferenceConstants.fileOptions, "") ?: ""

    fun putStreamOnWifi(context: Context?, status: Boolean) {
        val editor = getPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.streamOnWifi, status)
        editor?.apply()
    }

    fun getStreamOnWifi(context: Context?) =
        getPreference(context)?.getBoolean(PreferenceConstants.streamOnWifi, true)
            ?: false

    fun putNotifyingWhenMobileData(context: Context?, status: Boolean) {
        val editor = getPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.notifyingMobileData, status)
        editor?.apply()
    }

    fun getNotifyingWhenMobileData(context: Context?) =
        getPreference(context)?.getBoolean(PreferenceConstants.notifyingMobileData, true)
            ?: false

    fun putDownloadOnlyOnWIFI(context: Context?, bool: Boolean) {
        val editor = getPreference(context)?.edit()
        editor?.putBoolean(PreferenceConstants.downloadOnlyOnWIFI, bool)
        editor?.apply()
    }

    fun getDownloadOnlyOnWIFI(context: Context?) =
        getPreference(context)?.getBoolean(PreferenceConstants.downloadOnlyOnWIFI, true)
            ?: false
}