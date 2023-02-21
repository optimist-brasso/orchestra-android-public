package com.co.brasso.feature.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentPlayerBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.PlayerListClick
import com.co.brasso.feature.shared.adapter.PlayerListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class PlayerListFragment : BaseFragment<PlayerListFragmentView, PlayerListFragmentPresenter>(),
    PlayerListFragmentView, PlayerListClick, TextWatcher {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerListAdapter: PlayerListAdapter
    var playerList: MutableList<PlayerDetailResponse>? = null
    private var currentPage: Int = 0
    private var searchSlug: String = StringConstants.emptyString

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        initListener()
        setPageCount()
        initRecyclerView()
        getPlayerList()
        initToolbar()
        initPointBalance()
    }

    private fun initPointBalance() {
        if (getLoginState()) {
            if (isNetworkAvailable()) presenter.getBundleList()
            binding.cnlBalance.viewVisible()
            binding.rcvPlayerList.setPadding(0, 0, 0, 185)
        } else {
            binding.cnlBalance.viewGone()
            binding.rcvPlayerList.setPadding(0, 0, 0, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setBundleList(bundleList: GetBundleInfo) {
        bundleList.products?.paidPoint?.let {
            binding.txtTotalPoints.text = "%,d".format(
                (bundleList.products?.paidPoint?.plus(bundleList.products?.freePoint ?: 0))
            ) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txtTotalPoints.text = getString(R.string.zero)
        }
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun initListener() {
        initSearchActionListener()
        initTextChangeListener()
        binding.swpPlayer.setOnRefreshListener {
            binding.swpPlayer.isRefreshing = false
            binding.search.edvSearch.removeTextChangedListener(this)
            binding.search.edvSearch.setText(StringConstants.emptyString)
            binding.search.edvSearch.addTextChangedListener(this)
            currentPage = 1
            searchSlug = StringConstants.emptyString
            if (isNetworkAvailable()) presenter.getPlayerListForPullToRefresh(
                searchSlug, currentPage
            )
            else showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun setPageCount() {
        currentPage++
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
        if (isNetworkAvailable()) presenter.getPlayerList(searchSlug, currentPage)
    }

    override fun createPresenter() = PlayerListFragmentPresenter()

    @SuppressLint("NotifyDataSetChanged")
    override fun success(playerListResponse: MutableList<PlayerDetailResponse>) {
        if (playerListResponse.isEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1) playerList?.clear()
            playerList?.addAll(playerListResponse.shuffled())
            addOrRemoveLoadingForAdapter()
            if (currentPage == 1) playerListAdapter.notifyDataSetChanged()
            else playerListResponse.size.let { nSize ->
                playerList?.size?.let { lSize ->
                    playerListAdapter.notifyItemRangeChanged(lSize - nSize, nSize)
                }
            }
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

    private fun initTextChangeListener() {
        binding.search.edvSearch.addTextChangedListener(this)
    }


    private fun initSearchActionListener() {
        binding.search.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                searchSlug = binding.search.edvSearch.text.toString()
                presenter.getPlayerList(searchSlug, currentPage)
                hideKeyboard(binding.search.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.search.edvSearch.text.isEmpty()) {
            hideErrorLayout()
            currentPage = 1
            searchSlug = StringConstants.emptyString
            presenter.getPlayerList(searchSlug, currentPage)
            hideKeyboard(binding.search.edvSearch)
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}