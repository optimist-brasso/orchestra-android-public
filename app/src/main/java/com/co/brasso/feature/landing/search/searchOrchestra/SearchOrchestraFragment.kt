package com.co.brasso.feature.landing.search.searchOrchestra

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
import com.co.brasso.databinding.FragmentOrchestraSearchBinding
import com.co.brasso.feature.shared.adapter.search.OrchestraSearchAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DialogUtils


class SearchOrchestraFragment : BaseFragment<SearchOrchestraView, SearchOrchestraPresenter>(),
    SearchOrchestraView {

    private lateinit var binding: FragmentOrchestraSearchBinding
    private var orchestraSearchAdapter: OrchestraSearchAdapter? = null
    private var orchestraList: MutableList<HallSoundResponse>? = null
    var fragmentPosition: Int? = Constants.FRAGMENT_CONDUCTOR
     var searchSlug: String? = ""

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrchestraSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    override fun createPresenter() = SearchOrchestraPresenter()

    private fun setUp() {
        initRecyclerView()
        showErrorLayout()
    }

    private fun initRecyclerView() {
        orchestraList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvConductorFavourite.layoutManager = layoutManager
        orchestraSearchAdapter = OrchestraSearchAdapter(orchestraList ?: mutableListOf(), {
            openDetailPage(it)
        }, { addRemoveFavourite(it) })
        binding.rcvConductorFavourite.adapter = orchestraSearchAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addRemoveFavourite(position: Int?) {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(), getString(R.string.please_login), { navigateToLogin() }, {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
        } else {
            favoriteChange(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun favoriteChange(position: Int?) {
        when (fragmentPosition) {
            Constants.FRAGMENT_CONDUCTOR -> {
                orchestraList?.get(position ?: 0)?.isConductorFavourite =
                    !(orchestraList?.get(position ?: 0)?.isConductorFavourite ?: false)
                presenter.addFavourite(
                    FavouriteEntity(
                        ApiConstant.conductor,
                        orchestraList?.get(position ?: 0)?.id.toString()
                    )
                )
                orchestraSearchAdapter?.notifyDataSetChanged()
            }

            Constants.FRAGMENT_HALL_SOUND -> {
                orchestraList?.get(position ?: 0)?.isHallsoundFavourite =
                    !(orchestraList?.get(position ?: 0)?.isHallsoundFavourite ?: false)
                presenter.addFavourite(
                    FavouriteEntity(
                        ApiConstant.hallSound,
                        orchestraList?.get(position ?: 0)?.id.toString()
                    )
                )
                orchestraSearchAdapter?.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapter(hallSoundList: List<HallSoundResponse>?) {
        if (hallSoundList.isNullOrEmpty()) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            orchestraList?.clear()
            orchestraList?.addAll(hallSoundList)
            orchestraSearchAdapter?.notifyDataSetChanged()
        }
    }

    private fun showErrorLayout() {
        binding.rcvConductorFavourite.viewGone()
        binding.lltNoData.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding.rcvConductorFavourite.viewVisible()
        binding.lltNoData.rllNoData.viewGone()
    }

    private fun openDetailPage(hallSoundResponse: HallSoundResponse?) {
        when (fragmentPosition) {
            Constants.FRAGMENT_CONDUCTOR ->
                openOrchestraDetails(hallSoundResponse)
            Constants.FRAGMENT_SESSION ->
                openSessionLayout(hallSoundResponse)
            Constants.FRAGMENT_HALL_SOUND ->
                openHallSoundDetails(hallSoundResponse)
        }
    }

    private fun openOrchestraDetails(hallSoundResponse: HallSoundResponse?) {
        val bundle = bundleOf(BundleConstant.orchestraId to hallSoundResponse?.id)
        bundle.putString(BundleConstant.fragmentType, Constants.conductor)
        NavHostFragment.findNavController(this).navigate(R.id.conductorDetailFragment, bundle)
    }

    private fun openSessionLayout(hallSoundResponse: HallSoundResponse?) {
        val bundle = bundleOf(BundleConstant.orchestraId to hallSoundResponse?.id)
        bundle.putString(BundleConstant.fragmentType, Constants.session)
        NavHostFragment.findNavController(this).navigate(R.id.sessionLayoutFragment, bundle)
    }

    private fun openHallSoundDetails(hallSoundResponse: HallSoundResponse?) {
        val bundle = bundleOf(BundleConstant.orchestraId to hallSoundResponse?.id)
        bundle.putString(BundleConstant.fragmentType, Constants.hallSound)
        NavHostFragment.findNavController(this).navigate(R.id.hallSoundDetailFragment, bundle)
    }
}