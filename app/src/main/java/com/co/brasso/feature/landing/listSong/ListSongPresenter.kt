package com.co.brasso.feature.landing.listSong

import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants

class ListSongPresenter : BasePresenter<ListSongView>() {

    lateinit var interactor: ListSongInteractor

    override fun attachView(view: ListSongView) {
        super.attachView(view)
        interactor = ListSongInteractor()
    }

    fun getOrchestras(isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (isPullToRefresh)
                view.showLoading()
            else
                view.showProgressBar()
            getToken().flatMap { token ->
                interactor.getOrchestra(compositeDisposable, token)
            }.subscribe({
                if (isPullToRefresh)
                    view.hideLoading()
                else
                    view.hideProgressBar()
                view.setOrchestraList(getSearchList(it as MutableList<HallSoundResponse>?))
            }, {
                if (isPullToRefresh)
                    view.hideLoading()
                else
                    view.hideProgressBar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
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