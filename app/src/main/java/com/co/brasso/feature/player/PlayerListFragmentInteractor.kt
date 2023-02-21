package com.co.brasso.feature.player

import com.co.brasso.feature.shared.repositories.PlayerListRepository
import io.reactivex.disposables.CompositeDisposable

class PlayerListFragmentInteractor {
    fun getPlayerList(compositeDisposable: CompositeDisposable?,slug:String?,page:Int?) =
        PlayerListRepository.getPlayerList(compositeDisposable,slug,page)
}