package com.co.brasso.feature.session.sessionDetail.sessionDetail

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
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentSessionDetailBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.session.FavouriteSessionFragment
import com.co.brasso.feature.landing.listSong.listSongSession.ListSongSessionFragment
import com.co.brasso.feature.orchestra.conductorDetail.organizationchart.OrganizationChartActivity
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

class SessionDetailFragment : BaseFragment<SessionDetailView, SessionDetailPresenter>(),
    SessionDetailView {
    private lateinit var binding: FragmentSessionDetailBinding
    private var instrumentResponse: InstrumentResponse? = null
    var instrumentDetailResponse: InstrumentDetailResponse? = null
    private var isFirst = true
    private var isPartPurchaseUpdated: Boolean? = false

    companion object {
        var favStatusChanges: Boolean = false
        var favStatus: Boolean = false
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentSessionDetailBinding.inflate(layoutInflater)
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
          //  hideProgress()
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

    override fun onResume() {
        if (isPartPurchaseUpdated == true) {
            getInstrumentData()
            isPartPurchaseUpdated = false
        }
        super.onResume()
    }

    private fun setUp() {
        presenter.initBoardCastListener(requireContext())
        presenter.initVideoDownloader(requireContext())
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            purchaseCompleteBoardCast, IntentFilter(StringConstants.sessionBroadCastAction)
        )
        hideProgress()
        getIntentData()
        initListeners()
        getInstrumentData()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
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

        binding.txtBuyPart.setOnClickListener { proceedToBuyAndAddToCart() }

        binding.txtBuyMultiPart.setOnClickListener { navigateToBuyMultiPart() }

        binding.txtPremiumVideo.setOnClickListener {
//            if (instrumentDetailResponse?.isPremiumBought == true) {
//                navigateToAppendixVideo()
//            } else {
            navigateToPremiumVideo()
//            }
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
                if (isNetworkAvailable())
                    favoriteChange()
                else
                    showMessageDialog(R.string.error_no_internet_connection)
            }
        }
    }

    private fun favoriteChange() {
        if (instrumentDetailResponse?.isFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_session)
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

    private fun openOrganizationChart() {
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.url,
            this.instrumentDetailResponse?.hallSoundDetail?.organizationDiagram,
            OrganizationChartActivity::class
        )
    }

    private fun navigateToPremiumVideo() {
        val bundle = bundleOf(Constants.session to instrumentResponse)
        val navController = NavHostFragment.findNavController(this)
        bundle.putInt(StringConstants.selectedPos, getSelectedPosition() ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, getIsFromFav() ?: false)
        Router.navigateFragmentWithData(navController, R.id.sessionDetailPremiumFragment, bundle)
    }

    private fun navigateToAppendixVideo() {
        instrumentResponse?.fromPage = StringConstants.partPage
        val bundle = bundleOf(Constants.session to instrumentResponse)
        val navController = NavHostFragment.findNavController(this)
        bundle.putInt(StringConstants.selectedPos, getSelectedPosition() ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, getIsFromFav() ?: false)
        Router.navigateFragmentWithData(navController, R.id.appendixFragment, bundle)
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

    private fun proceedToBuyAndAddToCart() {
        if (instrumentDetailResponse?.isPartBought == false) {
            if (checkLoginState()) {
                DialogUtils.showSessionCheckoutDialog(requireContext(), instrumentDetailResponse, {
                    proceedToBuy()
                }, { proceedToAddToCart() }, isCancelable = false, Constants.part
                )
            }
        } else {
            presenter.playVideo(requireContext())
        }
    }

    private fun proceedToAddToCart() {
        val cartItems: MutableList<AddToCart> = mutableListOf()
        cartItems.add(
            AddToCart(
                orchestraId = instrumentResponse?.sessionId,
                type = ApiConstant.session,
                sessionType = Constants.part,
                price = instrumentDetailResponse?.partPrice ?: 0.0,
                musicianId = instrumentResponse?.musicianId,
                instrumentId = instrumentResponse?.instrument_id,
                identifier = instrumentDetailResponse?.partIdentifier,
                releaseDate = instrumentDetailResponse?.hallSoundDetail?.releaseDate
            )
        )
        presenter.addToCart(
            AddToCartList(cartItems)
        )
    }

    private fun proceedToBuy() {
        val purchasedItemsList: MutableList<CartListResponse> = mutableListOf()
        val cartItem = AppInfoGlobal.cartInfo?.find {
            it.orchestraId == instrumentResponse?.sessionId && it.type == ApiConstant.session && it.sessionType == ApiConstant.part && it.musicianId == instrumentResponse?.musicianId && it.instrumentId == instrumentResponse?.instrument_id
        }

        val cartPremiumItem = AppInfoGlobal.cartInfo?.find {
            it.orchestraId == instrumentResponse?.sessionId && it.type == ApiConstant.session && it.sessionType == Constants.comboType && it.musicianId == instrumentResponse?.musicianId && it.instrumentId == instrumentResponse?.instrument_id
        }
        if (cartItem != null) purchasedItemsList.add(
            cartItem
        )
        else purchasedItemsList.add(
            CartListResponse(
                id = cartPremiumItem?.id,
                orchestraId = instrumentResponse?.sessionId,
                type = ApiConstant.session,
                sessionType = ApiConstant.part,
                instrumentId = instrumentResponse?.instrument_id,
                musicianId = instrumentResponse?.musicianId,
                price = instrumentDetailResponse?.partPrice ?: 0.0,
                identifier = instrumentDetailResponse?.partIdentifier,
                releaseDate = instrumentDetailResponse?.hallSoundDetail?.releaseDate
            )
        )

        presenter.buyOrchestra(purchasedItemsList)
    }

    private fun navigateToBuyMultiPart() {
        if (checkLoginState()) {
            presenter.orchestra?.sessionId = instrumentResponse?.sessionId
            presenter.orchestra?.fromPage = StringConstants.partPage
            val bundle = bundleOf(Constants.session to presenter.orchestra)
            val navController = NavHostFragment.findNavController(this)
            Router.navigateFragmentWithData(navController, R.id.buyMultiPartFragment, bundle)
        }
    }

    override fun createPresenter() = SessionDetailPresenter()

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

    override fun success(instrumentDetailResponse: InstrumentDetailResponse) {
        this.instrumentDetailResponse = instrumentDetailResponse
        setData(instrumentDetailResponse.hallSoundDetail)
    }

    override fun addToCartSuccess(cartListResponse: List<CartListResponse>?) {
        AppInfoGlobal.cartInfo = mutableListOf()
        AppInfoGlobal.cartInfo?.addAll(cartListResponse ?: listOf())
        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionAddToCartSuccessDialog(
            requireContext(), instrumentDetailResponse, false, {
                val bundle = bundleOf()
                bundle.putString(BundleConstant.fragmentType, Constants.session)
                Router.navigateFragmentWithData(findNavController(), R.id.cartListFragment, bundle)
            }, Constants.part
        )
    }

    override fun purchaseSuccess(
        message: String?
    ) {
        instrumentDetailResponse?.isPartBought = true
        checkIsBrought(instrumentDetailResponse?.isPartBought)
        AppInfoGlobal.cartInfo?.removeIf {
            it.orchestraId == instrumentResponse?.sessionId && it.musicianId == instrumentResponse?.musicianId && it.instrumentId == instrumentResponse?.instrument_id
        }
        (requireContext() as? LandingActivity)?.showCartCount()
        DialogUtils.showSessionCheckoutSuccessDialog(
            requireContext(), instrumentDetailResponse, false, Constants.part
        )
        // getInstrumentData()
    }

    override fun noData() {
        findNavController().popBackStack()
    }

    private fun setData(hallSoundDetail: HallSoundResponse?) {
        binding.scvBody.viewVisible()
        binding.divEnd.viewVisible()
        binding.lltBuyPart.viewVisible()

        Picasso.get().load(instrumentDetailResponse?.vrThumbnail)
 //           .resize(binding.imgSessionThumbnail.width, binding.imgSessionThumbnail.height)
            .placeholder(R.drawable.ic_thumbnail).into(binding.imgSessionThumbnail)

        setPartNotice()

//        if (instrumentDetailResponse?.isPremiumBought == true) {
//            binding.txtPremiumVideo.text = getString(R.string.premiumAppendix)
//        } else {
        binding.txtPremiumVideo.text = getString(R.string.checkPremiumVideo)
//        }

        if (instrumentDetailResponse?.playerDetail?.name.isNullOrEmpty()) {
            binding.txtPlayerName.viewGone()
        } else {
            binding.txtPlayerName.viewVisible()
            binding.txtPlayerName.text = instrumentDetailResponse?.playerDetail?.name
        }

        if (hallSoundDetail?.band.isNullOrEmpty()) {
            binding.lltOrchestraName.viewGone()
        } else {
            binding.txvOrchestraName.text = hallSoundDetail?.band
        }

        if (hallSoundDetail?.title.isNullOrEmpty()) {
            binding.txtTitleEng.viewGone()
        } else {
            binding.txtTitleEng.text = hallSoundDetail?.title
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
            if (!splitText.isNullOrEmpty()) {
                if (splitText.size > 1) {
                    binding.txvConductorJpn.viewVisible()
                    binding.txvConductorEng.text = splitText[0].trim()
                    binding.txvConductorJpn.text = splitText[1].trim()
                } else if (splitText.size == 1) {
                    binding.txvConductorEng.text = splitText[0].trim()
                }
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

        if (instrumentDetailResponse?.hallSoundDetail?.organizationDiagram.isNullOrEmpty()) {
            binding.txtOrganizationChart.viewGone()
        } else {
            binding.txtOrganizationChart.viewVisible()
        }
        if (instrumentDetailResponse?.iosVrFile.isNullOrEmpty())
            binding.txtPlayVideo.viewGone()
        else binding.txtPlayVideo.viewVisible()
        checkIsBrought(instrumentDetailResponse?.isPartBought)
    }

    private fun setPartNotice() {
        if (instrumentDetailResponse?.isPremiumBought == true) {
            binding.txtPartNotice.viewGone()
        } else {
            binding.txtPartNotice.viewVisible()
        }
    }

    private fun checkIsBrought(isPartBought: Boolean?) {
        if (isPartBought == false) {
            setPartPrice()
            binding.txtPlayVideo.text = getString(R.string.playSampleVideo)
        } else {
            binding.txtBuyPart.viewGone()

            binding.txtBuyMultiPart.viewGone()

            binding.txtBuyPart.text = getString(R.string.text_record)
            binding.txtPlayVideo.text = getString(R.string.text_play_video)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPartPrice() {
//        if (instrumentDetailResponse?.partPrice == null) {
//            binding.txtBuyPart.text =
//                requireContext().getString(R.string.buyPart) + " " + requireContext().getString(
//                    R.string.zeroYen
//                ) + " " + getString(R.string.points) + " " + requireContext().getString(
//                    R.string.toBuy
//                )
//        } else {
        binding.txtBuyPart.text = requireContext().getString(R.string.buyPart)
//        }
    }

    private var purchaseCompleteBoardCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            val sessionId = intent?.getStringExtra(BundleConstant.sessionId)
            val sessionType = intent?.getStringExtra(BundleConstant.session)
            if (sessionId == instrumentResponse?.sessionId.toString()) {
                if ((sessionType == Constants.comboType || sessionType == Constants.premium)) {
                    instrumentDetailResponse?.isPremiumBought = true
                }
                instrumentDetailResponse?.isPartBought = true
                checkIsBrought(instrumentDetailResponse?.isPartBought)
                isPartPurchaseUpdated = true
                setPartNotice()
            }
        }
    }

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

    override fun navigateToPlayer(
        orchestra: HallSoundResponse?, filePath: String?, fileName: String?
    ) {
        orchestra?.vrFile = filePath
        orchestra?.isBoughtForUnity =
            instrumentDetailResponse?.isPartBought == true && instrumentDetailResponse?.isPremiumBought == true
        orchestra?.instrumentName = instrumentDetailResponse?.name
        orchestra?.businessType = instrumentDetailResponse?.hallSoundDetail?.businessType
        if (instrumentDetailResponse?.isPartBought == false) {
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