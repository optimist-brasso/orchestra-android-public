package com.co.brasso.feature.landing.myPage.tab.myPage.points.freePoints

import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse
import com.co.brasso.feature.shared.repositories.BundleListRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class FreePointsListInteractor {
    fun getFreeBundleList(
        compositeDisposable: CompositeDisposable?,
        token: String, page: Int?
    ): Single<List<FreePointListResponse>> =
        BundleListRepository.getFreePointsList(compositeDisposable, token, page)

}