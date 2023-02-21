package com.co.brasso.feature.notification.notificationDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.navigation.fragment.findNavController
import com.co.brasso.databinding.FragmentNotificationDetailsBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DensityUtils
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class NotificationDetailActivity :
    BaseFragment<NotificationDetailView, NotificationDetailPresenter>(),
    NotificationDetailView {

    lateinit var binding: FragmentNotificationDetailsBinding
    private var notificationShare: String? = null
    private var notificationItem: NotificationResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        getBundleData()
        initListener()
    }

    private fun setUp() {
        (activity as LandingActivity).hideToolBarTitle()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).hideNotification()

        val width = DensityUtils.getScreenWidth(binding.root.context)
        val height = width * (1 / 2f)
        if (width > 0 && height > 0) {
            val params = RelativeLayout.LayoutParams(width, height.toInt())
            binding.crdNotification.layoutParams = params
        }
    }

    private fun initListener() {
        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(notificationShare, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }
    }

    private fun getBundleData() {
        notificationItem =
            arguments?.getSerializable(BundleConstant.notificationItem) as? NotificationResponse
        val isFromFirebaseNotification = arguments?.getBoolean(BundleConstant.firebaseNotification)
        if (isFromFirebaseNotification == true) {
            (requireContext() as? LandingActivity)?.shouldRefreshHome = true
        }
        presenter.getNotificationDetail(notificationItem?.id.toString())
    }

    override fun createPresenter() = NotificationDetailPresenter()

    override fun setNotificationDetail(notificationResponse: NotificationResponse?) {
        if (!getLoginState() && notificationItem?.seenStatus == false) {
            notificationItem?.seenStatus = true
            presenter.updateNotificationSeenStatus(notificationItem)
        } else if (getLoginState() && notificationItem?.seenStatus == false) {
            val notification = AppInfoGlobal.notifications?.find { it.id == notificationItem?.id }
            notification?.seenStatus = true
        }
        if (notificationResponse != null) {
            hideErrorLayout()

            if (notificationResponse.image.isNullOrEmpty()) {
                binding.crdNotification.viewGone()
            } else {
                Picasso.get().load(notificationResponse.image)
                    .into(binding.imvNotifications, object : Callback {
                        override fun onSuccess() {
                            binding.imgProgress.viewGone()
                        }

                        override fun onError(e: Exception?) {
                            binding.imgProgress.viewGone()
                        }

                    })
            }

            if (notificationResponse.title.isNullOrEmpty()) {
                binding.txvNotificationTitle.viewGone()
            } else {
                binding.txvNotificationTitle.text = notificationResponse.title
            }

            if (notificationResponse.body.isNullOrEmpty()) {
                binding.txvNotificationDetail.viewGone()
            } else {
                binding.txvNotificationDetail.text = notificationResponse.body
                notificationShare = notificationResponse.body
            }

            if (notificationResponse.createdAt.isNullOrEmpty()) {
                binding.txvNotificationDate.viewGone()
            } else {
                binding.txvNotificationDate.text =
                    notificationResponse.createdAt
            }
        } else {
            showErrorLayout()
        }
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }

    override fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    override fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

    override fun popUpBackStack() {
        findNavController().popBackStack()
    }

    override fun showDetail() {
     binding.rllNotificationMain.viewVisible()
    }

    override fun hideDetail() {
        binding.rllNotificationMain.viewGone()
    }
}