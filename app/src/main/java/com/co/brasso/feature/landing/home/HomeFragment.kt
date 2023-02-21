package com.co.brasso.feature.landing.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.co.brasso.R
import com.co.brasso.databinding.FragmentHomeBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.InfiniteRecyclerAdapter
import com.co.brasso.feature.shared.adapter.RecommendationAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.listener.AppBarStateChangeListener
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.home.Banner
import com.co.brasso.feature.shared.model.response.home.HomeData
import com.co.brasso.feature.shared.model.response.home.Recommendation
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.Utils
import com.google.android.material.appbar.AppBarLayout
import java.util.*

class HomeFragment : BaseFragment<HomeView, HomePresenter>(), View.OnClickListener, HomeView {

    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: RecommendationAdapter
    private var recommendations: MutableList<Recommendation>? = null
    private lateinit var recommendationAdapter: RecommendationAdapter
    private var listSize: Int = 0
    private var isFirst = true
    private var timer: Timer? = null
    private var bannerTwoOneTwoList:MutableList<Banner>?=null
    private var bannerOneTwoOneList:MutableList<Banner> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst || (requireContext() as? LandingActivity)?.shouldRefreshHome == true) {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst || (requireContext() as? LandingActivity)?.shouldRefreshHome == true) {
            setUp()
            initListeners()
            presenter.getBanner(false)
            isFirst = false
            (requireContext() as? LandingActivity)?.shouldRefreshHome = false
        }
        initPointBalance()
        initToolBar()
    }

    private fun initToolBar() {
        (activity as LandingActivity).setToolBarTitle(getString(R.string.imgHome))
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showCartCount()
        (activity as LandingActivity).showToolbar()
        (activity as LandingActivity).showBottomBar()
        updateNotificationCount()
    }

    override fun updateNotificationCount() {
        (requireContext() as? LandingActivity)?.showNotificationIcon()
    }

    private fun setUp() {
        initRecommendedRecyclerView()
        initViewPagerAutoSlider()
    }

    private fun initPointBalance() {
        if (getLoginState()) {
            presenter.getBundleList()
            binding.cnlBalance.viewVisible()
            binding.rcvRecommendation.setPadding(0, 0, 0, 175)
        } else {
            binding.cnlBalance.viewGone()
            binding.rcvRecommendation.setPadding(0, 0, 0, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setBundleList(bundleList: GetBundleInfo) {
        bundleList.products?.paidPoint?.let {
            binding.txtTotalPoints.text = "%,d".format(
                (bundleList.products?.paidPoint?.plus(bundleList.products?.freePoint ?: 0))
            ) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txtTotalPoints
                .text = getString(R.string.zero)
        }

//        val cnlHeight = binding.imvPointImage.layoutParams.height
//        Toast.makeText(requireContext(), cnlHeight.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun initViewPagerAutoSlider() {
        if (timer != null)
            return
       setViewpagerTimer()
    }

    private fun setViewpagerTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                (requireContext() as? LandingActivity)?.runOnUiThread {
                    changeViewPagerRight()
                }
            }
        }, 3000, 3000)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    private fun initSwipeListener() {
        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                when (state) {
                    State.COLLAPSED -> {
                        binding.swpRefresh.isEnabled = false
                    }

                    State.EXPANDED -> {
                        binding.swpRefresh.isEnabled = true
                    }

                    State.IDLE -> {
                        binding.swpRefresh.isEnabled = false
                    }

                    else -> {
                        binding.swpRefresh.isEnabled = true
                    }
                }
            }

        })
    }

    private fun initRecommendedRecyclerView() {
        recommendations = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvRecommendation.layoutManager = layoutManager
        recommendationAdapter = RecommendationAdapter(recommendations) {
            openOrchestraDetails(it)
        }
        binding.rcvRecommendation.adapter = recommendationAdapter
    }

    private fun initListeners() {
        binding.imvBannerBack.setOnClickListener(this)
        binding.imvBannerForward.setOnClickListener(this)
        binding.header.imvConductor.setOnClickListener(this)
        binding.header.imvSession.setOnClickListener(this)
        binding.header.imvHallsound.setOnClickListener(this)
        binding.header.imvPlayer.setOnClickListener(this)
        binding.swpRefresh.setOnRefreshListener {
            binding.swpRefresh.isRefreshing = false
            presenter.getBanner(true)
            if (getLoginState()) {
                presenter.getBundleList()
            }
        }
        initSwipeListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.imvBannerBack -> {
                changeViewPagerLeft()
            }

            R.id.imvBannerForward -> {
                changeViewPagerRight()
            }

            R.id.imvConductor -> {
                openOrchestra(Constants.conductor)
            }

            R.id.imvPlayer -> {
                openOrchestra(Constants.player)
            }

            R.id.imvSession -> {
                openOrchestra(Constants.session)
            }

            R.id.imvHallsound -> {
                openOrchestra(Constants.hallSound)
            }
        }
    }

    private fun openOrchestraDetails(recommendation: Recommendation?) {
        val bundle = bundleOf(BundleConstant.orchestraId to recommendation?.id)
        bundle.putString(BundleConstant.fragmentType, Constants.conductor)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.conductorDetailFragment, bundle)
    }

    private fun openOrchestra(type: String) {
        val bundle = bundleOf(BundleConstant.fragmentType to type)
        Navigation.findNavController(requireView()).navigate(R.id.orchestraFragment, bundle)
    }

    override fun createPresenter() = HomePresenter()

    override fun setBanner(banners: List<Banner>?) {
        if (banners.isNullOrEmpty()) {
            binding.rrlBanner.viewGone()
            return
        } else {
            binding.rrlBanner.viewVisible()
        }
        val bannersList:MutableList<Banner> = mutableListOf()
        bannersList.addAll(banners)
        if(bannersList.size==1){
            bannersList.add(bannersList[0])
            bannersList.add(bannersList[0])
        }

        else if(bannersList.size==2){
            bannersList.add(bannersList[0])
            bannersList.add(bannersList[1])
        }
        val arrayList = arrayListOf<Int>()
        List(bannersList.size) { index ->
            arrayList.add(index + 1)
        }
        val infiniteRecyclerAdapter = InfiniteRecyclerAdapter(bannersList) {
            handleBannerClick(it)
        }
        binding.vwPager.adapter = infiniteRecyclerAdapter
        binding.vwPager.currentItem = 1
        binding.vwPager.apply {
            offscreenPageLimit = 2
            val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.defaultMargin_3_5x)
            val offsetPx = resources.getDimensionPixelOffset(R.dimen.defaultMargin)

            setPageTransformer { page, position ->
                val offset = position * -(2 * offsetPx + pageMarginPx)
                page.translationX = offset
            }
        }
        onInfinitePageChangeCallback((bannersList.size) + 2)
    }

    private fun handleBannerClick(banner: Banner) {
        if (banner.link?.url.isNullOrEmpty()) {
            if (!banner.link?.description.isNullOrEmpty()) {
                val bundle = bundleOf(BundleConstant.bannerData to banner)
                Navigation.findNavController(requireView())
                    .navigate(R.id.bannerDetailFragment, bundle)
            }
        } else {
            Utils.openLink(banner.link?.url, requireContext())
        }
    }

    private fun changeViewPagerRight() {
        removeAndSetTimer()
        when (binding.vwPager.currentItem) {
            listSize - 1 -> binding.vwPager.setCurrentItem(1, true)
            0 -> binding.vwPager.setCurrentItem(listSize - 2, true)
            else ->
                binding.vwPager.setCurrentItem((binding.vwPager.currentItem) + 1, true)
        }
    }

    private fun removeAndSetTimer() {
        timer?.cancel()
        timer=null
        setViewpagerTimer()
    }

    private fun changeViewPagerLeft() {
        removeAndSetTimer()
        when (binding.vwPager.currentItem) {
            listSize - 1 -> binding.vwPager.setCurrentItem(1, true)
            0 -> binding.vwPager.setCurrentItem(listSize - 2, true)
            else ->
                binding.vwPager.setCurrentItem((binding.vwPager.currentItem) - 1, true)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecommendation(recommendations: MutableList<Recommendation>?) {
        this.recommendations?.clear()
        this.recommendations?.addAll(recommendations ?: mutableListOf())
        this.recommendations?.add(Recommendation(viewType = Constants.recommendedBusinessSection))
        recommendationAdapter.notifyDataSetChanged()
    }

    override fun setHomeData(it: HomeData?) {
        if (it?.banners.isNullOrEmpty() && it?.recommendations.isNullOrEmpty()) {
            binding.llNoData.rllNoData.viewVisible()
            binding.rcvRecommendation.viewGone()
            binding.rrlBanner.viewGone()
            return
        } else {
            binding.llNoData.rllNoData.viewGone()
            binding.rcvRecommendation.viewVisible()
            binding.rrlBanner.viewVisible()
        }
        setBanner(it?.banners)
        setRecommendation(presenter.getFilteredRecommendedList(it?.recommendations as MutableList<Recommendation>?))
    }

    private fun onInfinitePageChangeCallback(listSize: Int) {
        this.listSize = listSize
        binding.vwPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                removeAndSetTimer()
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (binding.vwPager.currentItem) {
                        listSize - 1 -> binding.vwPager.setCurrentItem(1, false)
                        0 -> binding.vwPager.setCurrentItem(listSize - 2, false)
                    }
                }
            }
        })
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

}