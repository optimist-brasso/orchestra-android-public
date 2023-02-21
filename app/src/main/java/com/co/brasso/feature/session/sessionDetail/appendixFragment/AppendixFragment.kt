package com.co.brasso.feature.session.sessionDetail.appendixFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentAppendixBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.session.FavouriteSessionFragment
import com.co.brasso.feature.landing.listSong.listSongSession.ListSongSessionFragment
import com.co.brasso.feature.orchestra.conductorDetail.organizationchart.OrganizationChartActivity
import com.co.brasso.feature.session.sessionDetail.sessionDetail.SessionDetailFragment
import com.co.brasso.feature.session.sessionDetail.sessionDetailPremium.SessionDetailPremiumFragment
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

class AppendixFragment : BaseFragment<AppendixFragmentView, AppendixFragmentPresenter>(),
    AppendixFragmentView {
    private lateinit var binding: FragmentAppendixBinding
    private var instrumentResponse: InstrumentResponse? = null
    private var isFirst = true
    private var instrumentDetailResponse: InstrumentDetailResponse? = null
    val purchasedItemsList: MutableList<CartListResponse> = mutableListOf()

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setScreenPortrait()
        if (isFirst) {
            binding = FragmentAppendixBinding.inflate(layoutInflater)
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
            presenter.getDownloadedFilePath()
        }
        initToolbar()
    }

    private fun initToolbar() {
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
        initListeners()
        hideProgress()
        getInstrumentData()
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

        binding.txtOrganizationChart.setOnClickListener {
            openOrganizationChart()
        }

        binding.txtReturnToPart.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.txtAddToCart.setOnClickListener {
            if (checkLoginState() && checkPurchaseAndCartType() != Constants.record)
                showAddToCartAndBuyDialog(checkPurchaseAndCartType())
            else
                presenter.playVideo(context)
        }

        binding.imgDownload.clicks().throttle()?.subscribe {
            presenter.downloadVideo(context)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.txtBuyAppendixVideo.setOnClickListener {
            if (checkLoginState() && checkPurchaseAndCartType() != Constants.record)
                showAddToCartAndBuyDialog(checkPurchaseAndCartType())
            else
                presenter.playVideo(context)
        }

        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(instrumentDetailResponse?.hallSoundDetail?.title, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.imgFavourite.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(
                    requireContext(), getString(R.string.please_login), { navigateToLogin() }, {},
                    isCancelable = false,
                    showNegativeBtn = true,
                    getString(R.string.login)
                )
            } else {
                if (isNetworkAvailable())
                    favoriteChange()
                else
                    showMessageDialog(R.string.error_no_internet_connection)
            }
        }
    }

    private fun favoriteChange() {
        SessionDetailPremiumFragment.favStatusChanges = true
        SessionDetailPremiumFragment.favStatus = !(instrumentDetailResponse?.isFavourite ?: false)
        SessionDetailFragment.favStatusChanges = true
        SessionDetailFragment.favStatus = !(instrumentDetailResponse?.isFavourite ?: false)
        if (instrumentDetailResponse?.isFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_session
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
                    requireContext(),
                    R.drawable.ic_favourite_selected
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

    private fun showAddToCartAndBuyDialog(type: String) {
        DialogUtils.showSessionCheckoutDialog(
            requireContext(),
            instrumentDetailResponse,
            { proceedToBuy(type) },
            { proceedToAddToCart(type) },
            isCancelable = false, type
        )
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

    private fun proceedToBuy(type: String?) {
        val cartItem =
            AppInfoGlobal.cartInfo?.find {
                it.orchestraId == instrumentResponse?.sessionId && (it.sessionType == type || it.sessionType == Constants.part) && it.musicianId == instrumentResponse?.musicianId
                        && it.instrumentId == instrumentResponse?.instrument_id
            }
        if (cartItem != null)
            purchasedItemsList.add(
                cartItem
            )
        else
            purchasedItemsList.add(
                CartListResponse(
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

    override fun addToCartSuccess(cartListResponse: List<CartListResponse>?) {
        AppInfoGlobal.cartInfo = mutableListOf()
        AppInfoGlobal.cartInfo?.addAll(cartListResponse ?: listOf())
        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionAddToCartSuccessDialog(
            requireContext(), instrumentDetailResponse, false, {
                val bundle = bundleOf()
                bundle.putString(BundleConstant.fragmentType, Constants.appendix)
                Router.navigateFragmentWithData(findNavController(), R.id.cartListFragment, bundle)
            }, checkPurchaseAndCartType()
        )
    }

    override fun purchaseSuccess(
        message: String?
    ) {
        val purchaseType = checkPurchaseAndCartType()
        DialogUtils.showSessionCheckoutSuccessDialog(
            requireContext(),
            instrumentDetailResponse, false, purchaseType
        )
        AppInfoGlobal.cartInfo?.removeIf {
            it.orchestraId == instrumentResponse?.sessionId
                    && it.musicianId == instrumentResponse?.musicianId && (it.sessionType == checkPurchaseAndCartType() || it.sessionType == Constants.part)
                    && it.instrumentId == instrumentResponse?.instrument_id
        }
        if (purchasedItemsList.find { it.sessionType == Constants.premium } != null)
            instrumentDetailResponse?.isPremiumBought = true
        else {
            instrumentDetailResponse?.isPremiumBought = true
            instrumentDetailResponse?.isPartBought = true
        }
        updatePremiumDetail(purchaseType)
//        checkIsBrought(instrumentDetailResponse?.isPremiumBought)
        checkPurchaseCondition()
        (requireContext() as? LandingActivity)?.showCartCount()
        getInstrumentData()
    }

    private fun updatePremiumDetail(purchaseType: String?) {
        val intent = Intent()
        intent.action = StringConstants.sessionPremiumBroadCastAction
        intent.putExtra(BundleConstant.sessionId, instrumentResponse?.sessionId.toString())
        intent.putExtra(
            BundleConstant.session,
            purchaseType
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
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
            purchaseCompleteBoardCast,
            IntentFilter(StringConstants.sessionAppendixVideoBroadCastAction)
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

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun hidePlayButton() {
        binding.imvPlay.viewGone()
    }

    override fun showPlayButton() {
        binding.imvPlay.viewVisible()
    }

    override fun getInstrumentSuccess(instrumentDetailResponse: InstrumentDetailResponse) {
       this.instrumentDetailResponse = instrumentDetailResponse
        setData()
    }

    private fun setData() {
        binding.scvBody.viewVisible()
        binding.divEnd.viewVisible()
        binding.lltBuyPart.viewVisible()
        val hallSoundDetail = instrumentDetailResponse?.hallSoundDetail

        Picasso.get().load(instrumentDetailResponse?.premiumThumbnail)
            .resize(binding.imgSessionThumbnail.width, binding.imgSessionThumbnail.height)
            .placeholder(R.drawable.ic_thumbnail)
            .into(binding.imgSessionThumbnail)

//        if (instrumentDetailResponse?.playerDetail?.name.isNullOrEmpty()) {
//            binding.txtPlayerName.viewGone()
//        } else {
//            binding.txtPlayerName.viewVisible()
//            binding.txtPlayerName.text = instrumentDetailResponse?.playerDetail?.name
//        }

        setPremiumDetail()

        if (hallSoundDetail?.title.isNullOrEmpty()) {
            binding.txtTitleEng.viewGone()
        } else {
            binding.txtTitleEng.viewVisible()
            binding.txtTitleEng.text = hallSoundDetail?.title
        }

        if (hallSoundDetail?.band.isNullOrEmpty()) {
            binding.lltOrchestraName.viewGone()
        } else {
            binding.lltOrchestraName.viewVisible()
            binding.txvOrchestraName.text = hallSoundDetail?.band
        }

        if (hallSoundDetail?.titleJp.isNullOrEmpty()) {
            binding.txtTitleJpn.viewGone()
        } else {
            binding.txtTitleJpn.viewVisible()
            binding.txtTitleJpn.text = hallSoundDetail?.titleJp
        }

        if (hallSoundDetail?.composer.isNullOrEmpty()) {
            binding.rltComposer.viewGone()
        } else {
            binding.rltComposer.viewVisible()
            val splitText = hallSoundDetail?.composer?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvComposerJpn.viewVisible()
                binding.txvComposerEng.text = splitText[0].trim()
                binding.txvComposerJpn.text = splitText[1].trim()
            } else {
                binding.txvComposerJpn.viewGone()
                binding.txvComposerEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.organization.isNullOrEmpty()) {
            binding.lltOrganization.viewGone()
        } else {
            binding.lltOrganization.viewVisible()
            binding.txvOrganization.text = hallSoundDetail?.organization
        }

        if (hallSoundDetail?.releaseDate.isNullOrEmpty()) {
            binding.lltDate.viewGone()
        } else {
            binding.lltDate.viewVisible()
            binding.txvDate.text = hallSoundDetail?.releaseDate
        }

        if (hallSoundDetail?.duration == null) {
            binding.lltLap.viewGone()
        } else {
            binding.lltLap.viewVisible()
            binding.txvLap.text = DateUtils.convertIntToRecordTime(hallSoundDetail.duration)
        }

        if (hallSoundDetail?.conductor.isNullOrEmpty()) {
            binding.rltConductor.viewGone()
        } else {
            binding.rltConductor.viewVisible()
            val splitText = hallSoundDetail?.conductor?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvConductorJpn.viewVisible()
                binding.txvConductorEng.text = splitText[0].trim()
                binding.txvConductorJpn.text = splitText[1].trim()
            } else {
                binding.txvConductorJpn.viewGone()
                binding.txvConductorEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.venueTitle.isNullOrEmpty()) {
            binding.rltVenueName.viewGone()
        } else {
            binding.rltVenueName.viewVisible()
            val splitText = hallSoundDetail?.venueTitle?.split(",")

            if (splitText?.size!! > 1) {
                binding.txvVenueNameJpn.viewVisible()
                binding.txvVenueNameEng.text = splitText[0].trim()
                binding.txvVenueNameJpn.text = splitText[1].trim()
            } else {
                binding.txvVenueNameJpn.viewGone()
                binding.txvVenueNameEng.text = splitText[0].trim()
            }
        }

        if (hallSoundDetail?.jasracLicenseNumber.isNullOrEmpty()) {
            binding.lltLicence.viewGone()
        } else {
            binding.lltLicence.viewVisible()
            binding.txvLicence.text = hallSoundDetail?.jasracLicenseNumber
        }

        if (instrumentDetailResponse?.isFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_selected
                )
            )
        } else {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_session
                )
            )
        }

        if (hallSoundDetail?.businessType.isNullOrEmpty()) {
            binding.txvBusinessType.viewGone()
        } else {
            binding.txvBusinessType.viewVisible()
            binding.txvBusinessType.text = hallSoundDetail?.businessType
        }

        if (instrumentDetailResponse?.name.isNullOrEmpty()) {
            binding.txtInstrument.viewGone()
        } else {
            binding.txtInstrument.viewVisible()
            binding.txtInstrument.text = instrumentDetailResponse?.name
        }

//        if (instrumentDetailResponse?.musician.isNullOrEmpty()) {
//            binding.txtPlayerName.viewGone()
//        } else {
//            binding.txtPlayerName.viewVisible()
//            binding.txtPlayerName.text = instrumentDetailResponse?.musician
//        }

        if (instrumentDetailResponse?.premiumFile.isNullOrEmpty()) {
            binding.imvPlay.viewGone()
            binding.txtPlayVideo.viewGone()
        } else {
            binding.imvPlay.viewVisible()
            binding.txtPlayVideo.viewVisible()
        }

        if (instrumentDetailResponse?.hallSoundDetail?.organizationDiagram.isNullOrEmpty()) {
            binding.txtOrganizationChart.viewGone()
        } else {
            binding.txtOrganizationChart.viewVisible()
        }

//        if (instrumentDetailResponse?.isPartBought == true && instrumentDetailResponse?.isPremiumBought == true) {
//            binding.txtNotice.viewGone()
//        } else if (instrumentDetailResponse?.isPartBought == true && instrumentDetailResponse?.isPremiumBought == false) {
//            binding.txtNotice.viewGone()
//        } else if (instrumentDetailResponse?.isPartBought == false && instrumentDetailResponse?.isPremiumBought == true) {
//            binding.txtNotice.viewGone()
//        } else {
//            binding.txtNotice.viewVisible()
//        }

//        checkIsBrought(instrumentDetailResponse?.isPremiumBought)

        checkPurchaseCondition()
    }

    private fun setPremiumDetail() {
        if (instrumentDetailResponse?.premiumVideoDescription.isNullOrEmpty()) {
            binding.txtPremiumDetail.viewGone()
        } else {
            binding.txtPremiumDetail.viewVisible()
            if (instrumentDetailResponse?.isPremiumBought == false)
                binding.txtPremiumDetail.maxLines = 3
            else
                binding.txtPremiumDetail.maxLines = Integer.MAX_VALUE
            binding.txtPremiumDetail.text = instrumentDetailResponse?.premiumVideoDescription
        }
    }

    private fun showDownload() {
        //  binding.lltAppendixBody.viewGone()
//        binding.lltPremiumBody.viewVisible()
        binding.txtAddToCart.viewGone()
        binding.txtBuyAppendixVideo.viewGone()
        // binding.txtNotice.viewGone()
    }

    private fun hideDownload() {
        //   binding.lltAppendixBody.viewVisible()
//        binding.lltPremiumBody.viewGone()
        binding.txtAddToCart.viewVisible()
        binding.txtBuyAppendixVideo.viewVisible()
        // binding.txtNotice.viewVisible()
    }

    private fun checkPurchaseCondition() {
        when (checkPurchaseAndCartType()) {
            Constants.premium -> {
                setPremiumPrice()
                binding.txtPlayVideo.text = getString(R.string.playSampleVideo)
            }
            Constants.comboType -> {
                binding.txtBuyAppendixVideo.text =
                    getString(R.string.purchasePartAndPremium)
                binding.txtPlayVideo.text = getString(R.string.playSampleVideo)
            }
            else -> {
                binding.txtBuyAppendixVideo.viewGone()

                binding.txtAddToCart.viewGone()

                binding.txtBuyAppendixVideo.text =
                    getString(R.string.text_record)
                binding.txtPlayVideo.text = getString(R.string.text_play_video)
            }
        }
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

    private fun setPremiumPrice() {
//        if (instrumentDetailResponse?.premiumVideoPrice == null) {
//            binding.txtBuyAppendixVideo.text =
//                requireContext().getString(R.string.textPremium) + " " + requireContext().getString(
//                    R.string.zeroYen
//                ) + " " + getString(R.string.points) + " " + requireContext().getString(
//                    R.string.toBuy
//                )
//        } else {
        binding.txtBuyAppendixVideo.text =
            requireContext().getString(R.string.textPremium)
        //                + "%,d".format(
//                    instrumentDetailResponse?.premiumVideoPrice?.toInt()
//                ) + " " + getString(R.string.points) + " " + requireContext().getString(
//                    R.string.toBuy
//                )
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
                updatePremiumDetail(type)
//                checkIsBrought(instrumentDetailResponse?.isPremiumBought)
                checkPurchaseCondition()
                setPremiumDetail()
            }
        }
    }

    override fun createPresenter() = AppendixFragmentPresenter()

    private fun getIsFromFav() = arguments?.getBoolean(StringConstants.isFromFav, false)

    private fun getSelectedPosition() = arguments?.getInt(StringConstants.selectedPos)

    override fun downloadComplete() {
        hideProgress()
        hideDownloadButton()
    }

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

    private fun navigateToPlayer() {
        if (instrumentDetailResponse?.premiumFile.isNullOrEmpty()) {
            showMessage(requireContext().getString(R.string.empty_video))
        } else {
            instrumentDetailResponse?.type = checkPurchaseAndCartType()
            instrumentDetailResponse?.hallSoundDetail?.sessionId = instrumentResponse?.sessionId
            instrumentDetailResponse?.hallSoundDetail?.musicianId = instrumentResponse?.musicianId
            instrumentDetailResponse?.isFromAppendixVideo = true
            val bundle = bundleOf(Constants.videoUrl to instrumentDetailResponse)
            Router.navigateFragmentWithData(
                findNavController(),
                R.id.sessionVideoPlayerFragment,
                bundle
            )
        }
    }

    override fun navigateToPlayer(
        orchestra: HallSoundResponse?,
        filePath: String?,
        fileName: String?
    ) {
        orchestra?.vrFilePath = filePath
        instrumentDetailResponse?.type = checkPurchaseAndCartType()
        instrumentDetailResponse?.hallSoundDetail?.sessionId = instrumentResponse?.sessionId
        instrumentDetailResponse?.hallSoundDetail?.musicianId = instrumentResponse?.musicianId
        instrumentDetailResponse?.isFromAppendixVideo = true
        instrumentDetailResponse?.isDownloaded=true
        instrumentDetailResponse?.premiumFile=filePath
        val bundle = bundleOf(Constants.videoUrl to instrumentDetailResponse)
        Router.navigateFragmentWithData(
            findNavController(),
            R.id.sessionVideoPlayerFragment,
            bundle
        )
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