package com.co.brasso.feature.landing.myPage.tab.more.aboutApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentAboutAppBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.webView.WebViewActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.router.Router

class AboutAppFragment :
    BaseFragment<AboutAppFragmentView, AboutAppFragmentPresenter>(),
    AboutAppFragmentView {

    private lateinit var binding: FragmentAboutAppBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
        }

    private fun setup() {
        initListeners()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListeners() {
        binding.txtBtnTermOfServices.setOnClickListener {
            navigateToTermsOfServices()
        }

        binding.txtBtnPrivacyPolicy.setOnClickListener {
            navigateToPrivacyPolicy()
        }

        binding.txtBtnNotationBasedOnAct.setOnClickListener {
            navigateToNotationBasedOnAct()
        }

        binding.txtBtnSoftwareLicense.setOnClickListener {
            navigateToSoftwareLicense()
        }
    }

    private fun navigateToTermsOfServices() {
        val termsOfServices = "https://brasso.jp/terms"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            termsOfServices,
            WebViewActivity::class
        )
    }

    private fun navigateToPrivacyPolicy() {
        val privacyPolicy = "https://brasso.jp/privacy"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            privacyPolicy,
            WebViewActivity::class
        )
    }

    private fun navigateToNotationBasedOnAct() {
        val notationBasedOnAct = "https://brasso.jp/low"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            notationBasedOnAct,
            WebViewActivity::class
        )
    }

    private fun navigateToSoftwareLicense() {
        val softwareLicense = "https://brasso.jp/license"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            softwareLicense,
            WebViewActivity::class
        )
    }

    override fun createPresenter() =
        AboutAppFragmentPresenter()
}
