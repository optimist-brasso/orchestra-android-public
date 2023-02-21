package com.co.brasso.feature.orchestraPlayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentOrchestraPlayerBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.PlayerListClick
import com.co.brasso.feature.shared.adapter.PlayerListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class OrchestraPlayerFragment :
    BaseFragment<OrchestraPlayerListFragmentView, OrchestraPlayerListFragmentPresenter>(),
    OrchestraPlayerListFragmentView, PlayerListClick {

    private lateinit var binding: FragmentOrchestraPlayerBinding
    private var orchestraId: Int? = null
    private lateinit var playerListAdapter: PlayerListAdapter
    var playerList: MutableList<PlayerDetailResponse>? = null
    private var isFirst = true
    private var currentPage: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentOrchestraPlayerBinding.inflate(inflater, container, false)
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
    }

    private fun setUp() {
        initRecyclerView()
        setPageCount()
        getOrchestraID()
        getPlayerList()
        initToolBar()
    }

    private fun setPageCount() {
        currentPage++
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showCartCount()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showBottomBar()
    }

    private fun getOrchestraID() {
        orchestraId = arguments?.getInt(BundleConstant.orchestraId) ?: 0
    }

    private fun initRecyclerView() {
        playerList = mutableListOf()
        val gridLayoutManger = GridLayoutManager(requireContext(), 3)
        binding.rcvPlayerList.layoutManager = gridLayoutManger
        gridLayoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (playerListAdapter.getItemViewType(position)) {
                    Constants.playerListViewType -> 1
                    else -> 3
                }
            }
        }
        playerListAdapter = PlayerListAdapter(playerList, this) {
            currentPage += 1
            getPlayerList()
        }
        binding.rcvPlayerList.adapter = playerListAdapter
    }

    private fun getPlayerList() {
        presenter.getPlayerList(orchestraId, currentPage)
    }

    override fun createPresenter() = OrchestraPlayerListFragmentPresenter()

    @SuppressLint("NotifyDataSetChanged")
    override fun success(playerListResponse: MutableList<PlayerDetailResponse>) {
        if (playerListResponse.isEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                playerList?.clear()
            playerList?.addAll(playerListResponse.shuffled())
            addOrRemoveLoadingForAdapter()
            playerListAdapter.notifyDataSetChanged()
        }
    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = playerList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            playerList?.removeAt(loadingIndex)
        }
        if (playerList?.lastOrNull()?.hasNext == true) {
            playerList?.add(
                PlayerDetailResponse(viewType = Constants.showLoading, hasNext = true)
            )
        }
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun showErrorLayout() {
        binding.rllNoData.rllNoData.viewVisible()
    }

    override fun hideErrorLayout() {
        binding.rllNoData.rllNoData.viewGone()
    }

    override fun onClick(pos: Int?) {
        val bundle = bundleOf(BundleConstant.orchestraId to pos)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.playerDetailFragment, bundle)
    }
}