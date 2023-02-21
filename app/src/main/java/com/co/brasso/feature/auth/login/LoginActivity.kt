package com.co.brasso.feature.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.co.brasso.R
import com.co.brasso.databinding.ActivityLoginBinding
import com.co.brasso.feature.auth.forgotPassword.ForgotPasswordActivity
import com.co.brasso.feature.auth.signUpOptions.SignUpOptionActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.feature.splash.LoadingActivity
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
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
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import kotlin.reflect.KClass

class LoginActivity : BaseActivity<LoginView, LoginPresenter>(),
    LoginView, View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private var firebaseAuth: FirebaseAuth? = null
    private val email = "email"
    private val publicProfile = "public_profile"
    private var callBackManager: CallbackManager? = null
    private var onBackPressed: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setup()
    }

    private fun setup() {
        setListeners()
        initializeFirebase()
        setValue()
        hideViews()
    }

    private fun hideViews() {
        binding.incToolBar.txtFragment.viewGone()
        binding.incToolBar.imgBack.viewGone()
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private val startForResultForLine = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == RESULT_OK) {
                val intent: Intent = result.data ?: return@ActivityResultCallback
                val loginResult = LineLoginApi.getLoginResultFromIntent(intent)
                handleLineResult(loginResult)
            }
        })

    private fun setListeners() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.txtForgotPassword.setOnClickListener(this)
        binding.btnLoginWithLine.setOnClickListener(this)
        binding.btnLoginWithTwitter.setOnClickListener(this)
        binding.btnLoginWithFacebook.setOnClickListener(this)
        binding.incCancel.llyCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogin -> {
                hideKeyboard(binding.root)
                proceedToLogin()
            }
            R.id.btnRegister -> {
                if (onBackPressed == true)
                    finish()
                else
                    Router.navigateActivityWithBooleanData(this, false, Constants.backPressed,
                        onBackPressed, SignUpOptionActivity::class)
            }
            R.id.txtForgotPassword -> {
                navigateToResetPassword()
            }
            R.id.incCancel -> {
                navigateToMainAsGuest()
            }
            R.id.btnLoginWithLine -> {
                if (isNetworkAvailable()) proceedWithLineLogin()
                else showMessageDialog(R.string.error_no_internet_connection)
            }
            R.id.btnLoginWithTwitter -> {
                if (isNetworkAvailable()) proceedTwitterLogin()
                else showMessageDialog(R.string.error_no_internet_connection)
            }
            R.id.btnLoginWithFacebook -> {
                if (isNetworkAvailable()) proceedLoginWithFacebook()
                else showMessageDialog(R.string.error_no_internet_connection)
            }
        }
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

    private fun proceedTwitterLogin() {
        FirebaseAuth.getInstance().signOut()
        val pendingResultTask =
            firebaseAuth?.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener {
                        if (it != null)
                            handleTwitterLogin(it)
                        else {
                            showMessage(R.string.text_error_in_twitter_login)
                        }
                    })
                .addOnFailureListener {
                    showMessage(it.localizedMessage?.toString() ?: "")
                    Log.d("error", it.localizedMessage?.toString() ?: "")
                }
        } else {
            startSignInFlow()
        }
    }

    private fun handleTwitterLogin(authResult: AuthResult) {
        val accessToken = (authResult.credential as OAuthCredential).accessToken
        val secret = (authResult.credential as OAuthCredential).secret
        val socialLoginRequest = SocialLoginRequest(
            accessToken,
            ApiConstant.typeTwitter,
            ApiConstant.grantTypeSocial,
            secret
        )
        presenter.doSocialLogin(socialLoginRequest)
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
            ApiConstant.grantTypeSocial,
        )
        presenter.doSocialLogin(socialLoginRequest)
    }

    private fun startSignInFlow() {
        val provider: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "en")
        firebaseAuth
            ?.startActivityForSignInWithProvider(this, provider.build())
            ?.addOnSuccessListener { authResult ->
                if (authResult != null)
                    handleTwitterLogin(authResult)
            }?.addOnFailureListener { exception ->
                Log.d(
                    "exception",
                    exception.localizedMessage ?: ""
                )
            }
    }

    private fun proceedWithLineLogin() {
        try {
            val loginIntent: Intent = LineLoginApi.getLoginIntent(
                this,
                Constants.channelId,
                LineAuthenticationParams.Builder()
                    .scopes(listOf(Scope.PROFILE))
                    .build()
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
        Log.d("lineAccessToken", accessToken.toString())
        if (accessToken != null) {
            handleLineLogin(accessToken)
        }
    }

    private fun handleLineLogin(accessToken: String) {
        val socialLoginRequest = SocialLoginRequest(
            accessToken,
            ApiConstant.typeLine,
            ApiConstant.grantTypeSocial,
        )
        presenter.doSocialLogin(socialLoginRequest)

    }

    private fun navigateToRegister() {
        Router.navigateActivity(this, false, SignUpOptionActivity::class)
    }

    private fun navigateToResetPassword() {
        Router.navigateActivity(this, false, ForgotPasswordActivity::class)
    }

    private fun navigateToLanding() {
        Router.navigateClearingAllActivity(this, LandingActivity()::class)
    }

    private fun setValue() {
        binding.incToolBar.txtFragment.text = getString(R.string.my_page)
        binding.incToolBar.txtFragment.textSize = 22F
        binding.incToolBar.imgCart.viewGone()
        binding.incToolBar.imgCartItem.viewGone()
        binding.incToolBar.txvCartCount.viewGone()
        binding.incToolBar.imgNotify.viewGone()
        binding.incCancel.txvCancel.text = getString(R.string.not_yet)
        onBackPressed = intent.getBooleanExtra(Constants.backPressed, false)
    }

    private fun proceedToLogin() {
        hideKeyboard(binding.root)
        val email = binding.edtEmailAddress.text.toString()
        val password = binding.edtPassword.text.toString()
        val emailLoginEntity = EmailLoginEntity()
        emailLoginEntity.email = email
        emailLoginEntity.password = password
        emailLoginEntity.grantType = ApiConstant.grantTypePassword
        presenter.emailLogin(emailLoginEntity)
    }

    override fun createPresenter(): LoginPresenter = LoginPresenter()

    override fun success(successMSG: Int) {
        navigateToLanding()
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG ?: "")
    }

    override fun showEmailError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }

    override fun showPasswordError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }

    override fun navigateToMain() {
        val intent = getNotificationDataIntent(LoadingActivity::class)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun setLoginType(loginType: String) {
        PreferenceUtils.setLoginType(this, loginType)
    }

    private fun navigateToMainAsGuest() {
//        if (onBackPressed == true) {
//            onBackPressed()
//        } else {
            Router.navigateClearingAllActivity(this, LandingActivity::class)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callBackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}