package com.co.brasso.feature.shared.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.utils.ProgressDialogHelper
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.*
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<V : MvpView, P : MvpPresenter<V>> : MvpActivity<V, P>(), BaseView {

    private var dialogHelper: ProgressDialogHelper? = null
    protected var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        LocaleHelper.setLanguage(this, BuildConfig.LANGUAGE_CODE)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onDestroy() {
        compositeDisposable?.dispose()
        super.onDestroy()
    }

    override fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showMessageDialog(stringInt: Int) {
        DialogUtils.showAlertDialog(this, getString(stringInt), {}, {})
    }

    override fun showMessageDialog(string: String) {
        DialogUtils.showAlertDialog(this, string, {}, {})
    }

    override fun showMessageDialog(stringInt: Int, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(this, getString(stringInt), { okAction() }, {})
    }

    override fun showMessageDialog(stringInt: Int, isCancelable: Boolean, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(
            this, getString(stringInt), {
                okAction()
            }, {}, isCancelable = isCancelable
        )
    }

    override fun showMessageDialog(string: String, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(this, string, { okAction() }, {})
    }

    override fun showMessageDialog(string: String, isCancelable: Boolean, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(
            this, string, {
                okAction()
            }, {}, isCancelable = isCancelable
        )
    }

    override fun showLoading() {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(this)
        dialogHelper?.show(getString(R.string.loading))
    }

    override fun showLoading(message: String) {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(this)
        dialogHelper?.show(message)
    }

    override fun showLoading(message: Int) {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(this)
        dialogHelper?.show(applicationContext.getString(message))
    }

    override fun hideLoading(shouldDelay: Boolean) {
        if (shouldDelay) {
            Handler().postDelayed({
                if (dialogHelper != null)
                    dialogHelper?.dismiss()
            }, 500)
        } else {
            if (dialogHelper != null)
                dialogHelper?.dismiss()
        }
    }


    override fun saveToken(login: Login) {
        PreferenceUtils.setLoginState(this, true)
        //   PreferenceUtils.setGuestLogin(this, false)
        PreferenceUtils.saveToken(this, login)
    }

    override fun getLoginState() = PreferenceUtils.getLoginState(this)

    override fun getToken() = PreferenceUtils.getToken(this)

    override fun getRefreshToken() = PreferenceUtils.getRefreshToken(this)

    //  override fun isGuestLogin() =// PreferenceUtils.isGuestLogin(this)

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun hideKeyboard(view: View?) {
        Utils.hideKeyboard(this, view)
    }

    override fun showKeyboard(view: View?) {
        Utils.showKeyboard(this, view)
    }

    override fun isTokenValid() = PreferenceUtils.isTokenValid(this)

    override fun showSessionExpired(okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(this, getString(R.string.session_expired), { okAction() }, {})
    }

    override fun logout() {
        PreferenceUtils.clearAll(this)
        Router.navigateClearingAllActivity(this, LoginActivity::class)
    }

    override fun getUserId(): String {
        return PreferenceUtils.getUserId(this)
    }

    override fun isNetworkAvailable(): Boolean {
        return NetworkUtils.isNetworkAvailable(this)
    }

    override fun getLoginType(): String? {
        return PreferenceUtils.getLoginType(this)
    }

    override fun getAppDatabase() = AppDatabase.getAppDatabase(applicationContext)

    override fun checkLoginState(): Boolean {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                this, getString(R.string.please_login), { navigateToLogin() }, {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
            return false
        }
        return true
    }

    override fun navigateToLogin() {
        Router.navigateActivityWithBooleanData(
            this,
            false,
            Constants.backPressed,
            false,
            LoginActivity::class
        )
    }

    override fun setScreenLandScape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    override fun setScreenPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}