package com.co.brasso.feature.orchestraPlayer

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class OrchestraPlayerListFragmentPresenter : BasePresenter<OrchestraPlayerListFragmentView>() {
    private var playerListFragmentInteractor: OrchestraPlayerListFragmentInteractor? = null

    override fun attachView(view: OrchestraPlayerListFragmentView) {
        super.attachView(view)
        playerListFragmentInteractor = OrchestraPlayerListFragmentInteractor()
    }

    override fun detachView() {
        playerListFragmentInteractor = null
        super.detachView()
    }

    fun getPlayerList(orchestraId: Int?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (page == 1)
                    view.showProgressBar()
                getToken().flatMap {
                    playerListFragmentInteractor?.getPlayerList(compositeDisposable,it, orchestraId, page)
                }.subscribe({
                        if (page == 1)
                            view.hideProgressBar()
                        view.success(it.toMutableList())
                    }, {
                        if (page == 1)
                            view.hideProgressBar()
                        showErrorView(it) {}
                    }).let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}