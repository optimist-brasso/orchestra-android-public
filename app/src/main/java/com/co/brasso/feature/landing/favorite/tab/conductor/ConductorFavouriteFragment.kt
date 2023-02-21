package com.co.brasso.feature.landing.favorite.tab.conductor

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
import com.co.brasso.databinding.FragmentConductorFavouriteBinding
import com.co.brasso.feature.shared.adapter.conductorFavourite.ConductorFavouriteAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class ConductorFavouriteFragment :
    BaseFragment<ConductorFavouriteView, ConductorFavouritePresenter>(), ConductorFavouriteView {

    private var binding: FragmentConductorFavouriteBinding? = null
    private var conductorFavouriteAdapter: ConductorFavouriteAdapter? = null
    private var favouriteList: MutableList<FavouriteResponse>? = null
    var searchSlug: String? = ""
    var fragmentType: String? = Constants.conductor
    private var isFirst = true
    var type: String? = Constants.conductor

    companion object {
        var selectedPos: Int? = null
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (isFirst) {
            binding = FragmentConductorFavouriteBinding.inflate(inflater, container, false)
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

    override fun setMenuVisibility(isvisible: Boolean) {
        super.setMenuVisibility(isvisible)
        if (isvisible && isFirst) {
            filterFavouriteList(StringConstants.emptyString, false)
            isFirst = false
        }
    }

    override fun createPresenter() = ConductorFavouritePresenter()

    private fun setUp() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        favouriteList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding?.rcvConductorFavourite?.layoutManager = layoutManager
        conductorFavouriteAdapter = ConductorFavouriteAdapter(favouriteList ?: mutableListOf(), {
            if (isNetworkAvailable())
                removeFromFavouriteList(it)
            else
                showMessageDialog(R.string.error_no_internet_connection)
        }, {
            navigateToDetail(it)
        }, Constants.conductor)
        binding?.rcvConductorFavourite?.adapter = conductorFavouriteAdapter
    }

    private fun navigateToDetail(position: Int?) {
        val id = favouriteList?.get(position ?: 0)?.id
        val bundle = bundleOf(BundleConstant.orchestraId to id)
        bundle.putInt(StringConstants.selectedPos, position ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, true)
        bundle.putString(BundleConstant.fragmentType, fragmentType)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.conductorDetailFragment, bundle)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeFromFavouriteList(position: Int?) {
        val favouriteData = favouriteList?.get(position ?: 0)
        presenter.addFavourite(FavouriteEntity(favouriteData?.type, favouriteData?.id.toString()))
        favouriteList?.removeAt(position ?: 0)
        conductorFavouriteAdapter?.notifyItemRemoved(position ?: 0)
        if (favouriteList.isNullOrEmpty()) {
            showErrorLayout()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapter(favouriteList: List<FavouriteResponse>?) {
        if (favouriteList.isNullOrEmpty()) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            this.favouriteList?.clear()
            this.favouriteList?.addAll(favouriteList)
            conductorFavouriteAdapter?.notifyDataSetChanged()
        }
    }

    fun filterFavouriteList(slug: String?, isPullToRefresh: Boolean) {
        searchSlug = slug
        presenter.getFavouriteList(searchSlug, isPullToRefresh)
    }

    override fun setSearchData(favouriteSearchList: List<FavouriteResponse>?) {
        val filterData = favouriteSearchList?.filter { it.type == type }
        if (filterData.isNullOrEmpty()) showErrorLayout()
        else hideErrorLayout()
        favouriteList?.clear()
        favouriteList?.addAll(filterData ?: mutableListOf())
        conductorFavouriteAdapter?.notifyDataSetChanged()
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
            conductorFavouriteAdapter?.notifyItemRemoved(selectedPos ?: 0)
            if (favouriteList.isNullOrEmpty()) {
                showErrorLayout()
            }
            selectedPos = null
        }
    }
}