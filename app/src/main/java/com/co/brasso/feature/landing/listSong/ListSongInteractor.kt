package com.co.brasso.feature.landing.listSong

import com.co.brasso.feature.shared.repositories.OrchestraRepository
import io.reactivex.disposables.CompositeDisposable

class ListSongInteractor {
    fun getOrchestra(
        compositeDisposable: CompositeDisposable?,
        token: String
    ) = OrchestraRepository.getOrchestra(compositeDisposable, token)
}