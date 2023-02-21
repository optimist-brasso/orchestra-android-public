package com.co.brasso.feature.landing.listSong.listSongSession

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
import com.co.brasso.databinding.FragmentOrchestraListSongBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.listSong.sessionListSong.SessionListSongAdapter
import com.co.brasso.feature.shared.adapter.viewholder.SessionFavouriteViewHolder
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils

class ListSongSessionFragment : BaseFragment<ListSongSessionView, ListSongSessionPresenter>(),
    ListSongSessionView {

    private lateinit var binding: FragmentOrchestraListSongBinding
    private var orchestraSearchAdapter: SessionListSongAdapter? = null
    private var currentPage: Int = 0
    private var orchestraList: MutableList<FavouriteSessionResponse>? = null
    var searchSlug: String? = ""
    private var isFirst = true
    private var instrumentResponse: InstrumentResponse? = null

    companion object {
        var selectedPos: Int? = null
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentOrchestraListSongBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
        }
        initToolBar()
    }

    private fun initToolBar() {
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun setUp() {
        currentPage = 1
        getSessionList()
        initRecyclerView()
    }

    override fun createPresenter() = ListSongSessionPresenter()

    private fun initRecyclerView() {
        orchestraList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvConductorFavourite.layoutManager = layoutManager
        orchestraSearchAdapter = SessionListSongAdapter(orchestraList ?: mutableListOf(), {
            if (!getLoginState())
                DialogUtils.showAlertDialog(
                    requireContext(), getString(R.string.please_login), {
                        StateManagement.pageName = Constants.songsList
                        navigateToLogin() }, {},
                    isCancelable = false,
                    showNegativeBtn = true,
                    getString(R.string.login)
                )
            else{
                if (isNetworkAvailable())
                    addRemoveFavourite(it)
                else
                    showMessageDialog(R.string.error_no_internet_connection)
            }

        }, { openDetailPage(it) }, {
            currentPage += 1
            getSessionList()
        })
        binding.rcvConductorFavourite.adapter = orchestraSearchAdapter
    }

    private fun openDetailPage(position: Int?) {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(), getString(R.string.please_login), { navigateToLogin() }, {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
        } else {
            val favouriteData = orchestraList?.get(position ?: 0)
            instrumentResponse =
                InstrumentResponse(
                    sessionId = favouriteData?.orchestra?.id,
                    musicianId = favouriteData?.musician?.id,
                    instrument_id = favouriteData?.instrument?.id
                )
            val bundle = bundleOf(Constants.session to instrumentResponse)
            bundle.putInt(StringConstants.selectedPos, position ?: 0)
            bundle.putBoolean(StringConstants.isFromFav, false)
            Router.navigateFragmentWithData(findNavController(), R.id.sessionDetailFragment, bundle)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addRemoveFavourite(position: Int?) {
        orchestraList?.get(position ?: 0)?.instrument?.isFavourite =
            !((orchestraList?.get(position ?: 0)?.instrument?.isFavourite) ?: false)

        presenter.addSessionFavourite(
            FavouriteSessionEntity(
                orchestraList?.get(position ?: 0)?.orchestra?.id.toString(),
                orchestraList?.get(position ?: 0)?.instrument?.id.toString(),
                orchestraList?.get(position ?: 0)?.musician?.id.toString()
            )
        )
        position?.let {
            val holder =
                binding.rcvConductorFavourite.findViewHolderForAdapterPosition(it) as SessionFavouriteViewHolder
            val response = orchestraList?.get(it)
            holder.setFavourite(requireContext(), response)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setAdapter(sessionList: MutableList<FavouriteSessionResponse>?) {
        if (sessionList.isNullOrEmpty() && currentPage == 1) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                this.orchestraList?.clear()
            this.orchestraList?.addAll(sessionList ?: mutableListOf())
            addOrRemoveLoadingForAdapter()
            orchestraSearchAdapter?.notifyDataSetChanged()
        }
    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = orchestraList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            orchestraList?.removeAt(loadingIndex)
        }
        if (orchestraList?.lastOrNull()?.hasNext == true) {
            orchestraList?.add(
                FavouriteSessionResponse(viewType = Constants.showLoading, hasNext = true)
            )
        }
    }

    private fun getSessionList() {
        presenter.orchestraSearch(searchSlug, currentPage, false)
    }

    fun getSessionListForSearchAndPullToRefresh(searchSlug: String?, isPullToRefresh: Boolean) {
        this.searchSlug = searchSlug
        currentPage = 1
        presenter.orchestraSearch(searchSlug, currentPage, isPullToRefresh)
    }

    private fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }

    override fun onResume() {
        super.onResume()
        if (selectedPos != null) {
            orchestraList?.get(selectedPos ?: 0)?.instrument?.isFavourite =
                !((orchestraList?.get(selectedPos ?: 0)?.instrument?.isFavourite) ?: false)

            if(::binding.isInitialized) {
                selectedPos?.let {
                    val holder =
                        binding.rcvConductorFavourite.findViewHolderForAdapterPosition(it) as? SessionFavouriteViewHolder
                    val response = orchestraList?.get(it)
                    holder?.setFavourite(requireContext(), response)
                }
            }
            selectedPos = null
        }
    }
}