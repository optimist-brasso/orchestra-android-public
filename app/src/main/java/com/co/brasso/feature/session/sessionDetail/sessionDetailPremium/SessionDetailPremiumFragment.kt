package com.co.brasso.feature.session.sessionDetail.sessionDetailPremium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentSessionDetailPremiumBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.session.FavouriteSessionFragment
import com.co.brasso.feature.landing.listSong.listSongSession.ListSongSessionFragment
import com.co.brasso.feature.orchestra.conductorDetail.organizationchart.OrganizationChartActivity
import com.co.brasso.feature.session.sessionDetail.sessionDetail.SessionDetailFragment
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.request.cart.AddToCart
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.VideoResolution
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso

class SessionDetailPremiumFragment :
    BaseFragment<SessionDetailPremiumView, SessionDetailPremiumPresenter>(),
    SessionDetailPremiumView {
    private lateinit var binding: FragmentSessionDetailPremiumBinding
    private var instrumentResponse: InstrumentResponse? = null
    private var instrumentDetailResponse: InstrumentDetailResponse? = null
    private var isFirst = true
    val purchasedItemsList: MutableList<CartListResponse> = mutableListOf()

    companion object {
        var favStatusChanges: Boolean = false
        var favStatus: Boolean = false
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentSessionDetailPremiumBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
            isFirst = false
        } else {
//            hideProgress()
            presenter.getDownloadedFilePath()
        }
        initToolbar()
        checkFavUnFav()
    }

    private fun checkFavUnFav() {
        if (favStatusChanges) {
            changeFavUnFav(favStatus)
            favStatusChanges = false
        }
    }

    private fun changeFavUnFav(favStatus: Boolean) {
        if (favStatus) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_selected
                )
            )
            if (getIsFromFav() == true) {
                FavouriteSessionFragment.selectedPos = null
            } else {
                ListSongSessionFragment.selectedPos = getSelectedPosition()
            }
        } else {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_session
                )
            )

            if (getIsFromFav() == true) {
                FavouriteSessionFragment.selectedPos = getSelectedPosition()
            } else {
                ListSongSessionFragment.selectedPos = getSelectedPosition()
            }
        }
        instrumentDetailResponse?.isFavourite = favStatus
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun setUp() {
        presenter.initBoardCastListener(requireContext())
        presenter.initVideoDownloader(requireContext())
        registerBroadCast()
        getIntentData()
        getInstrumentData()
        initListeners()
        hideProgress()
    }

    private fun getIntentData() {
        instrumentResponse = arguments?.getSerializable(Constants.session) as? InstrumentResponse
    }

    private fun initListeners() {
        binding.swpPlayer.setOnRefreshListener {
            binding.swpPlayer.isRefreshing = false
            getInstrumentData()
        }

        binding.imvPlay.setOnClickListener {
            presenter.playVideo(requireContext())
        }

        binding.txtBuyPremiumVideo.setOnClickListener {
            if (checkLoginState() && checkPurchaseAndCartType() != Constants.record) showAddToCartAndBuyDialog(
                checkPurchaseAndCartType()
            )
            else {
                presenter.playVideo(requireContext())
            }
        }

        binding.txtBuyMultiPartAtOnce.setOnClickListener {
            navigateToBuyMultiPart()
        }

        binding.txtAppendixVideoSample.setOnClickListener {
            navigateToAppendix()
        }

        binding.imgDownload.clicks().throttle()?.subscribe {
            presenter.downloadVideo(requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.txtBtnConductor.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to instrumentResponse?.sessionId)
            NavHostFragment.findNavController(this).navigate(R.id.conductorDetailFragment, bundle)
        }

        binding.txtBtnHallSound.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to instrumentResponse?.sessionId)
            NavHostFragment.findNavController(this).navigate(R.id.hallSoundDetailFragment, bundle)
        }

        binding.txtBtnPlayer.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to instrumentResponse?.sessionId)
            NavHostFragment.findNavController(this).navigate(R.id.playerDetailFragment, bundle)
        }

        binding.txtOrganizationChart.setOnClickListener {
            openOrganizationChart()
        }

        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(instrumentDetailResponse?.hallSoundDetail?.title, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.imgFavourite.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(
                    requireContext(),
                    getString(R.string.please_login),
                    { navigateToLogin() },
                    {},
                    isCancelable = false,
                    showNegativeBtn = true,
                    getString(R.string.login)
                )
            } else {
                if (isNetworkAvailable()) favoriteChange()
                else showMessageDialog(R.string.error_no_internet_connection)
            }
        }
    }

    private fun favoriteChange() {
        SessionDetailFragment.favStatusChanges = true
        SessionDetailFragment.favStatus = !(instrumentDetailResponse?.isFavourite ?: false)
        if (instrumentDetailResponse?.isFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_session
                )
            )

            if (getIsFromFav() == true) {
                FavouriteSessionFragment.selectedPos = getSelectedPosition()
            } else {
                ListSongSessionFragment.selectedPos = getSelectedPosition()
            }
        } else {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_selected
                )
            )

            if (getIsFromFav() == true) {
                FavouriteSessionFragment.selectedPos = null
            } else {
                ListSongSessionFragment.selectedPos = getSelectedPosition()
            }
        }
        instrumentDetailResponse?.isFavourite = !(instrumentDetailResponse?.isFavourite ?: false)
        presenter.addFavourite(
            FavouriteSessionEntity(
                instrumentResponse?.sessionId.toString(),
                instrumentResponse?.instrument_id.toString(),
                instrumentResponse?.musicianId.toString()
            )
        )
    }

    private fun navigateToAppendix() {
        instrumentResponse?.fromPage = StringConstants.premiumPage
        val bundle = bundleOf(Constants.session to instrumentResponse)
        val navController = NavHostFragment.findNavController(this)
        bundle.putInt(StringConstants.selectedPos, getSelectedPosition() ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, getIsFromFav() ?: false)
        Router.navigateFragmentWithData(navController, R.id.appendixFragment, bundle)
    }

    private fun openOrganizationChart() {
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.url,
            this.instrumentDetailResponse?.hallSoundDetail?.organizationDiagram,
            OrganizationChartActivity::class
        )
    }

    private fun registerBroadCast() {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            purchaseCompleteBoardCast, IntentFilter(StringConstants.sessionPremiumBroadCastAction)
        )
    }

    private fun getInstrumentData() {
        val videoSupport = if (VideoResolution.is4kSupported() == true) {
            ApiConstant.fourKSupport
        } else {
            ApiConstant.hdSupport
        }
        presenter.getInstrumentDetail(
            instrumentResponse?.instrument_id,
            instrumentResponse?.sessionId,
            instrumentResponse?.musicianId,
            videoSupport
        )
    }

    private fun navigateToBuyMultiPart() {
        if (checkLoginState()) {
            val hallSoundDetail = presenter.orchestra
            hallSoundDetail?.sessionId = instrumentResponse?.sessionId
            hallSoundDetail?.isFromPremiumPage = true
            hallSoundDetail?.fromPage = StringConstants.premiumPage
            val bundle = bundleOf(Constants.session to hallSoundDetail)
            val navController = NavHostFragment.findNavController(this)
            Router.navigateFragmentWithData(navController, R.id.buyMultiPartFragment, bundle)
        }
    }

    private fun showAddToCartAndBuyDialog(type: String) {
        DialogUtils.showSessionCheckoutDialog(
            requireContext(),
            instrumentDetailResponse,
            { proceedToBuy(type) },
            { proceedToAddToCart(type) },
            isCancelable = false,
            type
        )
    }

    private fun proceedToBuy(type: String?) {
        val cartItem = AppInfoGlobal.cartInfo?.find {
            it.orchestraId == instrumentResponse?.sessionId && it.type == ApiConstant.session && it.sessionType == type && it.musicianId == instrumentResponse?.musicianId && it.instrumentId == instrumentResponse?.instrument_id
        }
        val cartPartItem = AppInfoGlobal.cartInfo?.find {
            it.orchestraId == instrumentResponse?.sessionId && it.type == ApiConstant.session && it.sessionType == Constants.part && it.musicianId == instrumentResponse?.musicianId && it.instrumentId == instrumentResponse?.instrument_id
        }
        if (cartItem != null) purchasedItemsList.add(
            cartItem
        )
        else purchasedItemsList.add(
            CartListResponse(
                id = cartPartItem?.id,
                orchestraId = instrumentResponse?.sessionId,
                type = ApiConstant.session,
                sessionType = type,
                musicianId = instrumentResponse?.musicianId,
                instrumentId = instrumentResponse?.instrument_id,
                price = getPrice(),
                identifier = getIdentifier(),
                releaseDate = instrumentDetailResponse?.hallSoundDetail?.releaseDate
            )
        )

        presenter.buyOrchestra(purchasedItemsList)
    }

    private fun checkPurchaseAndCartType(): String {
        return if (instrumentDetailResponse?.isPremiumBought == false) {
            if (instrumentDetailResponse?.isPartBought == false) {
                Constants.comboType
            } else {
                Constants.premium
            }
        } else {
            Constants.record
        }
    }

    private fun getPrice(): Double {
        return if (instrumentDetailResponse?.isPremiumBought == false) {
            if (instrumentDetailResponse?.isPartBought == false) {
                instrumentDetailResponse?.comboPrice ?: 0.0
            } else {
                instrumentDetailResponse?.premiumVideoPrice ?: 0.0
            }
        } else {
            0.0
        }
    }

    private fun getIdentifier(): String? {
        return if (instrumentDetailResponse?.isPremiumBought == false) {
            if (instrumentDetailResponse?.isPartBought == false) {
                instrumentDetailResponse?.comboVideoIdentifier
            } else {
                instrumentDetailResponse?.premiumVideoIdentifier
            }
        } else {
            instrumentDetailResponse?.partIdentifier
        }
    }

    private fun proceedToAddToCart(type: String) {
        val cartItems: MutableList<AddToCart> = mutableListOf()
        cartItems.add(
            AddToCart(
                orchestraId = instrumentResponse?.sessionId,
                type = ApiConstant.session,
                sessionType = type,
                price = getPrice(),
                musicianId = instrumentResponse?.musicianId,
                instrumentId = instrumentResponse?.instrument_id,
                identifier = getIdentifier(),
                releaseDate = instrumentDetailResponse?.hallSoundDetail?.releaseDate
            )
        )
        presenter.addToCart(
            AddToCartList(cartItems)
        )
    }

    override fun createPresenter() = SessionDetailPremiumPresenter()

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun success(instrumentDetailResponse: InstrumentDetailResponse) {
        this.instrumentDetailResponse = instrumentDetailResponse
        setData(instrumentDetailResponse)
    }

    override fun addToCartSuccess(cartListResponse: List<CartListResponse>?) {
        AppInfoGlobal.cartInfo = mutableListOf()
        AppInfoGlobal.cartInfo?.addAll(cartListResponse ?: listOf())
        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionAddToCartSuccessDialog(
            requireContext(), instrumentDetailResponse, false, {
                val bundle = bundleOf()
                bundle.putString(BundleConstant.fragmentType, Constants.premium)
                Router.navigateFragmentWithData(findNavController(), R.id.cartListFragment, bundle)
            }, checkPurchaseAndCartType()
        )
    }

    override fun purchaseSuccess(
        message: String?
    ) {
        DialogUtils.showSessionCheckoutSuccessDialog(
            requireContext(), instrumentDetailResponse, false, checkPurchaseAndCartType()
        )
        AppInfoGlobal.cartInfo?.removeIf { it.orchestraId == instrumentResponse?.sessionId && it.musicianId == instrumentResponse?.musicianId && (it.sessionType == checkPurchaseAndCartType() || it.sessionType == Constants.part) }
        if (purchasedItemsList.find { it.sessionType == Constants.premium } != null) instrumentDetailResponse?.isPremiumBought =
            true
        else if (purchasedItemsList.find { it.sessionType == Constants.part } != null) instrumentDetailResponse?.isPartBought =
            true
        else {
            instrumentDetailResponse?.isPremiumBought = true
            instrumentDetailResponse?.isPartBought = true
        }
        updateSessionDetail(Constants.comboType)
        checkPurchaseCondition()
        (requireContext() as? LandingActivity)?.showCartCount()
        getInstrumentData()
    }

    private fun updateSessionDetail(type: String?) {
        val intent = Intent()
        intent.action = StringConstants.sessionBroadCastAction
        intent.putExtra(BundleConstant.sessionId, instrumentResponse?.sessionId.toString())
        intent.putExtra(BundleConstant.session, type)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun setData(instrumentDetailResponse: InstrumentDetailResponse?) {
        binding.scvBody.viewVisible()
        binding.divEnd.viewVisible()
        binding.lltBuyPart.viewVisible()

        val hallSoundDetail = instrumentDetailResponse?.hallSoundDetail

        Picasso.get().load(instrumentDetailResponse?.premiumVrThumbnail)
//                .resize(binding.imgSessionThumbnail.width, binding.imgSessionThumbnail.height)
            .placeholder(R.drawable.ic_thumbnail).into(binding.imgSessionThumbnail)


        if (instrumentDetailResponse?.playerDetail?.name.isNullOrEmpty()) {
            binding.txtPlayerName.viewGone()
        } else {
            binding.txtPlayerName.text = instrumentDetailResponse?.playerDetail?.name
        }

        if (instrumentDetailResponse?.premiumVideoDescription.isNullOrEmpty()) {
            binding.txtPremiumDetail.viewGone()
        } else {
            binding.txtPremiumDetail.text = instrumentDetailResponse?.premiumContents
        }

        if (hallSoundDetail?.title.isNullOrEmpty()) {
            binding.txtTitleEng.viewGone()
        } else {
            binding.txtTitleEng.text = hallSoundDetail?.title
        }

        if (hallSoundDetail?.band.isNullOrEmpty()) {
            binding.lltOrchestraName.viewGone()
        } else {
            binding.txvOrchestraName.text = hallSoundDetail?.band
        }

        if (hallSoundDetail?.titleJp.isNullOrEmpty()) {
            binding.txtTitleJpn.viewGone()
        } else {
            binding.txtTitleJpn.text = hallSoundDetail?.titleJp
        }

        if (hallSoundDetail?.composer.isNullOrEmpty()) {
            binding.rltComposer.viewGone()
        } else {
            val splitText = hallSoundDetail?.composer?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvComposerJpn.viewVisible()
                binding.txvComposerEng.text = splitText[0].trim()
                binding.txvComposerJpn.text = splitText[1].trim()
            } else {
                binding.txvComposerEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.organization.isNullOrEmpty()) {
            binding.lltOrganization.viewGone()
        } else {
            binding.txvOrganization.text = hallSoundDetail?.organization
        }

        if (hallSoundDetail?.releaseDate.isNullOrEmpty()) {
            binding.lltDate.viewGone()
        } else {
            binding.txvDate.text = hallSoundDetail?.releaseDate
        }

        if (hallSoundDetail?.duration == null) {
            binding.lltLap.viewGone()
        } else {
            binding.txvLap.text = DateUtils.convertIntToRecordTime(hallSoundDetail.duration)
        }

        if (hallSoundDetail?.conductor.isNullOrEmpty()) {
            binding.rltConductor.viewGone()
        } else {
            val splitText = hallSoundDetail?.conductor?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvConductorJpn.viewVisible()
                binding.txvConductorEng.text = splitText[0].trim()
                binding.txvConductorJpn.text = splitText[1].trim()
            } else {
                binding.txvConductorEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.venueTitle.isNullOrEmpty()) {
            binding.rltVenueName.viewGone()
        } else {
            val splitText = hallSoundDetail?.venueTitle?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvVenueNameJpn.viewVisible()
                binding.txvVenueNameEng.text = splitText[0].trim()
                binding.txvVenueNameJpn.text = splitText[1].trim()
            } else {
                binding.txvVenueNameEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.jasracLicenseNumber.isNullOrEmpty()) {
            binding.lltLicence.viewGone()
        } else {
            binding.txvLicence.text = hallSoundDetail?.jasracLicenseNumber
        }

        if (instrumentDetailResponse?.isFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_selected
                )
            )
        } else {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_session
                )
            )
        }

        if (hallSoundDetail?.businessType.isNullOrEmpty()) {
            binding.txvBusinessType.viewGone()
        } else {
            binding.txvBusinessType.text = hallSoundDetail?.businessType
        }

        if (instrumentDetailResponse?.name.isNullOrEmpty()) {
            binding.txtInstrument.viewGone()
        } else {
            binding.txtInstrument.text = instrumentDetailResponse?.name
        }

        if (instrumentDetailResponse?.musician.isNullOrEmpty()) {
            binding.txtPlayerName.viewGone()
        } else {
            binding.txtPlayerName.text = instrumentDetailResponse?.musician
        }

        if (instrumentDetailResponse?.hallSoundDetail?.organizationDiagram.isNullOrEmpty()) {
            binding.txtOrganizationChart.viewGone()
        } else {
            binding.txtOrganizationChart.viewVisible()
        }

        if (instrumentDetailResponse?.iosPremiumVrFile.isNullOrEmpty()) binding.txtPlayVideo.viewGone()
        else binding.txtPlayVideo.viewVisible()
        checkPurchaseCondition()
    }

    private fun checkPurchaseCondition() {
        when (checkPurchaseAndCartType()) {
            Constants.premium -> {
                setPremiumPrice()
                binding.txtPlayVideo.text = getString(R.string.playSampleVideo)
            }
            Constants.comboType -> {
                binding.txtBuyPremiumVideo.text = getString(R.string.purchasePartAndPremium)
                binding.txtPlayVideo.text = getString(R.string.playSampleVideo)
            }
            else -> {
                binding.txtBuyPremiumVideo.viewGone()

                binding.txtBuyMultiPartAtOnce.viewGone()

                binding.txtPremiumNotice.viewGone()

                binding.txtBuyPremiumVideo.text = getString(R.string.text_record)
                binding.txtPlayVideo.text = getString(R.string.text_play_video)
            }
        }
    }

    private fun setPremiumPrice() {
//        if (instrumentDetailResponse?.premiumVideoPrice == null) {
//            binding.txtBuyPremiumVideo.text =
//                requireContext().getString(R.string.textPremium) + " " + requireContext().getString(
//                    R.string.zeroYen
//                ) + " " + getString(R.string.points) + " " + requireContext().getString(
//                    R.string.toBuy
//                )
//        } else {
        binding.txtPremiumNotice.viewGone()

        binding.txtBuyPremiumVideo.text = requireContext().getString(R.string.textPremium)
//        }
    }

    private var purchaseCompleteBoardCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            val sessionId = intent?.getStringExtra(BundleConstant.sessionId)
            val type = intent?.getStringExtra(BundleConstant.session)
            if (sessionId == instrumentResponse?.sessionId.toString()) {
                instrumentDetailResponse?.isPartBought = true
                if (type == Constants.comboType || type == Constants.premium) {
                    instrumentDetailResponse?.isPremiumBought = true
                }
                checkPurchaseCondition()
                updateSessionDetail(type)
            }
        }
    }

    private fun getIsFromFav() = arguments?.getBoolean(StringConstants.isFromFav, false)

    private fun getSelectedPosition() = arguments?.getInt(StringConstants.selectedPos)

    override fun downloadComplete() {
        hideProgress()
        hideDownloadButton()
    }


    override fun hidePlayButton() {
        binding.imvPlay.viewGone()
    }

    override fun showPlayButton() {
        binding.imvPlay.viewVisible()
    }

