package com.co.brasso.feature.shared.base

import com.co.brasso.R
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.Constants
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException

abstract class BasePresenter<V : BaseView> : MvpBasePresenter<V>() {
    private var baseInteractor: BaseInteractor? = null
    var compositeDisposable: CompositeDisposable? = null

    override fun attachView(view: V) {
        super.attachView(view)
        baseInteractor = BaseInteractor()
        compositeDisposable = CompositeDisposable()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
        super.detachView()
    }

    fun getDefaultError() = Throwable(Constants.defaultErrorText)

    fun getDefaultErrorString() = Constants.defaultErrorText

    fun getToken(): Single<String> =
        Single.create { e ->
            ifViewAttached { view ->
                if (view.isNetworkAvailable()) {
                    if (view.getLoginState() && view.isTokenValid() && !view.getToken()
                            .isNullOrBlank()
                    ) {
                        e.onSuccess(view.getToken() ?: "")
                    } else if (!view.getRefreshToken().isNullOrBlank()) {
                        doRefreshToken(e)
                    } else if (!view.getLoginState()) {
                        e.onSuccess("")
                    } else if (view.getLoginState()) {
                        e.onSuccess("")
                    } else {
                        view.hideLoading()
                    }
                } else
                    view.showMessageDialog(R.string.error_no_internet_connection)
            }
        }

    private fun doRefreshToken(
        emitter: SingleEmitter<String>
    ) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                baseInteractor?.doRefreshToken(
                    compositeDisposable,
                    EmailLoginEntity(
                        grantType = ApiConstant.grantTypeRefreshToken,
                        refreshToken = view.getRefreshToken()
                    )
                )?.subscribe({ login ->
                    view.saveToken(login)
                    emitter.onSuccess(view.getToken() ?: "")
                }, {
                    view.hideLoading()
                    view.logout()
                })?.let {
                    this.compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)

        }
    }


    fun showErrorView(throwable: Throwable, okAction: () -> Unit?) {
        ifViewAttached { view ->
            when (throwable) {
                is BaseRepository.UnAuthorizedError -> {
                    view.logout()
                }
                is IOException -> {
                    view.showMessageDialog(R.string.error_no_internet_connection) {
                        okAction()
                    }
                }
                else -> {
                    view.showMessageDialog(
                        throwable.localizedMessage ?: Constants.defaultErrorText
                    ) {
                        okAction()
                    }
                }
            }
        }
    }
}