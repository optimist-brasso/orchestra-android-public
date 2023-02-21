package com.co.brasso.feature.bannerDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentBannerDetailsBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.home.Banner
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.router.Router
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso

class BannerDetailFragment : BaseFragment<BannerDetailView, BannerDetailPresenter>(),
    BannerDetailView {

    lateinit var binding: FragmentBannerDetailsBinding
    private var isFirst = true
    private var bannerData: Banner? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentBannerDetailsBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
            isFirst = false
        }
        initToolBar()
    }

    private fun setUp() {
        getBundleData()
        setData()
        initListeners()
    }

    private fun initToolBar() {
        (activity as LandingActivity).hideToolBarTitle()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).showNotificationIcon()
    }

    private fun initListeners() {
        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(bannerData?.title, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }
    }

    private fun setData() {
        if (bannerData?.image.isNullOrEmpty()) {
            binding.crdNotification.viewGone()
        } else {
            Picasso.get().load(bannerData?.image).into(binding.imvNotifications)
        }

        if (bannerData?.title.isNullOrEmpty()) {
            binding.txvNotificationTitle.viewGone()
        } else {
            binding.txvNotificationTitle.text = bannerData?.title
        }

        if (bannerData?.link?.description.isNullOrEmpty()) {
            binding.txvNotificationDetail.viewGone()
        } else {
            binding.txvNotificationDetail.text = bannerData?.link?.description
        }
    }

    private fun getBundleData() {
        bannerData =
            arguments?.getSerializable(BundleConstant.bannerData) as? Banner
    }

    override fun createPresenter() = BannerDetailPresenter()
}



