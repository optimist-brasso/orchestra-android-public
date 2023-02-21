package com.co.brasso.feature.cart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentCartListBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.cartList.CartListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DialogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartListFragment : BaseFragment<CartListView, CartListPresenter>(),
    CartListView {

    lateinit var binding: FragmentCartListBinding
    private lateinit var cartListAdapter: CartListAdapter
    private var cartListResponse: MutableList<CartListResponse>? = null
    private var navBar: BottomNavigationView? = null
    private var isFirst = true
    var fragmentType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentCartListBinding.inflate(inflater, container, false)
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
        initToolBar()
    }

    private fun setup() {
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)
        getBundleData()
        initRecyclerView()
        getCartList()
        hideViews()
        initListeners()
    }

    private fun getBundleData() {
        fragmentType = arguments?.getString(BundleConstant.fragmentType)
        val stateManagement=arguments?.getBoolean(BundleConstant.stateManagement)
        if(stateManagement==true){
            (requireContext() as? LandingActivity)?.shouldRefreshHome=true
        }
    }

    private fun updateSessionDetail(purchaseType: String?, orchestraId: String?) {
        val intent = Intent()
        intent.action = StringConstants.sessionBroadCastAction
        intent.putExtra(BundleConstant.sessionId, orchestraId)
        intent.putExtra(
            BundleConstant.session,
            purchaseType
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun updateMultiPartList(cartItem: CartListResponse?) {
        val intent = Intent()
        intent.action = StringConstants.sessionMultiPartBroadCastAction
        intent.putExtra(BundleConstant.cartItem, cartItem)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun updateHallSound() {
        val intent = Intent()
        intent.action = StringConstants.hallSoundBroadCastAction
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun initListeners() {
        binding.btnBuy.setOnClickListener {
            val cartSelectedItem = cartListResponse?.filter { it.isChecked == true }
            if (cartSelectedItem.isNullOrEmpty()) {
                showMessageDialog(R.string.purchase_item_empty)
                return@setOnClickListener
            }
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.are_you_sure_you_want_to_buy),
                {
                    purchaseCartItem(cartSelectedItem as MutableList<CartListResponse>)
                },
                {},
                isCancelable = false,
                showNegativeBtn = true
            )
        }

        binding.swrCart.setOnRefreshListener {
            binding.swrCart.isRefreshing = false
            presenter.getCartList(true)
        }
    }

    private fun purchaseCartItem(cartSelectedItem: MutableList<CartListResponse>) {
        presenter.buyOrchestra(cartSelectedItem)
    }

    private fun getCartList() {
        presenter.getCartList(false)
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.hideCart()
        (activity as? LandingActivity)?.showNotificationIcon()
        navBar?.viewVisible()
        binding.search.edvSearch.viewGone()
    }

    private fun hideViews() {
        binding.search.edvSearch.alpha = 0F
    }

    private fun addRemoveItem(cartListResponse: CartListResponse?) {
        val cartStatus = this.cartListResponse?.find { it.id == cartListResponse?.id }
        cartStatus?.isChecked = !(cartStatus?.isChecked ?: false)
        cartListAdapter.notifyDataSetChanged()
    }

    private fun deleteCartItem(id: Int?) {
        presenter.deleteCartItem(id)
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    private fun initRecyclerView() {
        cartListResponse = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvCartList.layoutManager = layoutManager
        cartListAdapter = CartListAdapter(this.cartListResponse, {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.delete_cart),
                {
                    deleteCartItem(it?.id)
                },
                {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.delete),
                getString(R.string.cancel)
            )
        }, {
            addRemoveItem(it)
        })
        binding.rcvCartList.adapter = cartListAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setCartList(cartListResponse: List<CartListResponse>) {
        if (cartListResponse.isEmpty()) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            this.cartListResponse?.clear()
            this.cartListResponse?.addAll(cartListResponse)
            cartListAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteSuccess(message: String?, id: Int?) {
        val cart = cartListResponse?.find { it.id == id }
        val cartInfo = AppInfoGlobal.cartInfo?.find { it.id == id }
        AppInfoGlobal.cartInfo?.remove(cartInfo)
        cartListResponse?.remove(cart)
        cartListAdapter.notifyDataSetChanged()
        if (this.cartListResponse.isNullOrEmpty()) {
            binding.rcvCartList.viewGone()
            binding.lltBtnBuy.viewGone()
            binding.lltNoData.rllNoData.viewVisible()
        } else {
            binding.rcvCartList.viewVisible()
            binding.lltBtnBuy.viewVisible()
            binding.lltNoData.rllNoData.viewGone()
        }
    }

    override fun purchaseSuccess(message: String?) {
        val cartSelectedItem: CartListResponse? = if (fragmentType == Constants.hallSound) {
            cartListResponse?.find { it.isChecked == true && it.type == Constants.hallSound }
        } else {
            cartListResponse?.find { it.isChecked == true && it.type == Constants.session }
        }
        showMessageDialog(message ?: "")
        getCartList()
        updateSessionPage(
            cartSelectedItem?.sessionType,
            cartSelectedItem?.orchestraId,
            cartSelectedItem
        )
    }

    private fun updateSessionPage(
        purchaseType: String?,
        orchestraId: Int?,
        cartItem: CartListResponse?
    ) {
        when (fragmentType) {
            Constants.session -> {
                updateSessionDetail(purchaseType, orchestraId.toString())
            }
            Constants.premium -> {
                updatePremiumDetail(purchaseType, orchestraId.toString())
            }

            Constants.appendix -> {
                updateAppendixDetail(purchaseType, orchestraId.toString())
            }
            Constants.multiPart -> {
                updateMultiPartList(cartItem)
            }
            Constants.hallSound -> {
                updateHallSound()
            }
        }
    }

    private fun updatePremiumDetail(purchaseType: String?, orchestraId: String?) {
        val intent = Intent()
        intent.action = StringConstants.sessionPremiumBroadCastAction
        intent.putExtra(BundleConstant.sessionId, orchestraId)
        intent.putExtra(
            BundleConstant.session,
            purchaseType
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun updateAppendixDetail(purchaseType: String?, orchestraId: String?) {
        val intent = Intent()
        intent.action = StringConstants.sessionAppendixVideoBroadCastAction
        intent.putExtra(BundleConstant.sessionId, orchestraId)
        intent.putExtra(
            BundleConstant.session,
            purchaseType
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    override fun createPresenter(): CartListPresenter = CartListPresenter()

    private fun showErrorLayout() {
        binding.rcvCartList.viewGone()
        binding.lltBtnBuy.viewGone()
        binding.lltNoData.rllNoData.viewVisible()
    }

    private fun hideErrorLayout() {
        binding.rcvCartList.viewVisible()
        binding.lltBtnBuy.viewVisible()
        binding.lltNoData.rllNoData.viewGone()
    }

}