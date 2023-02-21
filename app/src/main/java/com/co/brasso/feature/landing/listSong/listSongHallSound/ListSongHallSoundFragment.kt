package com.co.brasso.feature.landing.listSong.listSongHallSound

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentOrchestraListSongBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.OrchestraListSongViewHolder
import com.co.brasso.feature.shared.adapter.listSong.OrchestraListSongAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils

class ListSongHallSoundFragment : BaseFragment<ListSongHallSoundView, ListSongHallSoundPresenter>(),
    ListSongHallSoundView {

    private var binding: FragmentOrchestraListSongBinding? = null
    var fragmentPosition: Int? = Constants.FRAGMENT_CONDUCTOR
    var searchSlug: String? = ""
    private var tempList: MutableList<HallSoundResponse>? = null
    private var orchestraList: MutableList<HallSoundResponse>? = null
    private var orchestraSearchAdapter: OrchestraListSongAdapter? = null
    var type: String = Constants.conductor

    companion object {
        var selectedPos: Int? = null
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrchestraListSongBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        initRecyclerView()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
        filterFavouriteList(StringConstants.emptyString, false)
    }

    private fun initRecyclerView() {
        orchestraList = mutableListOf()
        tempList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding?.rcvConductorFavourite?.layoutManager = layoutManager
        orchestraSearchAdapter = OrchestraListSongAdapter(orchestraList ?: mutableListOf(), {
            openDetailPage(it)
        }, {
            if (isNetworkAvailable())
                addRemoveFavourite(it)
            else
                showMessageDialog(R.string.error_no_internet_connection)
        },Constants.hallSound)
        binding?.rcvConductorFavourite?.adapter = orchestraSearchAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addRemoveFavourite(position: Int?) {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.please_login),
                {
                    StateManagement.pageName = Constants.songsList
                    navigateToLogin()
                },
                {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
        } else {
            favoriteChange(position)
        }
    }

    private fun favoriteChange(position: Int?) {
        orchestraList?.get(position ?: 0)?.isHallsoundFavourite =
            !(orchestraList?.get(position ?: 0)?.isHallsoundFavourite ?: false)

        presenter.addFavourite(
            FavouriteEntity(
                ApiConstant.hallSound,
                orchestraList?.get(position ?: 0)?.id.toString()
            )
        )
        position?.let {
            val holder =
                binding?.rcvConductorFavourite?.findViewHolderForAdapterPosition(it) as OrchestraListSongViewHolder
            holder.setFavourite(
                requireContext(),
                orchestraList?.get(position ?: 0)?.isHallsoundFavourite
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapter(hallSoundList: List<HallSoundResponse>?) {
        if (hallSoundList.isNullOrEmpty()) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            tempList?.clear()
            tempList?.addAll(hallSoundList)
            this.orchestraList?.clear()
            this.orchestraList?.addAll(hallSoundList)
            orchestraSearchAdapter?.notifyDataSetChanged()
        }
    }

    private fun openDetailPage(position: Int?) {
        val id = orchestraList?.get(position ?: 0)?.id
        val bundle = bundleOf(BundleConstant.orchestraId to id)
        bundle.putString(BundleConstant.fragmentType, Constants.hallSound)
        bundle.putInt(StringConstants.selectedPos, position ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, false)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.hallSoundDetailFragment, bundle)
    }

    private fun showErrorLayout() {
        binding?.lltNoData?.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding?.lltNoData?.rllNoData.viewGone()
    }

    fun filterFavouriteList(slug: String?, isPullToRefresh: Boolean) {
        if (!slug.isNullOrEmpty()) searchSlug = slug
        presenter.orchestraSearch(searchSlug, isPullToRefresh)
    }

    override fun setSearchData(searchData: MutableList<HallSoundResponse>?) {
        if (!searchData.isNullOrEmpty()) hideErrorLayout()
        else showErrorLayout()
        orchestraList?.clear()
        searchData?.forEach { it.type = type }
        orchestraList?.addAll(searchData ?: mutableListOf())
        orchestraSearchAdapter?.notifyDataSetChanged()
    }

    override fun showProgressBar() {
        binding?.pgb?.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding?.pgb?.cnsProgress.viewGone()
    }

    override fun createPresenter() = ListSongHallSoundPresenter()

    override fun onResume() {
        super.onResume()
        if (selectedPos != null) {
            orchestraList?.get(selectedPos ?: 0)?.isHallsoundFavourite =
                !(orchestraList?.get(selectedPos ?: 0)?.isHallsoundFavourite ?: false)

            orchestraSearchAdapter?.notifyDataSetChanged()
            selectedPos = null
        }
    }
}