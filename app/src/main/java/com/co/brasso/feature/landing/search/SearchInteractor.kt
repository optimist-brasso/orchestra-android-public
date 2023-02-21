package com.co.brasso.feature.landing.search

import com.co.brasso.feature.shared.repositories.SearchRepository
import io.reactivex.disposables.CompositeDisposable

class SearchInteractor {
    fun orchestraSearch(token: String?, slug: String?, compositeDisposable: CompositeDisposable?) =
        SearchRepository.getSearchData(compositeDisposable, token, slug)
}