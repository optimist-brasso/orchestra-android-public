package com.co.brasso.utils.extension

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Single<T>?.getSubscription(): Single<T>? =
    this?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>?.setDelay(timeInSeconds: Long = 2L): Single<T>? =
    this?.delay(timeInSeconds, TimeUnit.SECONDS)


fun <T> Observable<T>?.getSubscription(): Observable<T>? =
    this?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())