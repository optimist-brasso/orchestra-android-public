package com.co.brasso.feature.landing.myPage.tab.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import com.co.brasso.R
import com.co.brasso.databinding.FragmentMyPageOtherBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.onboarding.OnBoardingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.webView.WebViewActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.router.Router

class MyPageOtherFragment :
    BaseFragment<MyPageOtherFragmentView, MyPageOtherFragmentPresenter>(),
    MyPageOtherFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentMyPageOtherBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        initListeners()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListeners() {
        binding.txtForFirstTime.setOnClickListener(this)
        binding.imgForFirstTimeTab.setOnClickListener(this)
        binding.txtAboutApp.setOnClickListener(this)
        binding.imgAboutAppTab.setOnClickListener(this)
        binding.txtFAQ.setOnClickListener(this)
        binding.imgFAQTab.setOnClickListener(this)
        binding.txtOpinion.setOnClickListener(this)
        binding.imgOpinionTab.setOnClickListener(this)
        binding.txtBrassoWebSite.setOnClickListener(this)
        binding.imgBrassoWebSiteTab.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtForFirstTime -> {
                navigateToOnBoarding()
            }
            R.id.imgForFirstTimeTab -> {
                navigateToOnBoarding()
            }

            R.id.txtAboutApp -> {
                navigateToAboutApp()
            }
            R.id.imgAboutAppTab -> {
                navigateToAboutApp()
            }

            R.id.txtFAQ -> {
                navigateToFAQ()
            }
            R.id.imgFAQTab -> {
                navigateToFAQ()
            }

            R.id.txtOpinion -> {
                navigateToOpinion()
            }
            R.id.imgOpinionTab -> {
                navigateToOpinion()
            }

            R.id.txtBrassoWebSite -> {
                navigateToBrassoWebSite()
            }
            R.id.imgBrassoWebSiteTab -> {
                navigateToBrassoWebSite()
            }
        }
    }

    private fun navigateToOpinion() {
        Navigation.findNavController(requireView()).navigate(R.id.opinionAndRequestFragment)
    }

    private fun navigateToBrassoWebSite() {
        val brassoOfficialSite = "https://brasso.jp/"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            brassoOfficialSite,
            WebViewActivity::class
        )
    }

    private fun navigateToFAQ() {
        Navigation.findNavController(requireView()).navigate(R.id.faqFragment)
    }

    private fun navigateToAboutApp() {
        Navigation.findNavController(requireView()).navigate(R.id.aboutAppFragment)
    }

    private fun navigateToOnBoarding() {
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.onBoarding,
            StringConstants.onBoardingFromMypage,
            OnBoardingActivity::class
        )
    }

    override fun createPresenter(): MyPageOtherFragmentPresenter =
        MyPageOtherFragmentPresenter()
}
