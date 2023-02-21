package com.co.brasso.feature.landing.myPage.tab.myPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentMyPageMenuBinding
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.PreferenceUtils

class MyPageMenuFragment : BaseFragment<MyPageMenuFragmentView, MyPageMenuFragmentPresenter>(),
    MyPageMenuFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentMyPageMenuBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            binding = FragmentMyPageMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
        initToolbar()
    }

    private fun setUp() {
        initListeners()
        setValue()
        hideViewForGuestLogin()
        hideChangePasswordTab()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
        (activity as? LandingActivity)?.showBottomBar()
    }

    override fun createPresenter() = MyPageMenuFragmentPresenter()

    private fun hideChangePasswordTab() {
        if (getLoginType() == StringConstants.socialLogin) {
            binding.txtChangePassword.visibility = View.GONE
            binding.imgChangePasswordTab.visibility = View.GONE
            binding.vwChangePassword.visibility = View.GONE
        }
    }

    private fun hideViewForGuestLogin() {
        if (!getLoginState()) {
            binding.txtProfile.visibility = View.GONE
            binding.imgProfileTab.visibility = View.GONE
            binding.vwProfile.visibility = View.GONE
            binding.txtChangePassword.visibility = View.GONE
            binding.imgChangePasswordTab.visibility = View.GONE
            binding.vwChangePassword.visibility = View.GONE
            binding.txtListContent.visibility = View.GONE
            binding.imgListContentTab.visibility = View.GONE
            binding.vwListContent.visibility = View.GONE
            binding.txtBillingHistory.visibility = View.GONE
            binding.imgBillingHistoryTab.visibility = View.GONE
            binding.vwBillingHistory.visibility = View.GONE
            binding.txtWithDrawl.visibility = View.GONE
            binding.imgWithDrawl.visibility = View.GONE
            binding.vwWithDrawl.visibility = View.GONE
            binding.vwRecordingList.viewGone()
            binding.txtRecordingList.viewGone()
            binding.imgRecordingListTab.viewGone()

        }
    }

    private fun setValue() {
        if (!getLoginState()) {
            binding.txtLogout.text = getString(R.string.login)
        } else {
            binding.txtLogout.text = getString(R.string.logout)
        }
    }

    private fun initListeners() {
        binding.txtProfile.setOnClickListener(this)
        binding.imgProfileTab.setOnClickListener(this)
        binding.txtChangePassword.setOnClickListener(this)
        binding.imgChangePasswordTab.setOnClickListener(this)
        binding.txtListContent.setOnClickListener(this)
        binding.imgListContentTab.setOnClickListener(this)
        binding.txtRecordingList.setOnClickListener(this)
        binding.imgRecordingListTab.setOnClickListener(this)
        binding.txtLogout.setOnClickListener(this)
        binding.imgLogoutTab.setOnClickListener(this)
        binding.txtBillingHistory.setOnClickListener(this)
        binding.imgBillingHistoryTab.setOnClickListener(this)
        binding.txtWithDrawl.setOnClickListener(this)
        binding.imgWithDrawl.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtProfile -> {
                navigateToProfile()
            }
            R.id.imgProfileTab -> {
                navigateToProfile()
            }
            R.id.txtChangePassword -> {
                navigateToChangePassword()
            }
            R.id.imgChangePasswordTab -> {
                navigateToChangePassword()
            }
            R.id.txtListContent -> {
                navigateToPurchaseList()
            }
            R.id.imgListContentTab -> {
                navigateToPurchaseList()
            }
            R.id.txtRecordingList -> {
                navigateToRecordingList()
            }
            R.id.imgRecordingListTab -> {
                navigateToRecordingList()
            }
            R.id.txtLogout -> {
                proceedToLogout()
            }
            R.id.imgLogoutTab -> {
                proceedToLogout()
            }
            R.id.txtBillingHistory -> {
                proceedToBillingHistory()
            }
            R.id.imgBillingHistoryTab -> {
                proceedToBillingHistory()
            }
            R.id.txtWithDrawl -> {
                proceedToWithdrawal()
            }
            R.id.imgWithDrawl -> {
                proceedToWithdrawal()
            }
        }
    }

    private fun proceedToWithdrawal() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.withdrawalFragment)
    }

    private fun navigateToPurchaseList() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.purchaseListFragment)
    }

    private fun navigateToRecordingList() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.recordingListFragment)
    }

    private fun proceedToBillingHistory() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.billingHistoryFragment)
    }

    private fun navigateToChangePassword() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.changePasswordFragment)
    }

    private fun navigateToProfile() {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.myProfileFragment)
    }

    private fun proceedToLogout() {
        if (binding.txtLogout.text.equals(getString(R.string.login))) {
            StateManagement.pageName = Constants.myPage
            Router.navigateActivityWithBooleanData(
                requireContext(),
                false,
                Constants.backPressed,
                false,
                LoginActivity::class
            )
        } else {
            DialogUtils.showLogoutDialog(
                requireContext(),
                {
                    presenter.unRegisterFcm()
                }, false
            )
        }
    }

    override fun logoutSuccessfully() {
        PreferenceUtils.clearAll(context)
        DialogUtils.showLogoutSuccessDialog(
            requireContext(), {
                logoutToMain()
            }, false
        )
    }

    private fun logoutToMain() {
        Router.navigateClearingAllActivity(context, LoginActivity::class)
    }
}
