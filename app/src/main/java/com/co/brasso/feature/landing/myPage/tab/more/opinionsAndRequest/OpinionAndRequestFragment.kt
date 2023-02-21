package com.co.brasso.feature.landing.myPage.tab.more.opinionsAndRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentOpinionAndRequestBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.webView.WebViewActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.router.Router

class OpinionAndRequestFragment :
    BaseFragment<OpinionAndRequestFragmentView, OpinionAndRequestFragmentPresenter>(),
    OpinionAndRequestFragmentView {

    private lateinit var binding: FragmentOpinionAndRequestBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpinionAndRequestBinding.inflate(inflater, container, false)
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
        binding.txtBtnOpinionSubmission.setOnClickListener { navigateToOpinionSubmission() }
    }

    private fun navigateToOpinionSubmission() {
        val opinion = "https://brasso.jp/opinions"
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.webViewLink,
            opinion,
            WebViewActivity::class
        )
    }

    override fun createPresenter() =
        OpinionAndRequestFragmentPresenter()
}
