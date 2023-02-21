package com.co.brasso.feature.player.playerDetail

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteMusician

class PlayerDetailFragmentPresenter : BasePresenter<PlayerDetailFragmentView>() {
    private var playerDetailFragmentInteractor: PlayerDetailFragmentInteractor? = null

    override fun attachView(view: PlayerDetailFragmentView) {
        super.attachView(view)
        playerDetailFragmentInteractor = PlayerDetailFragmentInteractor()
    }

    override fun detachView() {
        playerDetailFragmentInteractor = null
        super.detachView()
    }

    fun getPlayerDetail(id: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showProgressBar()
                getToken().flatMap {
                    playerDetailFragmentInteractor?.getPlayerDetail(compositeDisposable, id, it)
                }.subscribe({
                    view.hideProgressBar()
                    view.success(it)
                }, {
                    view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else {
                view.hideProgressBar()
                view.noInternet(R.string.error_no_internet_connection)
            }
        }
    }

    fun addMusicianFavourite(favouriteMusician: FavouriteMusician?) {
        ifViewAttached {
            getToken().flatMap { token ->
                playerDetailFragmentInteractor?.favouriteMusician(
                    compositeDisposable,
                    favouriteMusician,
                    token
                )
            }.subscribe({
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}