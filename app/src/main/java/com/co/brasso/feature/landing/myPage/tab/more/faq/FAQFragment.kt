package com.co.brasso.feature.landing.myPage.tab.more.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentFaqBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.webView.WebViewActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.router.Router

class FAQFragment :
    BaseFragment<FAQFragmentView, FAQFragmentPresenter>(),
    FAQFragmentView {

    private lateinit var binding: FragmentFaqBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaqBinding.inflate(inflater, container, false)
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
        binding.txtBtnFAQ.setOnClickListener {
            val link = "https://brasso.jp/qa"
            Router.navigateActivityWithData(
                requireContext(),
                false,
                BundleConstant.webViewLink,
                link,
                WebViewActivity::class
            )
        }

        binding.txtBtnContactUs.setOnClickListener {
            val link = "https://brasso.jp/contact"
            Router.navigateActivityWithData(
                requireContext(),
                false,
                BundleConstant.webViewLink,
                link,
                WebViewActivity::class
            )
        }
    }

    override fun createPresenter() =
        FAQFragmentPresenter()
}
