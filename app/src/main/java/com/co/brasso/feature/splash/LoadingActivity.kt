package com.co.brasso.feature.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.co.brasso.R
import com.co.brasso.databinding.ActivityLoadingBinding
import com.co.brasso.feature.auth.signUp.SignUpActivity
import com.co.brasso.feature.auth.signUpOptions.SignUpOptionActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.onboarding.OnBoardingActivity
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.Logger
import com.co.brasso.utils.util.PreferenceUtils
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import kotlin.reflect.KClass


class LoadingActivity : BaseActivity<LoadingView, LoadingPresenter>(), LoadingView,
    AnimationListener {

    private var animationCompleted: Boolean? = false
    private var apiCallSuccess: Boolean? = false
    private var profileApiCalled: Boolean? = false

    lateinit var binding: ActivityLoadingBinding
    private lateinit var gifDrawable: GifDrawable
    private var myProfileResponse: MyProfileResponse? = null
    var billingClient: BillingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setup()

    }

    private fun setup() {
        initListeners()
        initGoogleInAppBilling()
    }

    private fun initGoogleInAppBilling() {
        if (billingClient != null) return
        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener)
            .enablePendingPurchases().build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                callAppInfoApi()
            }

            override fun onBillingServiceDisconnected() {
                finish()
            }
        })
    }

    fun callAppInfoApi() {
        if (isNetworkAvailable()) presenter.getAppInfo(billingClient, this@LoadingActivity)
        else {
            showMessageDialog(R.string.error_no_internet_connection) {
                recallApi()
            }
        }
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases -> }

    private fun initListeners() {
        gifDrawable = binding.imvImage.drawable as GifDrawable
        gifDrawable.loopCount = 1
        gifDrawable.addAnimationListener(this)
    }

    override fun createPresenter() = LoadingPresenter()

    override fun openStartingPage() {
        val walkThroughSeen = PreferenceUtils.getWalkTroughSeen(this)
        if (walkThroughSeen) {
            if (!PreferenceUtils.getLoginState(this)) {
                apiCallSuccess = true
                if (animationCompleted == true) {
                    startActivity(getNotificationDataIntent(SignUpOptionActivity::class))
                    finish()
                }
            } else {
                profileApiCalled = true
                presenter.getProfile()
            }

        } else {
            apiCallSuccess = true
            if (animationCompleted == true) {
                val intent = getNotificationDataIntent(OnBoardingActivity::class)
                intent.putExtra(BundleConstant.onBoarding, StringConstants.onBoardingFromSplash)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun hasNotificationIntentData(): Boolean {
        val notificationData = intent.extras
        if (notificationData?.getString(BundleConstant.notificationValue)
                .isNullOrEmpty()
        ) return false
        return true
    }

    private fun getNotificationDataIntent(className: KClass<*>): Intent {
        val notificationData = intent.extras
        val intent = Intent(this, className.java)
        Logger.d("jp", notificationData?.getString(BundleConstant.notificationValue) ?: "")
        Logger.d(
            "jpNotificationValue",
            notificationData?.getString(BundleConstant.notificationType) ?: ""
        )
        intent.putExtra(
            BundleConstant.notificationValue,
            notificationData?.getString(BundleConstant.notificationValue)
        )
        intent.putExtra(
            BundleConstant.notificationType,
            notificationData?.getString(BundleConstant.notificationType)
        )
        return intent
    }

    override fun closeApp() {
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun setProfileData(myProfileResponse: MyProfileResponse?) {
        apiCallSuccess = true
        this.myProfileResponse = myProfileResponse
        AppInfoGlobal.myProfileResponse = myProfileResponse
        updateProfileFlow(myProfileResponse)
    }

    override fun recallApi() {
        callAppInfoApi()
    }

    private fun updateProfileFlow(myProfileResponse: MyProfileResponse?) {
        if (myProfileResponse?.profileStatus == true) {
            if (animationCompleted == true) {
                navigateToLanding()
            }
        } else {
            if (animationCompleted == true) {
                val intent = getNotificationDataIntent(SignUpActivity::class)
                intent.putExtra(BundleConstant.profileStatus, myProfileResponse?.profileStatus)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun showProgress() {
        binding.pgbLoading.viewVisible()
    }

    override fun hideProgress() {
        binding.pgbLoading.viewGone()
    }

    private fun navigateToLanding() {
        startActivity(getNotificationDataIntent(LandingActivity::class))
        finish()
    }

    override fun onAnimationCompleted(loopNumber: Int) {
        animationCompleted = true
        if (profileApiCalled == true && apiCallSuccess == true) {
            binding.imvImage.viewVisible()
            binding.pgbLoading.viewVisible()
            updateProfileFlow(myProfileResponse)
        } else if (apiCallSuccess == true) {
            binding.imvImage.viewVisible()
            binding.pgbLoading.viewVisible()
            onAnimationCompleteFlow()
        } else {
            if (!isNetworkAvailable()) showMessageDialog(R.string.error_no_internet_connection) { recallApi() }
            hideProgress()
        }
    }

    private fun onAnimationCompleteFlow() {
        val walkThroughSeen = PreferenceUtils.getWalkTroughSeen(this)
        if (walkThroughSeen) {
            if (!PreferenceUtils.getLoginState(this)) {
                if (!hasNotificationIntentData()) {
                    startActivity(getNotificationDataIntent(SignUpOptionActivity::class))
                    finish()
                } else {
                    navigateToLanding()
                }
            }
        } else {
            val intent = getNotificationDataIntent(OnBoardingActivity::class)
            intent.putExtra(BundleConstant.onBoarding, StringConstants.onBoardingFromSplash)
            startActivity(intent)
            finish()
        }
    }
}