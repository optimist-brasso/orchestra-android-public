package com.co.brasso.feature.auth.signUp.otp

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.model.request.auth.OtpVerification

class OtpFragmentPresenter :
    BasePresenter<OtpFragmentView>() {
    private var otpFragmentInteractor: OtpFragmentInteractor? = null

    override fun attachView(view: OtpFragmentView) {
        super.attachView(view)
        otpFragmentInteractor = OtpFragmentInteractor()
    }

    override fun detachView() {
        otpFragmentInteractor = null
        super.detachView()
    }

    fun verifyOtp(otpVerification: OtpVerification?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(otpVerification?.token, view)) {
                    view.showLoading()
                    otpFragmentInteractor?.verifyOtp(compositeDisposable, otpVerification)
                        ?.subscribe({
                            view.hideLoading()
                            view.success(it.message)
                        }, {
                            view.hideLoading()
                            showErrorView(it) {}
                        })?.let {
                            compositeDisposable?.add(it)
                        }
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun resendToken(emailVerification: EmailVerification?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                otpFragmentInteractor?.resendToken(compositeDisposable, emailVerification)
                    ?.subscribe({
                        view.hideLoading()
                        view.resendSuccess(it.message)
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }


    private fun validate(otpVerification: String?, view: OtpFragmentView): Boolean {
        if (otpVerification.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_otp)
            return false
        }
        return true
    }
}