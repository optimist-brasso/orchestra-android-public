package com.co.brasso.feature.landing.listSong

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
import com.co.brasso.databinding.FragmentListSongBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.listSong.listSongHallSound.ListSongHallSoundFragment
import com.co.brasso.feature.landing.listSong.listSongOrchestra.ListSongOrchestraFragment
import com.co.brasso.feature.landing.listSong.listSongSession.ListSongSessionFragment
import com.co.brasso.feature.shared.adapter.favourite.FavouritePagerAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListSongFragment : BaseFragment<ListSongView, ListSongPresenter>(), ListSongView,
    TextWatcher {

    private lateinit var binding: FragmentListSongBinding
    private var tabLayoutTitle: MutableList<String>? = null
    private var fragmentList: MutableList<Fragment>? = null
    private lateinit var listSongConductorFragment: ListSongOrchestraFragment
    private lateinit var listSongSessionFragment: ListSongSessionFragment
    private lateinit var listSongHallSoundFragment: ListSongHallSoundFragment
    private var favouritePagerAdapter: FavouritePagerAdapter? = null
    private var isFirst = true
    private var isDataSearchedLastTime = false

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst)
            binding = FragmentListSongBinding.inflate(inflater, container, false)
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
        initViewPager()
        initListeners()
    }

    private fun initListeners() {
        initSearchActionListener()
        initTextChangeListener()
        initTabChangeListener()
        initPullToRefresh()
    }

    private fun initPullToRefresh() {
        binding.swrPlayList.setOnRefreshListener {
            binding.swrPlayList.isRefreshing = false
            binding.search.edvSearch.removeTextChangedListener(this)
            binding.search.edvSearch.setText(StringConstants.emptyString)
            initTextChangeListener()
            getSongData(StringConstants.emptyString, true)
        }
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).setToolBarTitle(getString(R.string.imgListOfSongs))
    }

    private fun initTabChangeListener() {
        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (::listSongConductorFragment.isInitialized && ::listSongHallSoundFragment.isInitialized && ::listSongSessionFragment.isInitialized) {
                    listSongConductorFragment.fragmentPosition = tab?.position ?: 0
                    listSongHallSoundFragment.fragmentPosition = tab?.position ?: 0
                }
                setSearchData(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun initViewPager() {
        fragmentList = mutableListOf()
        tabLayoutTitle = mutableListOf()

        if (!::listSongConductorFragment.isInitialized) {
            listSongConductorFragment = ListSongOrchestraFragment()
            listSongConductorFragment.type = Constants.conductor
        }
        tabLayoutTitle?.add(getString(R.string.conductor))
        fragmentList?.add(listSongConductorFragment)

        if (!::listSongSessionFragment.isInitialized) {
            listSongSessionFragment = ListSongSessionFragment()
        }
        tabLayoutTitle?.add(getString(R.string.session))
        fragmentList?.add(listSongSessionFragment)

        if (!::listSongHallSoundFragment.isInitialized) {
            listSongHallSoundFragment = ListSongHallSoundFragment()
            listSongHallSoundFragment.type = Constants.hallSound
        }
        tabLayoutTitle?.add(getString(R.string.hall_sound))
        fragmentList?.add(listSongHallSoundFragment)

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

    override fun createPresenter() = ListSongPresenter()

    override fun setOrchestraList(list: List<HallSoundResponse>) {
        listSongConductorFragment.setAdapter(list.filter { it.type == Constants.conductor })
        listSongHallSoundFragment.setAdapter(list.filter { it.type == Constants.hallSound })
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

    private fun initSearchActionListener() {
        binding.search.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                isDataSearchedLastTime = true
                getSongData(binding.search.edvSearch.text.toString(), false)
                hideKeyboard(binding.search.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setSearchData(position: Int) {
        when (position) {
            Constants.FRAGMENT_CONDUCTOR -> {
                binding.search.edvSearch.setText(listSongConductorFragment.searchSlug)
            }

            Constants.FRAGMENT_SESSION -> {
                binding.search.edvSearch.setText(listSongSessionFragment.searchSlug)
            }

            Constants.FRAGMENT_HALL_SOUND -> {
                binding.search.edvSearch.setText(listSongHallSoundFragment.searchSlug)
            }
        }
    }

    private fun getSongData(searchSlug: String?, isPullToRefresh: Boolean) {
        when (binding.vwPager.currentItem) {
            Constants.FRAGMENT_CONDUCTOR -> {
                listSongConductorFragment.searchSlug = searchSlug
                listSongConductorFragment.filterFavouriteList(searchSlug, isPullToRefresh)
            }

            Constants.FRAGMENT_SESSION->{
                listSongSessionFragment.getSessionListForSearchAndPullToRefresh(searchSlug,isPullToRefresh)
            }

            Constants.FRAGMENT_HALL_SOUND -> {
                listSongHallSoundFragment.searchSlug = searchSlug
                listSongHallSoundFragment.filterFavouriteList(searchSlug, isPullToRefresh)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.search.edvSearch.text.isEmpty() && isDataSearchedLastTime) {
            getSongData(binding.search.edvSearch.text.toString(), false)
            hideKeyboard(binding.search.edvSearch)
            isDataSearchedLastTime = false
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onResume() {
        super.onResume()

        if (::listSongConductorFragment.isInitialized) {
            listSongConductorFragment.onResume()
        }

        if (::listSongSessionFragment.isInitialized) {
            listSongSessionFragment.onResume()
        }

        if (::listSongHallSoundFragment.isInitialized) {
            listSongHallSoundFragment.onResume()
        }
    }
}
