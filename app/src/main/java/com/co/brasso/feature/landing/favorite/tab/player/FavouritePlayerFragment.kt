package com.co.brasso.feature.landing.favorite.tab.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentFavouritePlayerBinding
import com.co.brasso.feature.shared.adapter.playerFavourite.PlayerFavouriteAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteMusician
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class FavouritePlayerFragment : BaseFragment<FavouritePlayerView, FavouritePlayerPresenter>(),
    FavouritePlayerView {

    private var binding: FragmentFavouritePlayerBinding? = null
    private var playerFavouriteAdapter: PlayerFavouriteAdapter? = null
    private var playerFavouriteList: MutableList<PlayerDetailResponse>? = null
    private var currentPage: Int = 0
    var searchSlug: String? = ""
    private var isFirst = true

    companion object {
        var selectedPos: Int? = null
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isFirst) {
            binding = FragmentFavouritePlayerBinding.inflate(inflater, container, false)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
        }
    }

    private fun setUp() {
        setPageCount()
        initRecyclerView()
    }

    override fun setMenuVisibility(isvisible: Boolean) {
        super.setMenuVisibility(isvisible)
        if (isvisible && isFirst) {
            presenter.getMyPlayerFavouriteList(
                searchSlug,
                currentPage
            )
            isFirst = false
        }
    }

    private fun setPageCount() {
        currentPage++
    }

    private fun initRecyclerView() {
        playerFavouriteList = mutableListOf()
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding?.rcvFavouritePlayer?.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (playerFavouriteAdapter?.getItemViewType(position)) {
                    Constants.playerListViewType -> 1
                    else -> 3
                }
            }

        }
        playerFavouriteAdapter = PlayerFavouriteAdapter(playerFavouriteList ?: mutableListOf(), {
            if (isNetworkAvailable())
                removeFromFavouriteList(it)
            else
                showMessageDialog(R.string.error_no_internet_connection)
        }, {
            navigateToDetail(it)

        }, {
            currentPage++
            presenter.getMyPlayerFavouriteList(searchSlug, currentPage)
        })
        binding?.rcvFavouritePlayer?.adapter = playerFavouriteAdapter
    }

    private fun navigateToDetail(position: Int?) {
        val id = playerFavouriteList?.get(position ?: 0)?.id
        val bundle = bundleOf(BundleConstant.orchestraId to id)
        val navController = NavHostFragment.findNavController(this)
        bundle.putInt(StringConstants.selectedPos, position ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, true)
        Router.navigateFragmentWithData(navController, R.id.playerDetailFragment, bundle)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeFromFavouriteList(position: Int?) {
        val favouriteData = playerFavouriteList?.get(position ?: 0)
        presenter.addMusicianFavourite(FavouriteMusician(favouriteData?.id.toString()))
        playerFavouriteList?.removeAt(position ?: 0)
        playerFavouriteAdapter?.notifyItemRemoved(position ?: 0)
        if (playerFavouriteList.isNullOrEmpty()) {
            showErrorLayout()
        }
    }

    override fun createPresenter() = FavouritePlayerPresenter()

    @SuppressLint("NotifyDataSetChanged")
    override fun setMyPlayerFavourite(playerFavouriteList: List<PlayerDetailResponse>?) {
        if (playerFavouriteList.isNullOrEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                this.playerFavouriteList?.clear()
            this.playerFavouriteList?.addAll(playerFavouriteList ?: mutableListOf())
            addOrRemoveLoadingForAdapter()
            if (currentPage == 1)
                playerFavouriteAdapter?.notifyDataSetChanged()
            else
                playerFavouriteList?.size?.let { nSize ->
                    playerFavouriteList.size.let { lSize ->
                        playerFavouriteAdapter?.notifyItemRangeChanged(lSize - nSize, nSize)
                    }
                }
        }

    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = playerFavouriteList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            playerFavouriteList?.removeAt(loadingIndex)
        }
        if (playerFavouriteList?.lastOrNull()?.hasNext == true) {
            playerFavouriteList?.add(
                PlayerDetailResponse(viewType = Constants.showLoading, hasNext = true)
            )
        }
    }

    fun searchPlayer(searchSlug: String?) {
        this.searchSlug = searchSlug
        currentPage = 1
        presenter.getMyPlayerFavouriteList(searchSlug, currentPage)
    }

    fun getAllPlayerOnPullToRefresh() {
        currentPage = 1
        searchSlug = StringConstants.emptyString
        presenter.getMyPlayerFavouriteListOnPullToRefresh(searchSlug, currentPage)
    }

    fun getAllPlayer() {
        if (!this.searchSlug.isNullOrEmpty()) {
            currentPage = 1
            searchSlug = StringConstants.emptyString
            presenter.getMyPlayerFavouriteList(searchSlug, currentPage)
        }
    }

    override fun showProgressBar() {
        binding?.pgb?.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding?.pgb?.cnsProgress.viewGone()
    }

    private fun showErrorLayout() {
        binding?.lltNoData?.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding?.lltNoData?.rllNoData.viewGone()
    }

    override fun onResume() {
        super.onResume()
        if (selectedPos != null) {
            playerFavouriteList?.removeAt(selectedPos ?: 0)
            playerFavouriteAdapter?.notifyItemRemoved(selectedPos ?: 0)
            if (playerFavouriteList.isNullOrEmpty()) {
                showErrorLayout()
            }
            selectedPos = null
        }
    }
}