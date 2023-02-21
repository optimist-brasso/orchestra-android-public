package com.co.brasso.feature.landing.myPage.tab.myPage.points.freePoints

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.databinding.FragmentFreePointsListBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.freePointListAdapter.FreePointListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class FreePointsListFragment : BaseFragment<FreePointsListView, FreePointsListPresenter>(),
    FreePointsListView {

    private lateinit var binding: FragmentFreePointsListBinding
    private var pointList: MutableList<FreePointListResponse>? = null
    private var pointListAdapter: FreePointListAdapter? = null
    private var currentPage: Int = 0

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFreePointsListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
        initToolbar()
    }

    private fun setUp() {
        initListener()
        currentPage = 1
        retrieveBundleList(false)
        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun initListener() {
        binding.swrFreePointsList.setOnRefreshListener {
            binding.swrFreePointsList.isRefreshing = false
            currentPage = 1
            retrieveBundleList(true)
        }
    }

    private fun initRecyclerView() {
        pointList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvPointList.layoutManager = layoutManager
        pointListAdapter = FreePointListAdapter(pointList ?: mutableListOf(), {
        }, {
            currentPage += 1
            retrieveBundleList(false)
        })
        binding.rcvPointList.adapter = pointListAdapter
    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = pointList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            pointList?.removeAt(loadingIndex)
        }
        if (pointList?.lastOrNull()?.hasNext == true) {
            pointList?.add(
                FreePointListResponse(viewType = Constants.showLoading, hasNext = true)
            )
        }
    }

    private fun retrieveBundleList(isPullToRefresh: Boolean) {
        presenter.getFreeBundleList(currentPage, isPullToRefresh)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun setFreeBundleList(freePointListResponse: MutableList<FreePointListResponse>?) {
        if (freePointListResponse.isNullOrEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                this.pointList?.clear()
            this.pointList?.addAll(freePointListResponse ?: mutableListOf())
            addOrRemoveLoadingForAdapter()
            pointListAdapter?.notifyDataSetChanged()
        }
    }

    override fun createPresenter() = FreePointsListPresenter()

    private fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }
}
