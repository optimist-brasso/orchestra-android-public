package com.co.brasso.feature.landing.myPage.tab.myPage.points

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.co.brasso.R
import com.co.brasso.databinding.FragmentPointsListBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.pointListAdapter.PointListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.BoughtInAppProduct
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.bundleList.BundleListItem
import com.co.brasso.feature.shared.model.response.bundleList.BundleListResponse
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.DialogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class PointsListFragment : BaseFragment<PointsListView, PointsListPresenter>(),
    PointsListView {

    private lateinit var binding: FragmentPointsListBinding
    private var isFirst = true
    private var pointListAdapter: PointListAdapter? = null
    private var pointList: MutableList<BundleListItem>? = null
    private var bundlesItem: BundleListItem? = null
    private var billingClient: BillingClient? = null
   // private var navBar: BottomNavigationView? = null
    private var profileResponse: MyProfileResponse? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentPointsListBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
            isFirst = false
        }
        initToolbar()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.setToolBarTitle(getString(R.string.textPoints))
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun setUp() {
//        initBoardCastListener()
        initListener()
        initRecyclerView()
        retrieveBundleList(false)
        initGoogleInAppBilling()
    }

    private fun initListener() {
        binding.swrPointsList.setOnRefreshListener {
            binding.swrPointsList.isRefreshing = false
            retrieveBundleList(true)
        }

        binding.txvConfirm.setOnClickListener {
            (activity as? LandingActivity)?.proceedToFreePoints()
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        val boughtProducts = BoughtProducts()
                        val listOfProducts: MutableList<BoughtInAppProduct> = mutableListOf()
                        val boughtInAppPurchase =
                            BoughtInAppProduct(
                                purchase.orderId,
                                purchase.packageName,
                                purchase.products[0],
                                purchase.purchaseTime,
                                purchase.purchaseState,
                                purchase.purchaseToken,
                                purchase.quantity,
                                purchase.isAcknowledged,
                            )
                        if (boughtInAppPurchase.productId == bundlesItem?.identifier) {
                            listOfProducts.add(boughtInAppPurchase)
                            boughtProducts.products?.addAll(listOfProducts)
                            purchaseValidation(boughtProducts, purchase)
                        }
                    }
                }
            }
        }

    private fun purchaseValidation(boughtProducts: BoughtProducts?, purchase: Purchase) {
        presenter.purchaseVerify(
            (activity as? LandingActivity),
            billingClient,
            boughtProducts,
            purchase
        )
    }

    private fun initGoogleInAppBilling() {
        if (billingClient != null)
            return
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {}

            override fun onBillingServiceDisconnected() {
               // onBackPressedHandler()
            }
        })
    }

    private fun onBackPressedHandler() {
//        navBar?.visibility = View.VISIBLE
//        findNavController().popBackStack()
    }

    override fun purchaseSuccess(message: String?) {
        showMessageDialog(getString(R.string.pointBoughtSuccess)) {
            val totalPoints = profileResponse?.totalPoints?.plus(bundlesItem?.paidPoint ?: 0)
            profileResponse?.totalPoints = totalPoints
            retrieveBundleList(false)
        }
    }

    override fun buyFromInAppPurchase(productDetailsList: MutableList<ProductDetails>?) {
        val productDetail =
            productDetailsList?.find { it.productId == bundlesItem?.identifier }
        if (productDetail != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetail)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)
        } else {
            showMessage(R.string.text_product_not_found)
        }
    }

    private fun retrieveBundleList(isPullToRefresh: Boolean) {
        presenter.getBundleList(isPullToRefresh)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        pointList = mutableListOf()
        binding.rcvPointList.layoutManager = layoutManager
        pointListAdapter = PointListAdapter(pointList ?: mutableListOf()) {
            showPurchaseDialog(it)
        }
        binding.rcvPointList.adapter = pointListAdapter
    }

    private fun showPurchaseDialog(bundlesItem: BundleListItem?) {
        this.bundlesItem = bundlesItem
        DialogUtils.showAlertDialog(
            requireContext(), getString(R.string.are_you_sure_you_want_to_buy_this_point), {
                buyOrchestra(bundlesItem)
            },
            {},
            isCancelable = false,
            showNegativeBtn = true
        )
    }

    private fun buyOrchestra(bundlesItem: BundleListItem?) {
        if (!bundlesItem?.identifier.isNullOrEmpty())
            presenter.checkProductAvailability(
                requireActivity(),
                bundlesItem?.identifier,
                billingClient
            )
        else
            showMessageDialog(R.string.text_product_not_found)
    }

    private fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun setBundleList(bundleList: GetBundleInfo) {
        profileResponse = bundleList.profileResponse
        if (bundleList.products?.bundleList?.isEmpty() == true) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            this.pointList?.clear()
            val activeList =
                bundleList.products?.bundleList?.filter { it.status == StringConstants.active }
            pointList?.addAll(activeList ?: mutableListOf())
            pointListAdapter?.notifyDataSetChanged()
        }

        setData(bundleList.products)
    }

    @SuppressLint("SetTextI18n")
    private fun setData(products: BundleListResponse?) {
        products?.paidPoint?.let {
            binding.txtPaidPointsCount.text =
                "%,d".format(products.paidPoint) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txtPaidPointsCount.text = getString(R.string.zero)
        }

        products?.freePoint?.let {
            binding.txvLimitedPointsCount.text =
                "%,d".format(products.freePoint) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txvLimitedPointsCount.text = getString(R.string.zero)
        }

        products?.firstExpiry?.date?.let {
            binding.txvExpiryDate.viewVisible()
            binding.txtExpiryPointsCount.viewVisible()

            binding.txvExpiryDate.text = DateUtils.getBillHistoryDateYear(
                products.firstExpiry.date
            ) + getString(R.string.year_text) +
                    DateUtils.getBillHistoryDateMonth(
                        products.firstExpiry.date

                    ) + getString(R.string.month_text) +
                    DateUtils.getBillHistoryDateDay(
                        products.firstExpiry.date

                    ) + getString(R.string.day_text) + getString(R.string.until)

            binding.txtExpiryPointsCount.text =
                "%,d".format(products.firstExpiry.point) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txvExpiryDate.viewGone()
            binding.txtExpiryPointsCount.viewGone()
        }

        products?.secondExpiry?.date?.let {
            binding.txvFreePointsExpiry.viewVisible()

            binding.txvFreePointsExpiry.text = DateUtils.getBillHistoryDateYear(
                products.secondExpiry.date
            ) + getString(R.string.year_text) +
                    DateUtils.getBillHistoryDateMonth(
                        products.secondExpiry.date
                    ) + getString(R.string.month_text) +
                    DateUtils.getBillHistoryDateDay(
                        products.secondExpiry.date
                    ) + getString(R.string.day_text) + getString(R.string.until)

        } ?: kotlin.run {
            binding.txvFreePointsExpiry.viewGone()
        }

        products?.paidPoint?.let {
            binding.txtTotalPoints.text = "%,d".format(
                (products.paidPoint.plus(products.freePoint ?: 0))
            ) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txtTotalPoints
                .text = getString(R.string.zero)
        }
    }

    override fun createPresenter() = PointsListPresenter()

}