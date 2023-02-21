package com.co.brasso.feature.landing.favorite.tab.session

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentFavouriteSessionBinding
import com.co.brasso.feature.shared.adapter.sessionFavourite.SessionFavouriteAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils

class FavouriteSessionFragment : BaseFragment<FavouriteSessionView, FavouriteSessionPresenter>(),
    FavouriteSessionView {

    private var binding: FragmentFavouriteSessionBinding? = null
    private var sessionFavouriteAdapter: SessionFavouriteAdapter? = null
    private var currentPage: Int = 0
    private var favouriteList: MutableList<FavouriteSessionResponse>? = null
    var searchSlug: String? = ""
    private var isFirst = true
    var fragmentType: String? = Constants.session
    private var instrumentResponse: InstrumentResponse? = null

    companion object {
        var selectedPos: Int? = null
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isFirst) {
            binding = FragmentFavouriteSessionBinding.inflate(inflater, container, false)
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
        currentPage = 1
        getSessionFavouriteList()
        initRecyclerView()
    }

    override fun createPresenter() = FavouriteSessionPresenter()

    private fun initRecyclerView() {
        favouriteList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding?.rcvFavouriteSession?.layoutManager = layoutManager
        sessionFavouriteAdapter = SessionFavouriteAdapter(favouriteList ?: mutableListOf(), {
            if (isNetworkAvailable())
                removeFromFavouriteList(it)
            else
                showMessageDialog(R.string.error_no_internet_connection)
        }, {
            navigateToDetail(it)
        }, {
            currentPage += 1
            getSessionFavouriteList()
        })
        binding?.rcvFavouriteSession?.adapter = sessionFavouriteAdapter
    }

    private fun navigateToDetail(position: Int?) {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(), getString(R.string.please_login), { navigateToLogin() }, {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
        } else {
            val favouriteData = favouriteList?.get(position ?: 0)
            instrumentResponse =
                InstrumentResponse(
                    sessionId = favouriteData?.orchestra?.id,
                    musicianId = favouriteData?.musician?.id,
                    instrument_id = favouriteData?.instrument?.id
                )
            val bundle = bundleOf(Constants.session to instrumentResponse)
            bundle.putInt(StringConstants.selectedPos, position ?: 0)
            bundle.putBoolean(StringConstants.isFromFav, true)
            Router.navigateFragmentWithData(findNavController(), R.id.sessionDetailFragment, bundle)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeFromFavouriteList(position: Int?) {
        val favouriteData = favouriteList?.get(position ?: 0)
        presenter.addSessionFavourite(
            FavouriteSessionEntity(
                favouriteData?.orchestra?.id.toString(),
                favouriteData?.instrument?.id.toString(),
                favouriteData?.musician?.id.toString()
            )
        )
        favouriteList?.removeAt(position ?: 0)
        sessionFavouriteAdapter?.notifyItemRemoved(position ?: 0)
        if (favouriteList.isNullOrEmpty()) {
            showErrorLayout()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setAdapter(favouriteSearchList: MutableList<FavouriteSessionResponse>?) {
        if (favouriteSearchList.isNullOrEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                this.favouriteList?.clear()
            this.favouriteList?.addAll(favouriteSearchList ?: mutableListOf())
            addOrRemoveLoadingForAdapter()
            if (currentPage == 1)
                sessionFavouriteAdapter?.notifyDataSetChanged()
            else
                favouriteSearchList?.size?.let { nSize ->
                    favouriteList?.size?.let { lSize ->
                        sessionFavouriteAdapter?.notifyItemRangeChanged(lSize - nSize, nSize)
                    }
                }
        }
    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = favouriteList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            favouriteList?.removeAt(loadingIndex)
        }
        if (favouriteList?.lastOrNull()?.hasNext == true) {
            favouriteList?.add(
                FavouriteSessionResponse(viewType = Constants.showLoading, hasNext = true)
            )
        }
    }

    private fun getSessionFavouriteList() {
        presenter.getMySessionFavouriteList(searchSlug, currentPage, false)
    }

    fun getSessionFavouriteListWithEmptySlug(searchSlug: String?, isPullToRefresh: Boolean) {
        this.searchSlug = searchSlug
        currentPage = 1
        presenter.getMySessionFavouriteList(searchSlug, currentPage, isPullToRefresh)
    }

    private fun showErrorLayout() {
        binding?.lltNoData?.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding?.lltNoData?.rllNoData.viewGone()
    }

    override fun showProgressBar() {
        binding?.pgb?.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding?.pgb?.cnsProgress.viewGone()
    }

    override fun onResume() {
        super.onResume()
        if (selectedPos != null) {
            favouriteList?.removeAt(selectedPos ?: 0)
            sessionFavouriteAdapter?.notifyItemRemoved(selectedPos ?: 0)
            if (favouriteList.isNullOrEmpty()) {
                showErrorLayout()
            }
            selectedPos = null
        }
    }
}