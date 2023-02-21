package com.co.brasso.feature.landing.home

import android.util.Log
import com.co.brasso.R
import com.co.brasso.feature.landing.myPage.tab.myPage.points.PointsListInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.home.Recommendation
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.getSubscription

class HomePresenter : BasePresenter<HomeView>() {

    private var interactor: HomeInteractor? = null
    private var bundleListInteractor: PointsListInteractor? = null

    override fun attachView(view: HomeView) {
        super.attachView(view)
        interactor = HomeInteractor()
        bundleListInteractor = PointsListInteractor()
    }

    override fun detachView() {
        super.detachView()
        interactor = null
        bundleListInteractor = null
    }

    fun getBanner(pullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (pullToRefresh) view.showLoading()
                else view.showProgressBar()
                getToken().flatMap { token ->
                    interactor?.getBanner(compositeDisposable, token)
                }.subscribe({
                    Log.d("JpApiData", it.homeData.toString())
                    if (pullToRefresh) view.hideLoading()
                    else view.hideProgressBar()
                    view.setHomeData(it.homeData)
                    if (view.getLoginState()) {
                        AppInfoGlobal.notifications =
                            it.notificationResponse as MutableList<NotificationResponse>?
                        view.updateNotificationCount()
                    } else {
                        saveInDataBase(it.notificationResponse as MutableList<NotificationResponse>?)
                    }

                }, {
                    if (pullToRefresh) view.hideLoading()
                    else view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }


    private fun saveInDataBase(apiResponse: MutableList<NotificationResponse>?) {
        ifViewAttached { view ->
            interactor?.getAllNotifications(compositeDisposable, view.getAppDatabase())?.flatMap {
                val newDataBaseList: MutableList<NotificationResponse> = mutableListOf()
                apiResponse?.forEach { apiData ->
                    val databaseData = it.find { it.id == apiData.id }
                    if (databaseData != null) {
                        newDataBaseList.add(databaseData)
                    } else {
                        newDataBaseList.add(apiData)
                    }
                }
                interactor?.deleteNotifications(view.getAppDatabase())?.flatMap {
                    interactor?.saveNotificationsToDb(
                        view.getAppDatabase(), newDataBaseList
                    )?.getSubscription()
                }?.getSubscription()
            }?.subscribe({
                view.updateNotificationCount()
            }, {

            }).let {
                if (it != null) compositeDisposable?.add(it)
            }
        }
    }

    fun getFilteredRecommendedList(recommendedList: MutableList<Recommendation>?): MutableList<Recommendation> {
        val prList: MutableList<Recommendation> = mutableListOf()
        val nonPrList: MutableList<Recommendation> = mutableListOf()
        val filteredList: MutableList<Recommendation> = mutableListOf()

        recommendedList?.forEach { it ->
            if (it.tags?.find { it -> it.lowercase() == StringConstants.pr } != null) {
                val prResult = it.tags?.find { tag -> tag.lowercase() == StringConstants.pr }
                val index = it.tags?.indexOf(prResult)
                if (index != 0) {
                    it.tags?.removeAt(index ?: 0)
                    it.tags?.add(0, prResult ?: "")
                }
                prList.add(it)
            } else {
                nonPrList.add(it)
            }
        }
        val sortedPrList = prList.sortedByDescending { it.releaseDate }
        val sortedNonPrList = nonPrList.sortedByDescending { it.releaseDate }

        repeat(sortedPrList.size) {
            filteredList.add(sortedPrList[it])
        }

        repeat(sortedNonPrList.size) {
            filteredList.add(sortedNonPrList[it])
        }

        return filteredList
    }

    fun getBundleList() {
        ifViewAttached { view ->
            getToken().flatMap { token ->
                bundleListInteractor?.getBundleList(compositeDisposable, token)
            }.subscribe({
                view.setBundleList(it)
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }


}