package com.co.brasso.feature.landing.favorite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentFavoriteBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.conductor.ConductorFavouriteFragment
import com.co.brasso.feature.landing.favorite.tab.hallsound.HallSoundFavouriteFragment
import com.co.brasso.feature.landing.favorite.tab.player.FavouritePlayerFragment
import com.co.brasso.feature.landing.favorite.tab.session.FavouriteSessionFragment
import com.co.brasso.feature.shared.adapter.favourite.FavouritePagerAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FavoriteFragment : BaseFragment<FavouriteView, FavouritePresenter>(),
    FavouriteView, TextWatcher {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favouriteConductor: ConductorFavouriteFragment
    private lateinit var favouriteHallSound: HallSoundFavouriteFragment
    private lateinit var favouriteSession: FavouriteSessionFragment
    private lateinit var favouritePlayer: FavouritePlayerFragment
    private var favouritePagerAdapter: FavouritePagerAdapter? = null
    private var tabLayoutTitle: MutableList<String>? = null
    private var fragmentList: MutableList<Fragment>? = null
    private var isDataSearchedLastTime = false
    private var isFirst = true

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst)
            binding = FragmentFavoriteBinding.inflate(inflater, container, false)
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
        initViewPager()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
        initListeners()
    }

    private fun initViewPager() {
        fragmentList = mutableListOf()
        tabLayoutTitle = mutableListOf()

        if (!::favouriteConductor.isInitialized) {
            favouriteConductor = ConductorFavouriteFragment()
            favouriteConductor.type = Constants.conductor
        }
        tabLayoutTitle?.add(getString(R.string.conductor))
        fragmentList?.add(favouriteConductor)

        if (!::favouriteSession.isInitialized) {
            favouriteSession = FavouriteSessionFragment()
        }
        tabLayoutTitle?.add(getString(R.string.session))
        fragmentList?.add(favouriteSession)

        if (!::favouriteHallSound.isInitialized) {
            favouriteHallSound = HallSoundFavouriteFragment()
            favouriteHallSound.type = ApiConstant.hallSound
        }
        tabLayoutTitle?.add(getString(R.string.hall_sound))
        fragmentList?.add(favouriteHallSound)

        if (!::favouritePlayer.isInitialized) {
            favouritePlayer = FavouritePlayerFragment()
        }
        tabLayoutTitle?.add(getString(R.string.player))
        fragmentList?.add(favouritePlayer)

        favouritePagerAdapter = FavouritePagerAdapter(fragmentList, requireActivity())
        binding.vwPager.adapter = favouritePagerAdapter
        binding.vwPager.setCurrentItem(0, false)
        binding.vwPager.offscreenPageLimit = 2
        attachTabLayoutWithViewPager()
    }

    private fun attachTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tbLayout, binding.vwPager) { tab, position ->
            tab.text = tabLayoutTitle?.get(position)
        }.attach()
    }

    private fun initToolBar() {
        (activity as LandingActivity).setToolBarTitle(getString(R.string.imgFavourite))
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListeners() {
        initSearchActionListener()
        initTextChangeListener()
        initTabChangeListener()
        initPullToRefresh()
    }

    private fun initPullToRefresh() {
        binding.swrFavourite.setOnRefreshListener {
            binding.swrFavourite.isRefreshing = false
            searchData(StringConstants.emptyString, true)
            binding.search.edvSearch.removeTextChangedListener(this)
            binding.search.edvSearch.setText(StringConstants.emptyString)
            initTextChangeListener()
        }
    }

    private fun initTabChangeListener() {
        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setSearchData(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun createPresenter() = FavouritePresenter()

    override fun setFavouriteList(favouriteList: List<FavouriteResponse>?) {
        favouriteConductor.setAdapter(favouriteList?.filter { it.type?.lowercase() == Constants.conductor })
        favouriteHallSound.setAdapter(favouriteList?.filter { it.type?.lowercase() == Constants.hallSound })
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }

    private fun initSearchActionListener() {
        binding.search.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                isDataSearchedLastTime = true
                searchData(binding.search.edvSearch.text.toString(), false)
                hideKeyboard(binding.search.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun searchData(searchText: String?, isPullToRefresh: Boolean) {
        when (binding.vwPager.currentItem) {
            0 -> {
                favouriteConductor.filterFavouriteList(searchText, isPullToRefresh)
            }
            1 -> {
                favouriteSession.getSessionFavouriteListWithEmptySlug(searchText, isPullToRefresh)
            }
            2 -> {
                favouriteHallSound.filterFavouriteList(searchText, isPullToRefresh)
            }

            3 -> {
                favouritePlayer.searchPlayer(searchText)
            }
        }
    }

    private fun setSearchData(position: Int) {
        when (position) {
            Constants.FRAGMENT_CONDUCTOR -> {
                binding.search.edvSearch.setText(StringConstants.emptyString)
                favouriteConductor.fragmentType = Constants.conductor
            }

            Constants.FRAGMENT_SESSION->{
                binding.search.edvSearch.setText(StringConstants.emptyString)
                favouriteSession.fragmentType = Constants.session
            }

            Constants.FRAGMENT_HALL_SOUND -> {
                binding.search.edvSearch.setText(StringConstants.emptyString)
                favouriteHallSound.fragmentType = Constants.hallSound
            }

            Constants.FRAGMENT_PLAYER -> {
                binding.search.edvSearch.setText(StringConstants.emptyString)
            }
        }
    }

    private fun initTextChangeListener() {
        binding.search.edvSearch.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.search.edvSearch.text.isEmpty() && isDataSearchedLastTime) {
            isDataSearchedLastTime = false
            hideKeyboard(binding.search.edvSearch)
            searchData(binding.search.edvSearch.text.toString(), false)
        }

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onResume() {
        super.onResume()

        if (::favouriteConductor.isInitialized) {
            favouriteConductor.onResume()
        }

        if (::favouriteHallSound.isInitialized) {
            favouriteHallSound.onResume()
        }

        if (::favouriteSession.isInitialized) {
            favouriteSession.onResume()
        }

        if (::favouritePlayer.isInitialized) {
            favouritePlayer.onResume()
        }
    }
}
