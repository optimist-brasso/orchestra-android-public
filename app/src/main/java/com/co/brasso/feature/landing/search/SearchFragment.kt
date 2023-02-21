package com.co.brasso.feature.landing.search

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
import com.co.brasso.databinding.FragmentSearchBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.listSong.listSongSession.ListSongSessionFragment
import com.co.brasso.feature.landing.search.searchOrchestra.SearchOrchestraFragment
import com.co.brasso.feature.shared.adapter.favourite.FavouritePagerAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : BaseFragment<SearchView, SearchPresenter>(), SearchView, TextWatcher {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchConductorFragment: SearchOrchestraFragment
    private lateinit var searchSessionFragment: ListSongSessionFragment
    private lateinit var searchHallSoundFragment: SearchOrchestraFragment
    private var searchPagerAdapter: FavouritePagerAdapter? = null
    private var tabLayoutTitle: MutableList<String>? = null
    private var fragmentList: MutableList<Fragment>? = null
    private var isFirst = true
    private var isDataSearchedLastTime = false
    private var isDataSearchSessionLastTime = false

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentSearchBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setup()
            isFirst = false
        }
        initToolbar()
    }

    private fun setup() {
        initListeners()
        initViewPager()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).setToolBarTitle(getString(R.string.imgSearch))
    }

    private fun initListeners() {
        initListener()
        initSearchData()
        initSearchActionListener()
        initTextChangeListener()
        initTabChangeListener()
    }

    private fun initSearchData() {
        callApiOnSearch(Constants.emptyText, false)
    }

    private fun pullToRefresh() {
        binding.search.edvSearch.removeTextChangedListener(this)
        binding.search.edvSearch.setText(Constants.emptyText)
        callApiOnSearch(Constants.emptyText, true)
        initTextChangeListener()
    }

    override fun createPresenter() = SearchPresenter()

    private fun initTabChangeListener() {
        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (::searchConductorFragment.isInitialized && ::searchHallSoundFragment.isInitialized) {
                    searchConductorFragment.fragmentPosition = tab?.position ?: 0
                    searchHallSoundFragment.fragmentPosition = tab?.position ?: 0
                }
                setSearchData(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun setSearchData(position: Int) {
        when (position) {
            Constants.FRAGMENT_CONDUCTOR -> {
                binding.search.edvSearch.setText(searchConductorFragment.searchSlug)
            }

            Constants.FRAGMENT_SESSION -> {
                binding.search.edvSearch.setText(searchSessionFragment.searchSlug)
            }

            Constants.FRAGMENT_HALL_SOUND -> {
                binding.search.edvSearch.setText(searchHallSoundFragment.searchSlug)
            }
        }
    }

    override fun setOrchestraSearchData(searchList: MutableList<HallSoundResponse>?) {
        searchConductorFragment.setAdapter(searchList?.filter { it.type == Constants.conductor })
        searchHallSoundFragment.setAdapter(searchList?.filter { it.type == Constants.hallSound })
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }

    private fun initTextChangeListener() {
        binding.search.edvSearch.addTextChangedListener(this)
    }

    private fun resetSearchData() {
        initSearchData()
    }

    private fun initSearchActionListener() {
        binding.search.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setSearchStatus()
                searchData(binding.search.edvSearch.text.toString())
                hideKeyboard(binding.search.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setSearchStatus() {
        if (binding.vwPagerSearch.currentItem == 1) {
            isDataSearchSessionLastTime = true
        } else {
            isDataSearchedLastTime = true
        }
    }

    private fun searchData(searchKeyWord: String) {
        if (searchKeyWord.isNotEmpty()) {
            callApiOnSearch(searchKeyWord, false)
        } else {
            setOrchestraSearchData(mutableListOf())
        }
    }

    private fun callApiOnSearch(searchKeyWord: String, isSwipeToRefresh: Boolean) {
        when (binding.vwPagerSearch.currentItem) {
            0->{
                presenter.orchestraSearch(searchKeyWord, isSwipeToRefresh)
            }

            1->{
                searchSessionFragment.getSessionListForSearchAndPullToRefresh(searchKeyWord,isSwipeToRefresh)
            }

            2->{
                presenter.orchestraSearch(searchKeyWord, isSwipeToRefresh)
            }
        }

    }

    private fun initListener() {
        binding.swrSearch.setOnRefreshListener {
            binding.swrSearch.isRefreshing = false
            pullToRefresh()
        }
        binding.search.edvSearch.setOnLongClickListener { false }
    }

    private fun initViewPager() {
        fragmentList = mutableListOf()
        tabLayoutTitle = mutableListOf()

        if (!::searchConductorFragment.isInitialized) {
            searchConductorFragment = SearchOrchestraFragment()
        }
        tabLayoutTitle?.add(getString(R.string.conductor))
        fragmentList?.add(searchConductorFragment)

        if (!::searchSessionFragment.isInitialized) {
            searchSessionFragment = ListSongSessionFragment()
        }
        tabLayoutTitle?.add(getString(R.string.session))
        fragmentList?.add(searchSessionFragment)

        if (!::searchHallSoundFragment.isInitialized) {
            searchHallSoundFragment = SearchOrchestraFragment()
        }
        tabLayoutTitle?.add(getString(R.string.hall_sound))
        fragmentList?.add(searchHallSoundFragment)

        searchPagerAdapter = FavouritePagerAdapter(fragmentList, requireActivity())
        binding.vwPagerSearch.adapter = searchPagerAdapter
        binding.vwPagerSearch.setCurrentItem(0, false)
        binding.vwPagerSearch.offscreenPageLimit = 2
        attachTabLayoutWithViewPager()
    }

    private fun attachTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tbLayout, binding.vwPagerSearch) { tab, position ->
            tab.text = tabLayoutTitle?.get(position)
        }.attach()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.search.edvSearch.text.isEmpty() && getLastSearchStatus()) {
            setLastSearchStatus()
            resetSearchData()
            hideKeyboard(binding.search.edvSearch)
        }
    }

    private fun getLastSearchStatus(): Boolean {
        return if (binding.vwPagerSearch.currentItem == 1) {
            isDataSearchSessionLastTime
        } else {
            isDataSearchedLastTime
        }
    }

    private fun setLastSearchStatus() {
        if (binding.vwPagerSearch.currentItem == 1) {
            isDataSearchSessionLastTime = false
        } else {
            isDataSearchedLastTime = false
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