//    override fun showDownloadButton() {
//        binding.imgDownload.viewVisible()
//        binding.imgDownload.setImageResource(R.drawable.ic_download)
//    }

    override fun showStopDownloadButton() {
        binding.imgDownload.viewVisible()
        binding.imgDownload.setImageResource(R.drawable.ic_download_stop)
    }

    override fun setProgress(progress: Int) {
        binding.progressBar.progress = progress
    }

    override fun showProgress() {
        binding.txvDownloading.viewVisible()
        binding.progressBar.viewVisible()
    }

    override fun hideProgress() {
        binding.txvDownloading.viewGone()
        binding.progressBar.viewGone()
    }

    override fun hideDownloadButton() {
        binding.imgDownload.viewGone()
    }

    override fun navigateToPlayer(
        orchestra: HallSoundResponse?, filePath: String?, fileName: String?
    ) {
        orchestra?.vrFile = filePath
        orchestra?.isBoughtForUnity = instrumentDetailResponse?.isPremiumBought
        orchestra?.isPremium = true
        orchestra?.instrumentName = instrumentDetailResponse?.name
        orchestra?.businessType = instrumentDetailResponse?.hallSoundDetail?.businessType
        if (instrumentDetailResponse?.isPremiumBought == false) {
            orchestra?.playDuration = 30
        } else {
            orchestra?.playDuration = 0
        }
        (requireContext() as? LandingActivity)?.openVrActivity(orchestra)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(purchaseCompleteBoardCast)
        presenter.onDestroy(requireContext())
        super.onDestroy()
    }

    override fun noInternet(errorNoInternetConnection: Int) {
        setScreenPortrait()
        binding.scvBody.viewGone()
        binding.divEnd.viewGone()
        binding.lltBuyPart.viewGone()
        DialogUtils.showAlertDialog(requireContext(), getString(errorNoInternetConnection), {}, {})
    }
}