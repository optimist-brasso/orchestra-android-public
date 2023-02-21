package com.co.brasso.feature.auth.signUpOptions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.co.brasso.R
import com.co.brasso.databinding.ActivitySignUpOptionBinding
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.auth.signUp.SignUpActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.feature.splash.LoadingActivity
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.ApiConstant.grantTypeSocial
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.Logger
import com.co.brasso.utils.util.PreferenceUtils
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.jakewharton.rxbinding3.view.clicks
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import kotlin.reflect.KClass


class SignUpOptionActivity : BaseActivity<SignUpOptionView, SignUpOptionPresenter>(),
    SignUpOptionView {
    private lateinit var binding: ActivitySignUpOptionBinding

    private var callBackManager: CallbackManager? = null
    private val email = "email"
    private var termsAccepted: Boolean? = false
    private val publicProfile = "public_profile"
    private var firebaseAuth: FirebaseAuth? = null
    private var onBackPressed: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpOptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUp()
        initializeFirebase()
        setOnClickListener()
    }

    private fun setUp() {
        binding.incCancel.txvCancel.text = getString(R.string.not_yet)
        onBackPressed = intent.getBooleanExtra(Constants.backPressed, true)
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private val startForResultForLine =
        registerForActivityResult(StartActivityForResult(), ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent = result.data ?: return@ActivityResultCallback
                val loginResult = LineLoginApi.getLoginResultFromIntent(intent)
                handleLineResult(loginResult)
            }
        })

    private fun setOnClickListener() {

        binding.chbTermAndCondition.setOnClickListener {
            termsAccepted = binding.chbTermAndCondition.isChecked
        }

        binding.btnLoginWithLine.clicks().throttle()?.subscribe {
            proceedToLoginAndRegistration({
                if (isNetworkAvailable()) proceedWithLineLogin()
                else showMessageDialog(R.string.error_no_internet_connection)
            }, termsAccepted)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.btnLoginWithTwitter.clicks().throttle()?.subscribe {
            proceedToLoginAndRegistration({
                if (isNetworkAvailable()) proceedTwitterLogin()
                else showMessageDialog(R.string.error_no_internet_connection)
            }, termsAccepted)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.btnLoginWithFacebook.clicks().throttle()?.subscribe {
            proceedToLoginAndRegistration({
                if (isNetworkAvailable()) proceedLoginWithFacebook()
                else showMessageDialog(R.string.error_no_internet_connection)
            }, termsAccepted)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.btnRegister.clicks().throttle()?.subscribe {
            proceedToLoginAndRegistration({ proceedToRegister() }, termsAccepted)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.btnNavToLogin.clicks().throttle()?.subscribe {
            navigateToLoginPage()
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incCancel.llyCancel.clicks().throttle()?.subscribe {
            continueAsGuest()
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.txtTermAndCondition.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun validateNetwork() {
        if (!isNetworkAvailable()) {
            showMessageDialog(R.string.error_no_internet_connection)
            return
        }
    }

    override fun createPresenter(): SignUpOptionPresenter {
        return SignUpOptionPresenter()
    }

    private fun proceedToLoginAndRegistration(
        loginAndRegister: () -> Unit, termsAccepted: Boolean?
    ) {
        if (termsAccepted == true) {
            loginAndRegister()
        } else {
            showMessage(R.string.please_check_term_and_condition)
        }
    }

    private fun proceedWithLineLogin() {
        try {
            val loginIntent: Intent = LineLoginApi.getLoginIntent(
                this,
                Constants.channelId,
                LineAuthenticationParams.Builder().scopes(listOf(Scope.PROFILE)).build()
            )
            launchLineResultIntent(loginIntent)

        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    private fun launchLineResultIntent(loginIntent: Intent) {
        startForResultForLine.launch(loginIntent)
    }

    private fun handleLineResult(result: LineLoginResult) {
        val accessToken = result.lineCredential?.accessToken?.tokenString
        if (accessToken != null) {
            handleLineLogin(accessToken)
        }
    }

    private fun handleLineLogin(accessToken: String) {
        val socialLoginRequest = SocialLoginRequest(
            accessToken,
            ApiConstant.typeLine,
            grantTypeSocial,
        )
        presenter.doSocialLogin(socialLoginRequest)
    }

    private fun proceedTwitterLogin() {
        val pendingResultTask = firebaseAuth?.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(OnSuccessListener {
                if (it != null) handleTwitterLogin(it)
                else {
                    showMessage(R.string.text_error_in_twitter_login)
                }
            }).addOnFailureListener {
                showMessage(it.localizedMessage?.toString() ?: "")
            }
        } else {
            startSignInFlow()
        }
    }

    private fun startSignInFlow() {
        val provider: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "en")
        firebaseAuth?.startActivityForSignInWithProvider(this, provider.build())
            ?.addOnSuccessListener { authResult ->
                if (authResult != null) handleTwitterLogin(authResult)
            }?.addOnFailureListener { exception -> Log.d("exception", exception.localizedMessage) }
    }

    private fun handleTwitterLogin(authResult: AuthResult) {
        val accessToken = (authResult.credential as OAuthCredential).accessToken
        val secret = (authResult.credential as OAuthCredential).secret
        val socialLoginRequest = SocialLoginRequest(
            accessToken, ApiConstant.typeTwitter, grantTypeSocial, secret
        )
        presenter.doSocialLogin(socialLoginRequest)
    }

    private fun proceedToRegister() {
        Router.navigateActivity(this, false, SignUpActivity::class)
    }

    private fun proceedLoginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf(publicProfile, email))
        callBackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    Log.d("error", error.message.toString())
                }

                override fun onSuccess(result: LoginResult) {
                    doFacebookLogin(result.accessToken.token)
                }
            })
    }

    private fun doFacebookLogin(token: String) {
        val socialLoginRequest = SocialLoginRequest(
            token,
            ApiConstant.typeFacebook,
            grantTypeSocial,
        )
        presenter.doSocialLogin(socialLoginRequest)
    }

    override fun setLoginType(loginType: String) {
        PreferenceUtils.setLoginType(this, loginType)
    }

    override fun navigateToMain() {
        //  Router.navigateClearingAllActivity(this, LandingActivity::class)
        val intent = getNotificationDataIntent(LoadingActivity::class)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right, R.anim.slide_out_left
        )
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

    private fun navigateToLoginPage() {
        // PreferenceUtils.setGuestLogin(this, true)
        if (onBackPressed == false) {
            finish()
        } else {
            Router.navigateActivityWithBooleanData(
                this, false, Constants.backPressed, true, LoginActivity::class
            )
        }
    }

    private fun continueAsGuest() {
        Router.navigateClearingAllActivity(this, LandingActivity::class)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callBackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
