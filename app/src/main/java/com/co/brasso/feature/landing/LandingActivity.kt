package com.co.brasso.feature.landing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.co.brasso.R
import com.co.brasso.databinding.ActivityLandingBinding
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.feature.vlcPlayer.VLCPlayerActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.toJsonString
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils
import java.util.*

class LandingActivity : BaseActivity<LandingView, LandingPresenter>(), LandingView {

    private lateinit var binding: ActivityLandingBinding
    lateinit var navController: NavController
    var sessionLayoutResponse: SessionLayoutResponse? = null
    var instrumentResponse: InstrumentResponse? = null
    private lateinit var navHostFragment: NavHostFragment
    var shouldRefreshHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initNavBar()
        initListener()
        getCartItem()
        getIntentData()
        getStateInfo()
    }

    private fun getStateInfo() {
        when (StateManagement.pageName) {
            Constants.bundleManagement -> {
                if (getLoginState()) navFragment(navController, R.id.bundleManagementFragment)
            }

            Constants.cart -> {
                if (getLoginState()) navigateToCartFromStateManagement()
            }

            Constants.favourite -> {
                if (getLoginState()) navFragment(navController, R.id.favoriteFragment)
            }

            Constants.session -> {
                navigateToSessionLayout()
            }

            Constants.hallSound -> {
                navigateToHallSoundDetailFromStateManagement()
            }

            Constants.songsList -> {
                navFragment(navController, R.id.listSongFragment)
            }

            Constants.conductorDetail -> {
                navigateToConductorDetailFromStateManagement()
            }

            Constants.playerDetail -> {
                navigateToPlayerDetail()
            }

//            Constants.myPage -> {
//                if (!getLoginState()) navFragment(navController, R.id.myPageFragment)
//            }
        }
        StateManagement.pageName = ""
    }

    private fun navigateToCartFromStateManagement() {
        val bundle = bundleOf()
        bundle.putString(BundleConstant.fragmentType, Constants.homePage)
        bundle.putBoolean(BundleConstant.stateManagement, true)
        Router.navigateFragmentWithData(navController, R.id.cartListFragment, bundle)
    }

    private fun navigateToConductorDetailFromStateManagement() {
        val bundle = bundleOf(BundleConstant.orchestraId to StateManagement.id)
        bundle.putBoolean(BundleConstant.stateManagement, true)
        Router.navigateFragmentWithData(navController, R.id.conductorDetailFragment, bundle)
    }

    private fun navigateToSessionLayout() {
        val bundle = bundleOf(BundleConstant.orchestraId to StateManagement.id)
        bundle.putBoolean(BundleConstant.stateManagement, true)
        navController.navigate(R.id.sessionLayoutFragment, bundle)
    }

    private fun navigateToPlayerDetail() {
        val bundle = bundleOf(BundleConstant.orchestraId to StateManagement.id)
        bundle.putBoolean(BundleConstant.stateManagement, true)
        navController.navigate(R.id.playerDetailFragment, bundle)
    }

    private fun navigateToHallSoundDetailFromStateManagement() {
        val bundle = bundleOf(BundleConstant.orchestraId to StateManagement.id)
        bundle.putBoolean(BundleConstant.stateManagement, true)
        Router.navigateFragmentWithData(navController, R.id.hallSoundDetailFragment, bundle)
    }

    fun getIntentData() {
        val notificationData = intent?.extras
        val notificationValue = notificationData?.getString(BundleConstant.notificationValue)
        val notificationType = notificationData?.getString(BundleConstant.notificationType)
        if (!notificationType.isNullOrEmpty()) {
            when (notificationType.lowercase(Locale.getDefault())) {
                Constants.notificationDetail -> {
                    navigateToNotificationDetails(NotificationResponse(id = notificationValue?.toInt()))
                }
            }
        }
    }

    private fun navigateToNotificationDetails(notificationResponse: NotificationResponse?) {
        val bundle = Bundle()
        bundle.putSerializable(BundleConstant.notificationItem, notificationResponse)
        bundle.putBoolean(BundleConstant.firebaseNotification, true)
        navController.navigate(R.id.action_homeFragment_to_notification_detail, bundle)
        hideNotification()
    }

    private fun getCartItem() {
        if (!getLoginState()) {
            binding.incToolBar.cnlCartCount.viewGone()
        } else {
            presenter.getCartItem()
        }
    }

    private fun initListener() {
        binding.incToolBar.imgBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.incToolBar.imgNotify.setOnClickListener {
            navigateToNotificationListing()
            hideNotification()
        }

        binding.incToolBar.imgCart.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(this, getString(R.string.please_login), {
                    StateManagement.pageName = Constants.cart
                    navigateToLogin()
                }, {}, isCancelable = false, showNegativeBtn = true, getString(R.string.login)
                )
            } else {
                navigateToCartListing()
            }
        }
    }

    private fun initNavBar() {
        navHostFragment = binding.navHostFragment.getFragment()
        navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) navFragment(
                        navController, R.id.homeFragment
                    )
                }

                R.id.bundleManagementFragment -> {
                    if (navController.currentDestination?.id != R.id.bundleManagementFragment) {
                        if (!getLoginState()) {
                            DialogUtils.showAlertDialog(this,
                                getString(R.string.please_login),
                                {
                                    StateManagement.pageName = Constants.bundleManagement
                                    navigateToLogin()
                                },
                                {

                                },
                                isCancelable = false,
                                showNegativeBtn = true,
                                getString(R.string.login)
                            )
                            return@setOnItemSelectedListener false
                        } else {
                            navFragment(navController, R.id.bundleManagementFragment)
                        }
                    }
                }

                R.id.listSongFragment -> {
                    if (navController.currentDestination?.id != R.id.listSongFragment) navFragment(
                        navController, R.id.listSongFragment
                    )
                }

                R.id.favoriteFragment -> {
                    if (navController.currentDestination?.id != R.id.favoriteFragment) {
                        if (!getLoginState()) {
                            DialogUtils.showAlertDialog(this,
                                getString(R.string.please_login),
                                {
                                    StateManagement.pageName = Constants.favourite
                                    navigateToLogin()
                                },
                                {},
                                isCancelable = false,
                                showNegativeBtn = true,
                                getString(R.string.login)
                            )
                            return@setOnItemSelectedListener false
                        } else {
                            navFragment(
                                navController, R.id.favoriteFragment
                            )
                        }
                    }
                }

                R.id.myPageFragment -> {
                    StateManagement.pageName = Constants.myPage
                    navFragment(navController, R.id.myPageFragment)
                }
            }

            return@setOnItemSelectedListener true
        }
    }

    private fun navFragment(navController: NavController, fragmentId: Int) {
        navController.popBackStack()
        navController.navigate(fragmentId)
    }

    override fun onBackPressed() {
        binding.bottomNavigationView.viewVisible()
        if (navController.currentDestination?.id != R.id.homeFragment && navController.currentDestination?.id != R.id.bundleManagementFragment && navController.currentDestination?.id != R.id.favoriteFragment && navController.currentDestination?.id != R.id.myPageFragment && navController.currentDestination?.id != R.id.listSongFragment) {
            navController.popBackStack()
        } else if (navController.currentDestination?.id == R.id.homeFragment || navController.currentDestination?.id == R.id.bundleManagementFragment || navController.currentDestination?.id == R.id.favoriteFragment || navController.currentDestination?.id == R.id.myPageFragment || navController.currentDestination?.id == R.id.listSongFragment) {
            finish()
        }
    }

    override fun createPresenter() = LandingPresenter()

    private fun navigateToNotificationListing() {
        Router.navigateFragment(navController, R.id.notificationListing)
        binding.incToolBar.imgNotify.viewGone()
    }

    private fun navigateToCartListing() {
        val bundle = bundleOf()
        when (navController.currentDestination?.id) {
            R.id.sessionDetailFragment -> {
                bundle.putString(BundleConstant.fragmentType, Constants.session)
            }
            R.id.sessionDetailPremiumFragment -> {
                bundle.putString(BundleConstant.fragmentType, Constants.premium)
            }
            R.id.appendixFragment -> {
                bundle.putString(BundleConstant.fragmentType, Constants.appendix)
            }
            R.id.buyMultiPartFragment -> {
                bundle.putString(BundleConstant.fragmentType, Constants.multiPart)
            }
            R.id.hallSoundDetailFragment -> {
                bundle.putString(BundleConstant.fragmentType, Constants.hallSound)
            }
            else -> {
                bundle.putString(BundleConstant.fragmentType, Constants.homePage)
            }
        }
        Router.navigateFragmentWithData(navController, R.id.cartListFragment, bundle)
    }

    fun setToolBarTitle(title: String) {
        binding.incToolBar.txtFragment.text = title
        binding.incToolBar.txtFragment.viewVisible()
        binding.incToolBar.imgBack.viewGone()
    }

    fun hideToolBarTitle() {
        binding.incToolBar.txtFragment.viewGone()
        binding.incToolBar.imgBack.viewVisible()
    }

    fun showNotificationIcon() {
        binding.incToolBar.imgNotify.viewVisible()
        if (getLoginState()) setNotificationCount(AppInfoGlobal.notifications)
        else presenter.getNotifications()
    }

    fun showCartIcon() {
        binding.incToolBar.imgCart.viewVisible()
        if ((AppInfoGlobal.cartInfo?.size
                ?: 0) > 0
        ) binding.incToolBar.cnlCartCount.viewVisible() else binding.incToolBar.cnlCartCount.viewGone()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(Constants.volumeControl).apply {
                    putExtra(
                        Constants.keyType, getString(R.string.keyDown)
                    )
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val intent = Intent(Constants.volumeControl).apply {
                    putExtra(
                        Constants.keyType, getString(R.string.keyUp)
                    )
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
            KeyEvent.KEYCODE_BACK -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun showCartCount() {
        if (!getLoginState()) {
            binding.incToolBar.cnlCartCount.viewGone()
        } else {
            if ((AppInfoGlobal.cartInfo?.size
                    ?: 0) > 0){
                if (navController.currentDestination?.id != R.id.cartListFragment) binding.incToolBar.cnlCartCount.viewVisible()
                val cartItem = AppInfoGlobal.cartInfo?.size ?: 0
                binding.incToolBar.txvCartCount.text = cartItem.toString()
            } else{
                binding.incToolBar.cnlCartCount.viewGone()
            }
        }
    }

    fun proceedToFreePoints() {
        Router.navigateFragment(navController, R.id.freePointsListFragment)
    }

    override fun setNotificationCount(notifications: MutableList<NotificationResponse>?) {
        if (!notifications.isNullOrEmpty()) {
            val seenList = notifications.filter { !it.seenStatus }
            if (seenList.isNotEmpty()) {
                binding.incToolBar.cnlNotificationCount.viewVisible()
                binding.incToolBar.txvNotificationCount.text = seenList.size.toString()
            } else {
                binding.incToolBar.cnlNotificationCount.viewGone()
            }
        } else {
            binding.incToolBar.cnlNotificationCount.viewGone()
        }
    }

    fun hideToolbar() {
        binding.incToolBar.toolbar.viewGone()
    }

    fun showToolbar() {
        binding.incToolBar.toolbar.viewVisible()
    }

    fun hideNotification() {
        binding.incToolBar.imgNotify.viewGone()
        binding.incToolBar.cnlNotificationCount.viewGone()
    }

    fun hideCart() {
        binding.incToolBar.imgCart.viewGone()
        binding.incToolBar.cnlCartCount.viewGone()
    }

    override fun onResume() {
        super.onResume()
        if (navController.currentDestination?.id != R.id.cartListFragment) {
            showCartCount()
        }
    }

    fun showBottomBar() {
        binding.bottomNavigationView.viewVisible()
    }

    fun hideBottomBar() {
        binding.bottomNavigationView.viewGone()
    }

    fun openVrActivity(orchestra: HallSoundResponse?) {
//        orchestra?.isPremium =
//            !orchestra?.tags?.find { it.lowercase(Locale.getDefault()) == Constants.premium }
//                .isNullOrEmpty()
        Log.d("jp", orchestra.toJsonString())
        Router.navigateActivityWithParcelableData(
            this, false, BundleConstant.orchestraId, orchestra, VLCPlayerActivity::class
        )
    }

    fun setSelectedInstrument(instrumentResponse: InstrumentResponse?) {
        this.instrumentResponse = instrumentResponse
    }

    fun navigateToHallSoundDetail(orchestraId: Int?) {
        val bundle = bundleOf(BundleConstant.orchestraId to orchestraId)
        Router.navigateFragmentWithData(navController, R.id.hallSoundDetailFragment, bundle)
    }

    fun navigateToConductorDetail(orchestraId: Int?) {
        val bundle = bundleOf(BundleConstant.orchestraId to orchestraId)
        bundle.putString(BundleConstant.fragmentType, Constants.conductor)
        Router.navigateFragmentWithData(navController, R.id.conductorDetailFragment, bundle)
    }

    fun navigateToPartDetail(orchestraId: Int?, instrumentId: Int?, musicianId: Int?) {
        instrumentResponse = InstrumentResponse(
            instrument_id = instrumentId, sessionId = orchestraId, musicianId = musicianId
        )

        val bundle = bundleOf(Constants.session to instrumentResponse)
        Router.navigateFragmentWithData(navController, R.id.sessionDetailFragment, bundle)
    }

    fun navigateToPremiumDetail(orchestraId: Int?, instrumentId: Int?, musicianId: Int?) {
        instrumentResponse = InstrumentResponse(
            instrument_id = instrumentId, sessionId = orchestraId, musicianId = musicianId
        )

        val bundle = bundleOf(Constants.session to instrumentResponse)
        Router.navigateFragmentWithData(navController, R.id.sessionDetailPremiumFragment, bundle)
    }
}