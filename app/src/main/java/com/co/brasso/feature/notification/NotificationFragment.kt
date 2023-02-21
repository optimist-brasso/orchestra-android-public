package com.co.brasso.feature.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentNotificationListingBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.NotificationListingAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DensityUtils

class NotificationFragment : BaseFragment<NotificationView, NotificationPresenter>(),
    NotificationView {

    lateinit var binding: FragmentNotificationListingBinding
    private var notificationAdapter: NotificationListingAdapter? = null
    private var notificationList: MutableList<NotificationResponse>? = null
    private var isFirst = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentNotificationListingBinding.inflate(inflater, container, false)
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
        initListener()
        initRecyclerView()
        getNotificationList()
    }

    private fun getNotificationList() {
        presenter.getNotificationList(false)
    }

    private fun initListener() {

        binding.header.imvConductor.setOnClickListener {
            openOrchestra(Constants.conductor)
        }

        binding.header.imvSession.setOnClickListener {
            openOrchestra(Constants.session)
        }

        binding.header.imvHallsound.setOnClickListener {
            openOrchestra(Constants.hallSound)
        }

        binding.header.imvPlayer.setOnClickListener {
            openOrchestra(Constants.player)
        }

        binding.swpNotification.setOnRefreshListener {
            binding.swpNotification.isRefreshing = false
            presenter.getNotificationList(true)
        }
    }

    private fun openOrchestra(type: String) {
        val bundle = bundleOf(BundleConstant.fragmentType to type)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.orchestraFragment, bundle)
    }

    private fun initToolBar() {
        (activity as LandingActivity).hideToolBarTitle()
        (activity as LandingActivity).hideNotification()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).showBottomBar()
    }

    override fun createPresenter() = NotificationPresenter()

    private fun initRecyclerView() {
        notificationList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvNotificationListing.layoutManager = layoutManager
        notificationAdapter = NotificationListingAdapter(notificationList) {
            navigateToNotificationDetails(
                NotificationResponse(
                    it?.id, it?.title, it?.body,
                    it?.image, it?.createdAt, it?.color, it?.seenStatus ?: false
                )
            )
            val itemPosition = notificationList?.indexOf(it)
            notificationList?.get(itemPosition ?: 0)?.seenStatus = true
            notificationAdapter?.notifyItemChanged(itemPosition ?: 0)
        }
        binding.rcvNotificationListing.adapter = notificationAdapter
    }

    private fun navigateToNotificationDetails(notificationResponse: NotificationResponse?) {
        val bundle = Bundle()
        bundle.putSerializable(BundleConstant.notificationItem, notificationResponse)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.notificationDetails, bundle)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setNotificationList(notificationResponse: List<NotificationResponse>) {

        if (notificationResponse.isEmpty()) {
        showErrorLayout()
        } else {
            hideErrorLayout()
            this.notificationList?.clear()
            this.notificationList?.addAll(notificationResponse)
            notificationAdapter?.notifyDataSetChanged()
        }
    }

    override fun showProgressBar() {
        binding.pgbConductor.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgbConductor.cnsProgress.viewGone()
    }

    private fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
        binding.lltNoData.rllNoData.layoutParams.height =
            (DensityUtils.getScreenHeight(requireContext()) - (binding.header.lltHeader.height + DensityUtils.dipTopx(
                requireContext(),
                120f
            )))
    }

    private fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

}