package com.co.brasso.feature.landing.search

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants

class SearchPresenter : BasePresenter<SearchView>() {

    private var searchInteractor: SearchInteractor? = null

    override fun attachView(view: SearchView) {
        super.attachView(view)
        searchInteractor = SearchInteractor()
    }

    override fun detachView() {
        searchInteractor = null
        super.detachView()
    }

    fun orchestraSearch(slug: String?, isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isSwipeToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap {
                    searchInteractor?.orchestraSearch(it, slug, compositeDisposable)
                }.subscribe({
                    if (isSwipeToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.setOrchestraSearchData(getSearchList(it))
                }, {
                    if (isSwipeToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    //using single list is creating issue so all type list is merged in one
    private fun getSearchList(list: MutableList<HallSoundResponse>?): MutableList<HallSoundResponse> {

        val searchList: MutableList<HallSoundResponse> = mutableListOf()

        val searchListConductor: MutableList<HallSoundResponse> = mutableListOf()
        val searchListSession: MutableList<HallSoundResponse> = mutableListOf()
        val searchListHallSound: MutableList<HallSoundResponse> = mutableListOf()

        list?.forEach {
            searchListConductor.add(
                HallSoundResponse(
                    it.id,
                    it.title,
                    it.titleJp,
                    it.composer,
                    it.conductor,
                    it.introduction,
                    it.tags,
                    it.recordTime,
                    it.releaseDate,
                    it.duration,
                    it.image,
                    it.organization,
                    it.organizationDiagram,
                    it.businessType,
                    it.band,
                    it.jasracLicenseNumber,
                    it.venueTitle,
                    it.venueDescription,
                    it.isConductorFavourite,
                    it.isHallsoundFavourite,
                    it.isSessionFavourite,
                    it.vrFile,
                    it.isBought,
                    it.conductorOrchestraPrice,
                    it.hallSoundPrice,
                    it.hallSound,
                    it.musicianId,
                    it.musicTag,
                    Constants.conductor
                )
            )
        }

        list?.forEach {
            searchListSession.add(
                HallSoundResponse(
                    it.id,
                    it.title,
                    it.titleJp,
                    it.composer,
                    it.conductor,
                    it.introduction,
                    it.tags,
                    it.recordTime,
                    it.releaseDate,
                    it.duration,
                    it.image,
                    it.organization,
                    it.organizationDiagram,
                    it.businessType,
                    it.band,
                    it.jasracLicenseNumber,
                    it.venueTitle,
                    it.venueDescription,
                    it.isConductorFavourite,
                    it.isHallsoundFavourite,
                    it.isSessionFavourite,
                    it.vrFile,
                    it.isBought,
                    it.conductorOrchestraPrice,
                    it.hallSoundPrice,
                    it.hallSound,
                    it.musicianId,
                    it.musicTag,
                    Constants.session
                )
            )
        }

        list?.forEach {
            searchListHallSound.add(
                HallSoundResponse(
                    it.id,
                    it.title,
                    it.titleJp,
                    it.composer,
                    it.conductor,
                    it.introduction,
                    it.tags,
                    it.recordTime,
                    it.releaseDate,
                    it.duration,
                    it.image,
                    it.organization,
                    it.organizationDiagram,
                    it.businessType,
                    it.band,
                    it.jasracLicenseNumber,
                    it.venueTitle,
                    it.venueDescription,
                    it.isConductorFavourite,
                    it.isHallsoundFavourite,
                    it.isSessionFavourite,
                    it.vrFile,
                    it.isBought,
                    it.conductorOrchestraPrice,
                    it.hallSoundPrice,
                    it.hallSound,
                    it.musicianId,
                    it.musicTag,
                    Constants.hallSound
                )
            )
        }

        searchList.addAll(searchListConductor)
        searchList.addAll(searchListSession)
        searchList.addAll(searchListHallSound)

        return searchList
    }
}