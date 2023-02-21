package com.co.brasso.feature.shared.base

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.model.response.ErrorResponse
import com.co.brasso.network.RetrofitHelper
import com.co.brasso.utils.constant.Constants
import com.google.gson.Gson
import java.io.IOException

abstract class BaseRepository {

    private val retrofitHelper = RetrofitHelper.getInstance()
    val apiService = retrofitHelper?.getInstance()

    fun getDefaultError(throwable: Throwable) = if(throwable is IOException){
        throwable
    }else{
        Throwable(Constants.defaultErrorText)
    }



    fun getDefaultError() = Throwable(Constants.defaultErrorText)

    class UnAuthorizedError() : Throwable("401")

    class DiskFullError(msg:String) :Throwable(msg)

    fun getError(errorCode: Int?, error: String?): Throwable {
        try {
            val gson = Gson()
            val root = gson.fromJson(error, ErrorResponse::class.java)
            val errorMsg = root?.error
            val errorList = root?.errorList

            return if (errorCode == 401) {
                UnAuthorizedError()
            } else if (errorMsg != null) {
                Throwable(errorMsg.detail)
            } else if (!errorList.isNullOrEmpty())
                Throwable(errorList[0]?.detail)
            else
                getDefaultError()
        } catch (e: Exception) {
            return getDefaultError()
        }

    }

    fun get404Error(error: String?): String {
        val gson = Gson()
        val root = gson.fromJson(error, ErrorResponse::class.java)
        val errorMsg = root?.error
        val errorList = root?.errorList

        return if (errorMsg != null) {
            errorMsg.detail ?: ""
        } else if (!errorList.isNullOrEmpty())
            errorList[0]?.detail ?: ""
        else
            ""
    }

    protected fun closeDb(appDatabase: AppDatabase?) = appDatabase?.close()
}