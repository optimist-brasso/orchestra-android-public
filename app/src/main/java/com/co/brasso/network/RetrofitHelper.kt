package com.co.brasso.network

import com.co.brasso.BuildConfig
import com.co.brasso.utils.constant.ApiConstant

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitHelper() {

    private var apiService: ApiService? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    companion object {

        private var retrofitHelper: RetrofitHelper? = null

        fun getInstance(): RetrofitHelper? {
            if (retrofitHelper == null) {
                retrofitHelper = RetrofitHelper()
            }
            return retrofitHelper
        }
    }

    fun getInstance(): ApiService? {
        if (apiService == null) {
            RetrofitHelper()
        }
        return apiService
    }

    init {
        val gson = GsonBuilder().setLenient().create()

        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BuildConfig.baseUrl)
            .client(getOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(loggingInterceptor)
        }
        okHttpClient.networkInterceptors().add(addInterceptor())
        return okHttpClient.build()
    }

    private fun addInterceptor() = Interceptor { chain ->
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        requestBuilder.addHeader(ApiConstant.clientIdKey, BuildConfig.CLIENT_ID)
        requestBuilder.addHeader(ApiConstant.clientSecretKey, BuildConfig.CLIENT_SECRET)
        chain.proceed(requestBuilder.build())
    }
}