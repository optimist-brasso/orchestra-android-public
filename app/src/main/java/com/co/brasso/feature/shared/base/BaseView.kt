package com.co.brasso.feature.shared.base

import android.view.View
import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.model.response.auth.Login
import com.hannesdorfmann.mosby3.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(message: String?)

    fun showMessage(message: Int)

    fun showMessageDialog(stringInt: Int)

    fun showMessageDialog(string: String)

    fun showMessageDialog(stringInt: Int, okAction: () -> Unit?)

    fun showMessageDialog(stringInt: Int, isCancelable: Boolean = true, okAction: () -> Unit?)

    fun showMessageDialog(string: String, okAction: () -> Unit?)

    fun showMessageDialog(string: String, isCancelable: Boolean = true, okAction: () -> Unit?)

    fun showLoading()

    fun showLoading(message: String)

    fun showLoading(message: Int)

    fun hideLoading(shouldDelay: Boolean = false)

    fun saveToken(login: Login)

    fun getToken(): String?

    fun getLoginState(): Boolean

    fun getRefreshToken(): String?

   // fun isGuestLogin(): Boolean

    fun getLoginType(): String?

    fun hideKeyboard(view: View?)

    fun showKeyboard(view: View?)

    fun isTokenValid(): Boolean

    fun showSessionExpired(okAction: () -> Unit?)

    fun logout()

    fun getUserId(): String

    fun isNetworkAvailable(): Boolean

    fun getAppDatabase(): AppDatabase?

    fun checkLoginState():Boolean

    fun navigateToLogin()

    fun setScreenPortrait()
    fun setScreenLandScape()
}