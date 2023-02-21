package com.co.brasso.utils.extension

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>?.throttle(timeInMilliSeconds: Long = 2000L): Observable<T>? =
    this?.throttleFirst(timeInMilliSeconds, TimeUnit.MILLISECONDS)