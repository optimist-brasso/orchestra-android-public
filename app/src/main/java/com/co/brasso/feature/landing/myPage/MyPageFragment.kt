package com.co.brasso.feature.landing.myPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.co.brasso.R
import com.co.brasso.databinding.FragmentMyPageBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.myPage.tab.MyPageMoreFragmentTab
import com.co.brasso.feature.landing.myPage.tab.MyPageProfileFragmentTab
import com.co.brasso.feature.landing.myPage.tab.MyPageSettingFragmentTab
import com.co.brasso.feature.shared.adapter.MyPagePagerAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MyPageFragment : BaseFragment<MyPageFragmentView, MyPageFragmentPresenter>(),
    MyPageFragmentView {

    private lateinit var binding: FragmentMyPageBinding
    private var pagerAdapter: MyPagePagerAdapter? = null
    private var fragmentList: MutableList<Fragment>? = null
    private var isFirst = true
    private lateinit var pageProfileFragment: MyPageProfileFragmentTab
    private lateinit var myPageSettingFragment: MyPageSettingFragmentTab
    private lateinit var myPageMoreFragment: MyPageMoreFragmentTab

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        setScreenPortrait()
    }

    private fun setUp() {
        setViewPagerAdapter()
        attachTabLayoutWithViewPager()
        addPagerChangeListener()
        initToolBar()
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.showCartCount()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.setToolBarTitle(getString(R.string.imgMyPage))
        (activity as? LandingActivity)?.showBottomBar()
    }

    private fun attachTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tbLayout, binding.vwPager) { tab, position ->
            addTabTitleAndIcon(position, tab)
        }.attach()
        setTabSelected(0)
    }

    private fun setTabSelected(position: Int) {
        when (position) {
            0 -> {
                binding.tbLayout.getTabAt(position)?.setIcon(R.drawable.ic_my_page_black)
                binding.tbLayout.getTabAt(1)?.setIcon(R.drawable.ic_setting)
                binding.tbLayout.getTabAt(2)?.setIcon(R.drawable.ic_more)
            }
            1 -> {
                binding.tbLayout.getTabAt(position)?.setIcon(R.drawable.ic_setting_black)
                binding.tbLayout.getTabAt(0)?.setIcon(R.drawable.ic_my_page)
                binding.tbLayout.getTabAt(2)?.setIcon(R.drawable.ic_more)
            }
            else -> {
                binding.tbLayout.getTabAt(position)?.setIcon(R.drawable.ic_more_black)
                binding.tbLayout.getTabAt(0)?.setIcon(R.drawable.ic_my_page)
                binding.tbLayout.getTabAt(1)?.setIcon(R.drawable.ic_setting)
            }
        }
    }

    private fun addPagerChangeListener() {
        binding.vwPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                setTabSelected(binding.vwPager.currentItem)
                super.onPageScrollStateChanged(state)

            }
        })

        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                popUpSettingBackStack(tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                popUpSettingBackStack(tab?.position)
            }

        })
    }

    private fun popUpSettingBackStack(position: Int?) {
        when (position) {
            0 -> popUpBackStack()
            1 -> popUpBackStackProfileSettings()
            2 -> popUpBackStackOtherSettings()
        }
    }

    private fun addTabTitleAndIcon(position: Int, tab: TabLayout.Tab) {

        when (position) {
            0 -> {
                tab.text = getString(R.string.my_page)
                tab.setIcon(R.drawable.ic_my_page)
            }

            1 -> {
                tab.text = getString(R.string.setting)
                tab.setIcon(R.drawable.ic_setting)
            }

            2 -> {
                tab.text = getString(R.string.other_page)
                tab.setIcon(R.drawable.ic_more)
            }
        }
    }

    private fun popUpBackStack() {
        val profileFragment = fragmentList?.get(0) as? MyPageProfileFragmentTab?
        profileFragment?.popUpBackStack()
    }

    private fun popUpBackStackProfileSettings() {
        val profileFragment = fragmentList?.get(1) as? MyPageSettingFragmentTab?
        profileFragment?.popUpBackStack()
    }

    private fun popUpBackStackOtherSettings() {
        val profileFragment = fragmentList?.get(2) as? MyPageMoreFragmentTab?
        profileFragment?.popUpBackStack()
    }

    private fun setViewPagerAdapter() {
        fragmentList = mutableListOf()

        if (!::pageProfileFragment.isInitialized) pageProfileFragment = MyPageProfileFragmentTab()
        fragmentList?.add(pageProfileFragment)

        if (!::myPageSettingFragment.isInitialized) myPageSettingFragment =
            MyPageSettingFragmentTab()
        fragmentList?.add(myPageSettingFragment)

        if (!::myPageMoreFragment.isInitialized) myPageMoreFragment = MyPageMoreFragmentTab()
        fragmentList?.add(myPageMoreFragment)

        pagerAdapter = MyPagePagerAdapter(fragmentList, requireActivity())
        binding.vwPager.adapter = pagerAdapter
        binding.vwPager.currentItem = 0
        binding.vwPager.offscreenPageLimit = (fragmentList?.size ?: 0) - 1
    }

    override fun createPresenter() = MyPageFragmentPresenter()
}
