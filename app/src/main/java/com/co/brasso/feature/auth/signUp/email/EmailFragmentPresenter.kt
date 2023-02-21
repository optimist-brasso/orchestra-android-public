package com.co.brasso.feature.auth.signUp.email

import android.util.Patterns
import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.auth.EmailVerification

class EmailFragmentPresenter :
    BasePresenter<EmailFragmentView>() {
    private var emailFragmentInteractor: EmailFragmentInteractor? = null

    override fun attachView(view: EmailFragmentView) {
        super.attachView(view)
        emailFragmentInteractor = EmailFragmentInteractor()
    }

    override fun detachView() {
        emailFragmentInteractor = null
        super.detachView()
    }

    fun getOTP(emailVerification: EmailVerification?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(emailVerification, view)) {
                    view.showLoading()
                    emailFragmentInteractor?.getOTP(compositeDisposable, emailVerification)
                        ?.subscribe({
                            view.hideLoading()
                            view.success(it.message)
                        }, {
                            view.hideLoading()
                            showErrorView(it){}
                        })?.let {
                            compositeDisposable?.add(it)
                        }
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun validate(emailVerification: EmailVerification?, view: EmailFragmentView): Boolean {
        if (emailVerification?.email.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailVerification?.email.toString()).matches()) {
            view.showMessageDialog(R.string.email_invalid)
            return false
        }
        return true
    }
}