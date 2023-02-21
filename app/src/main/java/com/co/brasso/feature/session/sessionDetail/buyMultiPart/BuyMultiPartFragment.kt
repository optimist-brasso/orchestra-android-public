package com.co.brasso.feature.session.sessionDetail.buyMultiPart

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentBuyMulitiplePartBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.multiPartList.MultiPartListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.cart.AddToCart
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils

class BuyMultiPartFragment : BaseFragment<BuyMultiPartView, BuyMultiPartPresenter>(),
    BuyMultiPartView {

    private lateinit var binding: FragmentBuyMulitiplePartBinding
    private var sessionId: Int = 0
    private var multiPartListAdapter: MultiPartListAdapter? = null
    private var cartItem: MutableList<AddToCart>? = null
    private var multiPartList: MutableList<MultiPartListResponseItem>? = null
    var hallSoundResponse: HallSoundResponse? = null
    val purchasedItemsList: MutableList<CartListResponse> = mutableListOf()
    var multiPartListResponseItem: MultiPartListResponseItem? = null
    var isFirst: Boolean = true
    var fromPage: String? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setScreenPortrait()
        if (isFirst) {
            binding = FragmentBuyMulitiplePartBinding.inflate(layoutInflater)
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

    private fun initToolBar() {
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showCartCount()
        (activity as? LandingActivity)?.showNotificationIcon()
    }

    private fun setup() {
        initPurchaseBroadCast()
        initRecyclerView()
        initListeners()
        getSessionData()
        getMultiPartList()
    }

    private fun initPurchaseBroadCast() {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            purchaseCompleteBoardCast,
            IntentFilter(StringConstants.sessionMultiPartBroadCastAction)
        )
    }

    private var purchaseCompleteBoardCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            val cartItem =
                intent?.getSerializableExtra(BundleConstant.cartItem) as? CartListResponse
            buySuccessFromCart(cartItem)
        }
    }

    private fun getSessionData() {
        hallSoundResponse =
            arguments?.getSerializable(Constants.session) as? HallSoundResponse?
        sessionId = hallSoundResponse?.sessionId ?: 0
        setSessionData()
    }

    private fun setSessionData() {
        if (hallSoundResponse?.fromPage.equals(StringConstants.partPage)) {
            binding.txtBuyMultiPartAtOnce.text = getString(R.string.part_multi_part)
        } else {
            binding.txtBuyMultiPartAtOnce.text = getString(R.string.premium_multi_part)
        }
        binding.txtTitleEng.text = hallSoundResponse?.title
        binding.txtTitleJpn.text = hallSoundResponse?.titleJp
    }

    private fun initRecyclerView() {
        multiPartList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvMultiPart.layoutManager = layoutManager
        multiPartListAdapter = MultiPartListAdapter(multiPartList ?: mutableListOf())
        multiPartListAdapter?.setHasStableIds(true)
        binding.rcvMultiPart.adapter = multiPartListAdapter
    }

    private fun getMultiPartList() {
        presenter.getMultiPartList(sessionId)
    }

    private fun initListeners() {
        binding.txtBuyBulk.setOnClickListener {
            val isPart = fromPage.equals(StringConstants.partPage)
            if(isPart){
                if(multiPartListAdapter?.isAllPartPurchase()==true){
                    showMessageDialog(R.string.all_part_purchased)
                    return@setOnClickListener
                }
            }else{
                if(multiPartListAdapter?.isAllMultiPartPurchase()==true){
                    showMessageDialog(R.string.all_multi_part_purchase)
                    return@setOnClickListener
                }
            }
            if (checkListValidation())
                proceedToBuyAndAddToCart()
        }

        binding.txtAddAllToCart.setOnClickListener {
            val isPart = fromPage.equals(StringConstants.partPage)
            if(isPart){
                if(multiPartListAdapter?.isAllPartPurchase()==true){
                    showMessageDialog(R.string.no_parts_to_add_to_cart)
                    return@setOnClickListener
                }
            }else{
                if(multiPartListAdapter?.isAllMultiPartPurchase()==true){
                    showMessageDialog(R.string.no_combo_to_add_to_cart)
                    return@setOnClickListener
                }
            }
            if (checkListValidation())
                proceedToAddToCart()
        }
    }

    private fun proceedToBuyAndAddToCart() {
        DialogUtils.showSessionMultiPartCheckoutDialog(
            requireContext(),
            { buyMultiPart() }, { proceedToAddToCart() }, false
        )
    }

    private fun checkListValidation(): Boolean {
        if (multiPartList?.filter { it.isChecked == true && it.isPartBought == false }
                .isNullOrEmpty()) {
            showMessageDialog(R.string.text_select_one_instrument)
            return false
        }
        return true
    }

    private fun proceedToAddToCart() {
        cartItem = mutableListOf()
        val selectedItems = multiPartList?.filter { it.isChecked == true }
        var type: String? = null
        selectedItems?.forEach {
            type = if (fromPage.equals(StringConstants.partPage)) {
                Constants.part
            } else {
                checkPurchaseAndCartType(it)
            }
            cartItem?.add(
                AddToCart(
                    orchestraId = sessionId,
                    type = Constants.session,
                    sessionType = type,
                    price = it.partPrice ?: 0.0,
                    musicianId = it.player?.id,
                    instrumentId = it.id,
                    identifier = it.partIdentifier
                )
            )
        }
        presenter.addToCart(AddToCartList(cartItem))
    }

    private fun checkPurchaseAndCartType(selectedItems: MultiPartListResponseItem): String {
        return if (selectedItems.isPremiumBought == false) {
            if (selectedItems.isPartBought == false) {
                Constants.comboType
            } else {
                Constants.premium
            }
        } else {
            Constants.record
        }
    }

    override fun addToCartSuccess(list: List<CartListResponse>) {
        AppInfoGlobal.cartInfo?.clear()
        AppInfoGlobal.cartInfo?.addAll(list)
        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionMultiPartAddToCartSuccessDialog(
            requireContext(),
            cartItem?.size ?: 0,
            hallSoundResponse,
            true
        ) {
            val bundle = bundleOf()
            bundle.putString(BundleConstant.fragmentType, Constants.multiPart)
            Router.navigateFragmentWithData(findNavController(), R.id.cartListFragment, bundle)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun purchaseSuccess(
        message: String?
    ) {
        multiPartList?.forEach { checkItem ->
            if (checkItem.isChecked == true) {
                checkItem.isPartBought = true
                AppInfoGlobal.cartInfo?.removeIf { it.orchestraId == sessionId && it.musicianId == checkItem.player?.id && (it.sessionType == ApiConstant.part || it.sessionType == Constants.comboType) }
            }
        }

        multiPartListAdapter?.notifyDataSetChanged()
        val clickedInstrument = (requireContext() as? LandingActivity)?.instrumentResponse
        if (purchasedItemsList.find { it.orchestraId == sessionId && it.musicianId == clickedInstrument?.musicianId && it.instrumentId == clickedInstrument?.instrument_id } != null) {
            if (hallSoundResponse?.isFromPremiumPage == true)
                updatePremiumDetail()
            else {
                if (hallSoundResponse?.isFromVideoPlayer == true)
                    updateSessionVideoPlayer()
                else
                    updateSessionDetail()
            }
        }

        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionMultiPartCheckoutSuccessDialog(
            hallSoundResponse,
            requireContext(),
            multiPartList?.filter { it.isChecked == true }?.size,
            true
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun buySuccessFromCart(cartItem: CartListResponse?) {
        multiPartList?.forEach { checkItem ->
            if (checkItem.player?.id == cartItem?.musicianId) {
                checkItem.isPartBought = true
            }
        }
        multiPartListAdapter?.notifyDataSetChanged()
        if (cartItem?.orchestraId == sessionId) {
            if (hallSoundResponse?.isFromPremiumPage == true)
                updatePremiumDetail()
            else {
                if (hallSoundResponse?.isFromVideoPlayer == true)
                    updateSessionVideoPlayer()
                else
                    updateSessionDetail()
            }
        }
    }

    private fun buyMultiPart() {
        val isPart = fromPage.equals(StringConstants.partPage)

        val selectedItems =
            multiPartList?.filter { it.isChecked == true && it.isPartBought == false }
        if ((selectedItems?.size ?: 0) < 1) {
            showMessage(R.string.text_select_only_one_instrument)
            return
        }

        selectedItems?.forEach { selectedItem ->
            val cartItem =
                AppInfoGlobal.cartInfo?.find {
                    it.orchestraId == sessionId && if (isPart) it.sessionType == ApiConstant.part else it.sessionType == Constants.comboType && it.musicianId == selectedItem.player?.id && it.instrumentId == selectedItem.id
                }
            if (cartItem != null)
                purchasedItemsList.add(
                    cartItem
                )
            else {
                purchasedItemsList.add(
                    CartListResponse(
                        orchestraId = sessionId,
                        type = ApiConstant.session,
                        sessionType = if (isPart) ApiConstant.part else Constants.comboType,
                        musicianId = selectedItem.player?.id,
                        instrumentId = selectedItem.id,
                        price = selectedItem.partPrice ?: 0.0,
                        identifier = selectedItem.partIdentifier
                    )
                )
            }
        }

        presenter.buyOrchestra(purchasedItemsList)
    }

    private fun updateSessionDetail() {
        val intent = Intent()
        intent.action = StringConstants.sessionBroadCastAction
        intent.putExtra(BundleConstant.sessionId, sessionId.toString())
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun updatePremiumDetail() {
        val intent = Intent()
        intent.action = StringConstants.sessionPremiumBroadCastAction
        intent.putExtra(BundleConstant.sessionId, sessionId.toString())
        intent.putExtra(
            BundleConstant.session,
            Constants.part
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun updateSessionVideoPlayer() {
        val intent = Intent()
        intent.action = StringConstants.sessionVideoPlayerBroadCastAction
        intent.putExtra(BundleConstant.sessionId, sessionId.toString())
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    override fun createPresenter() = BuyMultiPartPresenter()

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun success(multiPartListResponse: List<MultiPartListResponseItem>?) {
        if (multiPartListResponse != null) {
            hideErrorLayout()
            this.fromPage = hallSoundResponse?.fromPage
            this.multiPartList?.clear()
            multiPartListResponse.forEach {
                it.fromPage=fromPage
            }
            this.multiPartList?.addAll(
                multiPartListResponse
            )
            multiPartListAdapter?.notifyDataSetChanged()
        } else {
            showErrorLayout()
        }
    }

    private fun hideErrorLayout() {
        binding.rcvMultiPart.viewVisible()
        binding.lltNoData.rllNoData.viewGone()
    }

    private fun showErrorLayout() {
        binding.rcvMultiPart.viewGone()
        binding.lltNoData.rllNoData.viewVisible()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(purchaseCompleteBoardCast)
    }
}