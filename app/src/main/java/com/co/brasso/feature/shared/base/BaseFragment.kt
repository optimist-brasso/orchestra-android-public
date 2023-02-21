package com.co.brasso.feature.shared.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.co.brasso.R
import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.utils.ProgressDialogHelper
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.NetworkUtils
import com.co.brasso.utils.util.PreferenceUtils
import com.co.brasso.utils.util.Utils
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<V : MvpView, P : MvpPresenter<V>> : MvpFragment<V, P>(), BaseView {

    private var dialogHelper: ProgressDialogHelper? = null
    protected var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
    }

    override fun onDestroy() {
        compositeDisposable?.dispose()
        super.onDestroy()
    }

    override fun showMessage(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showMessageDialog(stringInt: Int) {
        DialogUtils.showAlertDialog(requireContext(), getString(stringInt), {}, {})
    }

    override fun showMessageDialog(string: String) {
        DialogUtils.showAlertDialog(requireContext(), string, {}, {})
    }

    override fun showMessageDialog(stringInt: Int, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(requireContext(), getString(stringInt), { okAction() }, {})
    }

    override fun showMessageDialog(stringInt: Int, isCancelable: Boolean, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(
            requireContext(), getString(stringInt), {
                okAction()
            }, {}, isCancelable = isCancelable
        )
    }

    override fun showMessageDialog(string: String, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(requireContext(), string, { okAction() }, {})
    }

    override fun showMessageDialog(string: String, isCancelable: Boolean, okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(
            requireContext(), string, {
                okAction()
            }, {}, isCancelable = isCancelable
        )
    }

    override fun showLoading() {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(requireContext())
        dialogHelper?.show(getString(R.string.loading))
    }

    override fun showLoading(message: String) {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(requireContext())
        dialogHelper?.show(message)
    }

    override fun showLoading(message: Int) {
        if (dialogHelper == null)
            dialogHelper = ProgressDialogHelper(requireContext())
        dialogHelper?.show(requireContext()?.getString(message) ?: "")
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
        PreferenceUtils.setLoginState(requireContext(), true)
      //  PreferenceUtils.setGuestLogin(context, false)
        PreferenceUtils.saveToken(requireContext(), login)
    }

    override fun getLoginState() = PreferenceUtils.getLoginState(requireContext())

    override fun getToken() = PreferenceUtils.getToken(requireContext())

    override fun getRefreshToken() = PreferenceUtils.getRefreshToken(requireContext())

  //  override fun isGuestLogin() = PreferenceUtils.isGuestLogin(context)

    override fun hideKeyboard(view: View?) {
        Utils.hideKeyboard(requireContext(), view)
    }

    override fun showKeyboard(view: View?) {
        Utils.showKeyboard(requireContext(), view)
    }

    override fun isTokenValid() = PreferenceUtils.isTokenValid(requireContext())

    override fun showSessionExpired(okAction: () -> Unit?) {
        DialogUtils.showAlertDialog(
            requireContext(),
            getString(R.string.session_expired),
            { okAction() },
            {})
    }

    override fun logout() {
        PreferenceUtils.clearAll(requireContext())
        Router.navigateClearingAllActivity(requireContext(), LoginActivity::class)
    }

    override fun getUserId(): String {
        return PreferenceUtils.getUserId(requireContext())
    }

    override fun isNetworkAvailable(): Boolean {
        return NetworkUtils.isNetworkAvailable(requireContext())
    }

    override fun getLoginType(): String? {
        return PreferenceUtils.getLoginType(requireContext())
    }

    override fun getAppDatabase() = AppDatabase.getAppDatabase(requireContext())

    override fun checkLoginState(): Boolean {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(), getString(R.string.please_login), { navigateToLogin() }, {},
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
            requireContext(),
            false,
            Constants.backPressed,
            false,
            LoginActivity::class
        )
    }

    override fun setScreenLandScape() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    override fun setScreenPortrait() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}