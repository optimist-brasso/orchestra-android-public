package com.co.brasso.feature.landing.myPage.tab.myPage.recordList

import com.co.brasso.feature.shared.repositories.RecordingListRepository
import io.reactivex.disposables.CompositeDisposable

class RecordingFragmentInteractor {
    fun getRecording(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?,
        page: Int?
    ) =
        RecordingListRepository.getRecording(compositeDisposable, token, slug, page)
}