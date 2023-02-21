package com.co.brasso.feature.orchestraPlayer

import com.co.brasso.feature.shared.repositories.OrchestraPlayerRepository
import io.reactivex.disposables.CompositeDisposable

class OrchestraPlayerListFragmentInteractor {
    fun getPlayerList(compositeDisposable: CompositeDisposable?,token:String?, orchestraId: Int?, page: Int?) =
        OrchestraPlayerRepository.getPlayerList(compositeDisposable,token, orchestraId,page)
}