package com.co.brasso.network

import com.co.brasso.feature.shared.model.base.BaseArrayResponse
import com.co.brasso.feature.shared.model.base.BaseResponse
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfo
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse
import com.co.brasso.feature.shared.model.response.bundleList.BundleListResponse
import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.changepassword.ChangePasswordFragmentResponse
import com.co.brasso.feature.shared.model.response.emailverification.EmailFragmentResponse
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.home.HomeData
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.feature.shared.model.response.otp.OtpFragmentResponse
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.feature.shared.model.response.purchaseList.OrchestraPurchaseListResponse
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.feature.shared.model.response.registration.RegistrationResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.model.response.resetpassword.ResetPasswordResponse
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.feature.shared.model.updateProfile.UpdateProfileEntity
import com.co.brasso.utils.constant.ApiConstant
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(ApiConstant.socialLoginUrl)
    fun doSocialLogin(
        @Body requestBody: RequestBody?
    ): Single<Response<BaseResponse<Login>>>

    @Multipart
    @POST(ApiConstant.register)
    @JvmSuppressWildcards
    fun register(
        @PartMap params: Map<String, RequestBody?>,
        @Part file: MultipartBody.Part?,
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<RegistrationResponse>>>

    @POST(ApiConstant.logIn)
    fun emailLogin(
        @Body emailLoginEntity: EmailLoginEntity?
    ): Single<Response<BaseResponse<Login>>>

    @POST(ApiConstant.getOTP)
    fun getOTP(
        @Body email: RequestBody?
    ): Single<Response<BaseResponse<EmailFragmentResponse>>>

    @POST(ApiConstant.resendToken)
    fun resendToken(
        @Body email: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @POST(ApiConstant.tokenVerification)
    fun verifyOtp(
        @Body tokenVerification: RequestBody?
    ): Single<Response<BaseResponse<OtpFragmentResponse>>>

    @GET(ApiConstant.userProfile)
    fun getProfile(
        @Header(ApiConstant.authorization) authorization: String?
    ): Single<Response<BaseResponse<MyProfileResponse>>>

    @GET(ApiConstant.appInfoUrl)
    fun getAppInfo(): Single<Response<BaseResponse<AppInfo>>>

    @GET(ApiConstant.bannerUrl)
    fun getHomeData(@Header(ApiConstant.authorization) token: String): Single<Response<BaseResponse<HomeData>>>

    @POST(ApiConstant.changePassword)
    fun changePassword(
        @Header(ApiConstant.authorization) token: String, @Body changePassword: RequestBody?
    ): Single<Response<BaseResponse<ChangePasswordFragmentResponse>>>

    @POST(ApiConstant.forgotPassword)
    fun forgotPassword(
        @Body email: RequestBody?
    ): Single<Response<BaseResponse<EmailFragmentResponse>>>

    @POST(ApiConstant.resetPassword)
    fun resetPassword(
        @Body resetPassword: RequestBody?
    ): Single<Response<BaseResponse<ResetPasswordResponse>>>


    @PUT(ApiConstant.updateProfile)
    fun updateProfile(
        @Header(ApiConstant.authorization) token: String,
        @Body updateProfileEntity: UpdateProfileEntity?
    ): Single<Response<BaseResponse<MyProfileResponse>>>

    @Multipart
    @PUT(ApiConstant.updateProfilePic)
    fun updateProfilePic(
        @Header(ApiConstant.authorization) token: String, @Part file: MultipartBody.Part?
    ): Single<Response<BaseResponse<MyProfileResponse>>>

    @DELETE(ApiConstant.updateProfilePic)
    fun deleteProfilePic(
        @Header(ApiConstant.authorization) token: String
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.orchestraUrl)
    fun getOrchestra(@Header(ApiConstant.authorization) token: String): Single<Response<BaseArrayResponse<HallSoundResponse>>>

    @GET(ApiConstant.orchestraDetailUrl)
    fun getOrchestraDetail(
        @Header(ApiConstant.authorization) token: String,
        @Header(ApiConstant.videoSupport) videoSupport: String?,
        @Path(ApiConstant.id) id: Int?
    ): Single<Response<BaseResponse<HallSoundResponse>>>

    @GET(ApiConstant.playerList)
    fun getPlayerList(
        @Query(ApiConstant.keyword) searchSlug: String?, @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<PlayerDetailResponse>>>

    @GET(ApiConstant.playerDetail)
    fun getPlayerDetail(
        @Header(ApiConstant.authorization) token: String?, @Path(ApiConstant.id) id: Int?
    ): Single<Response<BaseResponse<PlayerDetailResponse>>>

    @POST(ApiConstant.addFavourite)
    fun addFavourite(
        @Body favouriteEntity: RequestBody?, @Header(ApiConstant.authorization) token: String
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.myFavourite)
    fun getFavourite(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) searchSlug: String?
    ): Single<Response<BaseArrayResponse<FavouriteResponse>>>

    @POST(ApiConstant.musicianFavourite)
    fun addMusicianFavourite(
        @Header(ApiConstant.authorization) token: String?, @Body favouriteEntity: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.myFavouriteMusician)
    fun getMyFavouriteMusician(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) slug: String?,
        @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<PlayerDetailResponse>>>

    @GET(ApiConstant.notification)
    fun getNotification(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseArrayResponse<NotificationResponse>>>

    @GET(ApiConstant.notificationDetail)
    fun getNotificationDetail(
        @Header(ApiConstant.authorization) token: String?, @Path(ApiConstant.id) id: String?
    ): Single<Response<BaseResponse<NotificationResponse>>>

    @GET(ApiConstant.sessionLayout)
    fun getSessionLayout(
        @Header(ApiConstant.authorization) token: String?, @Path(ApiConstant.id) id: Int?
    ): Single<Response<BaseResponse<SessionLayoutResponse>>>

    @GET(ApiConstant.recording)
    fun getRecording(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) searchSlug: String?,
        @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<RecordingListResponse>>>

    @GET(ApiConstant.orchestraPurchase)
    fun getPurchaseList(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<OrchestraPurchaseListResponse>>>

    @GET(ApiConstant.cartList)
    fun getCartList(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseArrayResponse<CartListResponse>>>

//    @GET(ApiConstant.billingHistory)
//    fun getBillingHistory(
//        @Header(ApiConstant.authorization) token: String?,
//    ): Single<Response<BaseArrayResponse<BillingHistoryResponse>>>

    @GET(ApiConstant.bundlePurchase)
    fun getBillingHistory(
        @Header(ApiConstant.authorization) token: String?,
    ): Single<Response<BaseArrayResponse<NewBillingHistoryResponse>>>


    @POST(ApiConstant.addToCart)
    fun addToCart(
        @Header(ApiConstant.authorization) token: String?, @Body favouriteEntity: RequestBody?
    ): Single<Response<BaseArrayResponse<CartListResponse>>>

    @DELETE(ApiConstant.deleteCartItem)
    fun deleteCartItem(
        @Header(ApiConstant.authorization) token: String?, @Path(ApiConstant.id) id: Int?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @POST(ApiConstant.purchaseCartItem)
    fun purchaseCartItem(
        @Header(ApiConstant.authorization) token: String?, @Body cartPurchase: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @POST(ApiConstant.emailPasswordUpdate)
    fun userEmailPasswordRegister(
        @Body userRegister: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.searchOrchestra)
    fun orchestraSearch(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) searchSlug: String?,
    ): Single<Response<BaseArrayResponse<HallSoundResponse>>>

    @GET(ApiConstant.instrumentDetail)
    fun getInstrumentDetail(
        @Header(ApiConstant.authorization) token: String?,
        @Header(ApiConstant.videoSupport) videoSupport: String?,
        @Path(ApiConstant.instrumentId) instrumentID: Int?,
        @Query(ApiConstant.orchestraId) sessionID: Int?,
        @Query(ApiConstant.musicianId) musicianId: Int?
    ): Single<Response<BaseResponse<InstrumentDetailResponse>>>

    @GET(ApiConstant.multiPartList)
    fun getMultiPartList(
        @Header(ApiConstant.authorization) token: String?,
        @Path(ApiConstant.orchestraId) sessionID: Int?
    ): Single<Response<BaseArrayResponse<MultiPartListResponseItem>>>

    @POST(ApiConstant.recording)
    @JvmSuppressWildcards
    @Multipart
    fun saveRecording(
        @Header(ApiConstant.authorization) token: String?,
        @PartMap params: Map<String, RequestBody?>,
        @Part file: MultipartBody.Part?
    ): Single<Response<BaseResponse<RegistrationResponse>>>

    @POST(ApiConstant.withdraw)
    fun proceedToWithdraw(
        @Header(ApiConstant.authorization) token: String?, @Body remarks: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.orchestraPlayerList)
    fun getOrchestraPlayerList(
        @Header(ApiConstant.authorization) token: String?,
        @Path(ApiConstant.id) orchestraId: Int?, @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<PlayerDetailResponse>>>

    @POST(ApiConstant.googlePay)
    fun paymentVerify(
        @Header(ApiConstant.authorization) token: String?, @Body remarks: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @POST(ApiConstant.registerFcm)
    fun registerFcm(
        @Header(ApiConstant.authorization) token: String?, @Body remarks: RequestBody?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @DELETE(ApiConstant.unRegisterFcm)
    fun unRegisterFcm(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.bundleList)
    fun getBundleList(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<BundleListResponse>>>

    @GET(ApiConstant.freePointList)
    fun getFreeBundleList(
        @Header(ApiConstant.authorization) token: String?, @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<FreePointListResponse>>>

    @POST(ApiConstant.addSessionFavourite)
    fun addSessionFavourite(
        @Body favouriteEntity: RequestBody?, @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @GET(ApiConstant.myFavouriteSession)
    fun getFavouriteSession(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) searchSlug: String?,
        @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<FavouriteSessionResponse>>>

    @GET(ApiConstant.searchSession)
    fun sessionSearch(
        @Header(ApiConstant.authorization) token: String?,
        @Query(ApiConstant.keyword) searchSlug: String?,
        @Query(ApiConstant.page) pageNo: Int?
    ): Single<Response<BaseArrayResponse<FavouriteSessionResponse>>>

    @POST(ApiConstant.notificationPermission)
    fun setNotificationStatus(
        @Header(ApiConstant.authorization) token: String?
    ): Single<Response<BaseResponse<SuccessResponse>>>

    @POST(ApiConstant.updateDownloadCount)
    fun updateDownloadCount(
    ): Single<Response<BaseResponse<SuccessResponse>>>
}