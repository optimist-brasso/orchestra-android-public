package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.favourite.FavouriteMusician
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object FavouriteRepository : BaseRepository() {

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.addFavourite(favouriteEntity.toJson(), token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun addSessionFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteSessionEntity?,
        token: String?
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.addSessionFavourite(favouriteEntity.toJson(), token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun addMusicianFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteMusician?,
        token: String?
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.addMusicianFavourite(token, favouriteEntity.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun addToCart(
        compositeDisposable: CompositeDisposable?,
        addTOCart: AddToCartList,
        token: String?
    ): Single<List<CartListResponse>> = Single.create { e ->
        apiService?.addToCart(token, addTOCart.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: listOf())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getFavourite(
        compositeDisposable: CompositeDisposable?,
        searchSlug: String?, token: String?
    ): Single<List<FavouriteResponse>> = Single.create { e ->
        apiService?.getFavourite(token, searchSlug)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data!!)
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getMyPlayerFavouriteList(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?,
        page: Int?
    ): Single<List<PlayerDetailResponse>> = Single.create { e ->
        apiService?.getMyFavouriteMusician(token, slug, page)
            .getSubscription()
            ?.subscribe({ response ->
                if (response.isSuccessful && response.body()?.data != null) {
                    val players = response?.body()?.data
                    val pagination = response?.body()?.meta?.pagination
                    players?.forEach {
                        it.hasNext =
                            (pagination?.page ?: 0) < (pagination?.totalPages ?: 0)
                    }
                    e.onSuccess(players ?: listOf())
                } else {
                    e.onError(getError(response.code(), response.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getMySessionFavouriteList(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?,
        page: Int?
    ): Single<List<FavouriteSessionResponse>> = Single.create { e ->
        apiService?.getFavouriteSession(token, slug, page)
            .getSubscription()
            ?.subscribe({ response ->
                if (response.isSuccessful && response.body()?.data != null) {
                    val players = response?.body()?.data
                    val pagination = response?.body()?.meta?.pagination
                    players?.forEach {
                        it.hasNext =
                            (pagination?.page ?: 0) < (pagination?.totalPages ?: 0)
                    }
                    e.onSuccess(players ?: listOf())
                } else {
                    e.onError(getError(response.code(), response.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}