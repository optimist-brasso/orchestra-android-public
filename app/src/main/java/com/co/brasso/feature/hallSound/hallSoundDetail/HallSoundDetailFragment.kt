package com.co.brasso.feature.hallSound.hallSoundDetail


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentHallsoundDetailBinding
import com.co.brasso.feature.hallSound.hallSoundPlayer.HallSoundPlayerFragment
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.hallsound.HallSoundFavouriteFragment
import com.co.brasso.feature.landing.listSong.listSongHallSound.ListSongHallSoundFragment
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.request.cart.AddToCart
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso

class HallSoundDetailFragment : BaseFragment<HallSoundDetailView, HallSoundDetailPresenter>(),
    HallSoundDetailView {

    private lateinit var binding: FragmentHallsoundDetailBinding
    private var navBar: BottomNavigationView? = null
    private var purchasedItemsList: MutableList<CartListResponse>? = mutableListOf()
    private lateinit var hallSoundPlayerFragment: HallSoundPlayerFragment
    private var callback: OnBackPressedCallback? = null
    private var isFirst = true
    private var orchestraId: Int? = null
    private var isPaused: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) binding = FragmentHallsoundDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setup()
            isFirst = false
        } else {
            presenter.getDownloadedFilePath()
        }
        hideDownload()
        initToolbar()
    }

    private fun initBoardCastListener() {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            purchaseCompleteBoardCast, IntentFilter(StringConstants.hallSoundBroadCastAction)
        )
    }

    override fun createPresenter() = HallSoundDetailPresenter()

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun setup() {
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)
        presenter.initBoardCastListener(requireContext())
        presenter.initVideoDownloader(requireContext())
        hideProgress()
        initBoardCastListener()
        initClickListener()
        initListener()
        arguments?.apply {
            orchestraId = getInt(BundleConstant.orchestraId)
            getHallSoundDetail()
            val stateManagement = getBoolean(BundleConstant.stateManagement)
            if (stateManagement) {
                (requireContext() as? LandingActivity)?.shouldRefreshHome = true
            }
        }
    }

    private fun getHallSoundDetail() {
        val videoSupport = if (VideoResolution.is4kSupported() == true) {
            ApiConstant.fourKSupport
        } else {
            ApiConstant.hdSupport
        }
        presenter.getOrchestraDetail(
            orchestraId, videoSupport
        )
    }

    private fun initListener() {
        binding.swpPlayer.setOnRefreshListener {
            binding.swpPlayer.isRefreshing = false
            getHallSoundDetail()
        }
    }

    override fun onDestroyView() {
        callback?.remove()
        super.onDestroyView()
    }

    override fun onResume() {
        isPaused = false
        super.onResume()
        if (::hallSoundPlayerFragment.isInitialized) {
            if (navBar?.visibility == View.VISIBLE) {
                navBar?.visibility = View.GONE
            }
        }
    }

    private fun initClickListener() {
        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressedHandler()
        }

        binding.imgDownload.clicks().throttle()?.subscribe {
            presenter.cancelDownload()
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.txvBuy.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(requireContext(), getString(R.string.please_login), {
                    StateManagement.pageName = Constants.hallSound
                    StateManagement.id = orchestraId
                    navigateToLogin()
                }, {}, isCancelable = false, showNegativeBtn = true, getString(R.string.login)
                )
            } else {
                showPurchaseDialog()
            }
        }

        binding.imgFavourite.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(requireContext(), getString(R.string.please_login), {
                    StateManagement.pageName = Constants.hallSound
                    StateManagement.id = orchestraId
                    navigateToLogin()
                }, {}, isCancelable = false, showNegativeBtn = true, getString(R.string.login)
                )
            } else {
                if (isNetworkAvailable()) favoriteChange()
                else showMessageDialog(R.string.error_no_internet_connection)
            }
        }

        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(presenter?.orchestra?.title, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incSubscribedOption.llt1stSeat.clicks().throttle()?.subscribe {
            playAudio(0)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incSubscribedOption.llt2ndSeat.clicks().throttle()?.subscribe {
            playAudio(1)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incSubscribedOption.llt3rdSeat.clicks().throttle()?.subscribe {
            playAudio(2)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incSubscribedOption.llt4thSeat.clicks().throttle()?.subscribe {
            playAudio(3)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.txtConductor.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter?.orchestra?.id)
            bundle.putString(BundleConstant.fragmentType, Constants.conductor)
            NavHostFragment.findNavController(this).navigate(R.id.conductorDetailFragment, bundle)
        }

        binding.txtSession.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter?.orchestra?.id)
            NavHostFragment.findNavController(this).navigate(R.id.sessionLayoutFragment, bundle)
        }

        binding.txtPlayer.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter?.orchestra?.id)
            NavHostFragment.findNavController(this).navigate(R.id.orchestraPlayerFragment, bundle)
        }
    }

    private fun playAudio(position: Int) {
        if (binding.progressBar.isVisible) showMessage(resources.getString(R.string.msg_downloading))
        else presenter?.playAudio(context, position)
    }

    private fun favoriteChange() {
        if (presenter?.orchestra?.isHallsoundFavourite == true) {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_session
                )
            )

            if (getIsFromFav() == true) {
                HallSoundFavouriteFragment.selectedPos = getSelectedPosition()
            } else {
                ListSongHallSoundFragment.selectedPos = getSelectedPosition()
            }
        } else {
            binding.imgFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_favourite_selected
                )
            )

            if (getIsFromFav() == true) {
                HallSoundFavouriteFragment.selectedPos = null
            } else {
                ListSongHallSoundFragment.selectedPos = getSelectedPosition()
            }
        }
        presenter?.orchestra?.isHallsoundFavourite =
            !(presenter?.orchestra?.isHallsoundFavourite ?: false)
        presenter.addFavourite(
            FavouriteEntity(
                ApiConstant.hallSound, presenter?.orchestra?.id.toString()
            )
        )
    }

    private fun showPurchaseDialog() {
        DialogUtils.showPurchaseRequestDialog(requireContext(), presenter?.orchestra, {
            buyOrchestra()
        }, {
            addToCart()
        })
    }

    private fun buyOrchestra() {
        purchasedItemsList = mutableListOf()
        val cartItem =
            AppInfoGlobal.cartInfo?.find { it.orchestraId == presenter?.orchestra?.id && it.type == ApiConstant.hallSound }
        if (cartItem != null) {
            purchasedItemsList?.add(
                cartItem
            )
        } else purchasedItemsList?.add(
            CartListResponse(
                orchestraId = presenter?.orchestra?.id,
                type = ApiConstant.hallSound,
                musicianId = presenter?.orchestra?.musicianId,
                price = presenter?.orchestra?.hallSoundPrice ?: 0.0,
                identifier = presenter?.orchestra?.hallSoundIdentifier,
                releaseDate = presenter?.orchestra?.releaseDate
            )
        )
//        if (!presenter?.orchestra?.hallSoundIdentifier.isNullOrEmpty())
        presenter.buyOrchestra(purchasedItemsList)
//        else
//            showMessageDialog(R.string.text_product_not_found)
    }

    private fun addToCart() {
        if (AppInfoGlobal.cartInfo?.find { it.orchestraId == presenter?.orchestra?.id && it.type == ApiConstant.hallSound } != null) {
            showMessageDialog(requireContext().getString(R.string.already_added_in_cart))
            return
        }
        val cartItems: MutableList<AddToCart> = mutableListOf()
        cartItems.add(
            AddToCart(
                orchestraId = presenter?.orchestra?.id,
                type = ApiConstant.hallSound,
                price = presenter?.orchestra?.hallSoundPrice ?: 0.0,
                musicianId = presenter?.orchestra?.musicianId,
                identifier = presenter?.orchestra?.hallSoundIdentifier,
                releaseDate = presenter?.orchestra?.releaseDate
            )
        )
        presenter.addToCart(
            AddToCartList(cartItems)
        )
    }

    private var purchaseCompleteBoardCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            purchaseSuccess(Constants.emptyText, false)

        }
    }

    override fun updateViews() {
        if (getLoginState() && presenter?.orchestra?.isBought == true) {
            showMusicPlayer()
//            showDownload()
            setHallSoundPlayer()

        } else {
            hideMusicPlayer()
            hideDownload()
        }
    }

    override fun setHallSoundDetail(hallSound: HallSoundResponse) {
        binding.scvBody.viewVisible()
        Picasso.get().load(hallSound.venueDiagram).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imvTitle)

        hallSound.title?.let {
            binding.txtEngTitleMain.text = hallSound.title
            binding.txtEngTitleSub.text = hallSound.title
        } ?: kotlin.run {
            binding.txtEngTitleMain.viewGone()
            binding.txtEngTitleSub.viewGone()
        }

        hallSound.titleJp?.let {
            binding.txtJapTitleMain.text = hallSound.titleJp
            binding.txtJapTitleSub.text = hallSound.titleJp
        } ?: kotlin.run {
            binding.txtJapTitleMain.viewGone()
            binding.txtJapTitleSub.viewGone()
        }

        hallSound.venueTitle?.let {

            val splitText = hallSound.venueTitle.split(",")

            if (splitText.size > 1) {
                binding.band.viewVisible()
                binding.band.text = splitText[0].trim() + "\n" + splitText[1].trim()
            } else {
                binding.band.text = splitText[0].trim()
            }
        } ?: kotlin.run { binding.band.viewGone() }

        hallSound.venueDescription?.let { binding.txvDescription.text = hallSound.venueDescription }
            ?: kotlin.run { binding.txvDescription.viewGone() }

        if (hallSound.venueTitle.isNullOrEmpty() && hallSound.venueDescription.isNullOrEmpty()) binding.imvDivider.viewGone()
        else binding.imvDivider.viewVisible()

        hallSound.composer?.let {

            val splitText = hallSound.composer.split(",")

            if (splitText.size > 1) {
                binding.txvComposerJpn.viewVisible()
                binding.txvComposerEng.text = splitText[0].trim()
                binding.txvComposerJpn.text = splitText[1].trim()
            } else {
                binding.txvComposerEng.text = splitText[0].trim()
            }
        } ?: kotlin.run { binding.rltComposer.viewGone() }

        hallSound.band?.let { binding.txvOrchestraNameEng.text = hallSound.band }
            ?: kotlin.run { binding.lnlOrchestraName.viewGone() }

        hallSound.conductor?.let {
            val splitText = hallSound.conductor.split(",")

            if (splitText.size > 1) {
                binding.txvConductorJpn.viewVisible()
                binding.txvConductorEng.text = splitText[0].trim()
                binding.txvConductorJpn.text = splitText[1].trim()
            } else {
                binding.txvConductorEng.text = splitText[0].trim()
            }
        } ?: kotlin.run { binding.rltConductor.viewGone() }

        hallSound.organization?.let { binding.txvOrganization.text = hallSound.organization }
            ?: kotlin.run { binding.lnlOrganization.viewGone() }

        hallSound.venueTitle?.let {
            val splitText = hallSound.venueTitle.split(",")

            if (splitText.size > 1) {
                binding.txvVenueNameJpn.viewVisible()
                binding.txvVenueNameEng.text = splitText[0].trim()
                binding.txvVenueNameJpn.text = splitText[1].trim()
            } else {
                binding.txvVenueNameEng.text = splitText[0].trim()
            }
        } ?: kotlin.run { binding.rltVenueName.viewGone() }

        hallSound.releaseDate?.let {
            binding.txvDate.text = hallSound.releaseDate
        } ?: kotlin.run { binding.lnlData.viewGone() }

        hallSound.duration?.let {
            binding.txvLap.text = DateUtils.convertIntToRecordTime(hallSound.duration)
        } ?: kotlin.run { binding.lnlLap.viewGone() }

        hallSound.jasracLicenseNumber?.let {
            binding.txvLicence.text = hallSound.jasracLicenseNumber
        } ?: kotlin.run { binding.lnlLicence.viewGone() }

        hallSound.businessType?.let {
            binding.txvBusinessType.text = it
        } ?: kotlin.run { binding.txvBusinessType.viewGone() }

        val isFavourite = hallSound.isHallsoundFavourite

        if (isFavourite == true) {
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

        updateViews()

        if (presenter?.orchestra?.businessType.isNullOrEmpty()) {
            binding.txvBusinessType.viewGone()
        } else {
            binding.txvBusinessType.viewVisible()
            binding.txvBusinessType.text = presenter?.orchestra?.businessType
        }
    }

    private fun showDownload() {
        binding.imgDownload.viewVisible()
    }

    private fun hideDownload() {
        binding.imgDownload.viewGone()
    }

    private fun showMusicPlayer() {
        binding.incSubscribedOption.lnlSubscribedOption.viewVisible()
        binding.txvBuy.viewGone()
    }

    private fun hideMusicPlayer() {
        binding.incSubscribedOption.lnlSubscribedOption.viewGone()
        binding.txvBuy.viewVisible()
    }

    private fun setHallSoundPlayer() {
        val orchestraHallSoundList = presenter?.orchestra?.hallSound

        if (orchestraHallSoundList?.isNotEmpty() == true && orchestraHallSoundList.size == 1) {
            binding.incSubscribedOption.llt1stSeat.viewVisible()
            binding.incSubscribedOption.txv1stSeat.text =
                orchestraHallSoundList.get(0).type?.trim()?.lowercase()
        } else {
            binding.incSubscribedOption.llt1stSeat.viewGone()
            binding.incSubscribedOption.llt2ndSeat.viewGone()
            binding.incSubscribedOption.llt3rdSeat.viewGone()
            binding.incSubscribedOption.llt4thSeat.viewGone()
        }

        if (orchestraHallSoundList?.isNotEmpty() == true && orchestraHallSoundList.size == 2) {
            binding.incSubscribedOption.llt1stSeat.viewVisible()
            binding.incSubscribedOption.llt2ndSeat.viewVisible()
            binding.incSubscribedOption.txv1stSeat.text =
                orchestraHallSoundList.get(0).type?.trim()?.lowercase()
            binding.incSubscribedOption.txv2ndSeat.text =
                orchestraHallSoundList.get(1).type?.trim()?.lowercase()
        } else {
            binding.incSubscribedOption.llt3rdSeat.viewGone()
            binding.incSubscribedOption.llt4thSeat.viewGone()
        }

        if (orchestraHallSoundList?.isNotEmpty() == true && orchestraHallSoundList.size == 3) {
            binding.incSubscribedOption.llt1stSeat.viewVisible()
            binding.incSubscribedOption.llt2ndSeat.viewVisible()
            binding.incSubscribedOption.llt3rdSeat.viewVisible()
            binding.incSubscribedOption.txv1stSeat.text =
                orchestraHallSoundList.get(0).type?.trim()?.lowercase()
            binding.incSubscribedOption.txv2ndSeat.text =
                orchestraHallSoundList.get(1).type?.trim()?.lowercase()
            binding.incSubscribedOption.txv3rdSeat.text =
                orchestraHallSoundList.get(2).type?.trim()?.lowercase()
        } else {
            binding.incSubscribedOption.llt4thSeat.viewGone()
        }

        if (orchestraHallSoundList?.isNotEmpty() == true && orchestraHallSoundList.size == 4) {
            binding.incSubscribedOption.llt1stSeat.viewVisible()
            binding.incSubscribedOption.llt2ndSeat.viewVisible()
            binding.incSubscribedOption.llt3rdSeat.viewVisible()
            binding.incSubscribedOption.llt4thSeat.viewVisible()
            binding.incSubscribedOption.txv1stSeat.text =
                orchestraHallSoundList[0].type?.lowercase()
            binding.incSubscribedOption.txv2ndSeat.text =
                orchestraHallSoundList[1].type?.lowercase()
            binding.incSubscribedOption.txv3rdSeat.text =
                orchestraHallSoundList[2].type?.lowercase()
            binding.incSubscribedOption.txv4thSeat.text =
                orchestraHallSoundList[3].type?.lowercase()
        }
    }

    override fun addToCartSuccess(cartListResponse: List<CartListResponse>?) {
        AppInfoGlobal.cartInfo = mutableListOf()
        AppInfoGlobal.cartInfo?.addAll(cartListResponse ?: listOf())
        initToolbar()
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun purchaseSuccess(message: String?, showCartCount: Boolean) {
        showMusicPlayer()
        setHallSoundPlayer()
//        showDownload()
        AppInfoGlobal.cartInfo?.removeIf { it.orchestraId == presenter?.orchestra?.id && it.type == ApiConstant.hallSound }
        if (showCartCount) (activity as? LandingActivity)?.showCartCount()
    }

    override fun noInternet(errorNoInternetConnection: Int) {
        binding.scvBody.viewGone()
        DialogUtils.showAlertDialog(requireContext(), getString(errorNoInternetConnection), {}, {})
    }

    private fun onBackPressedHandler() {
        navBar?.visibility = View.VISIBLE
        if (::hallSoundPlayerFragment.isInitialized) hallSoundPlayerFragment.releasePlayer()
        findNavController().popBackStack()
    }

    override fun onPause() {
        if (::hallSoundPlayerFragment.isInitialized) hallSoundPlayerFragment.playerPause()
        isPaused = true
        super.onPause()
    }

    private fun getIsFromFav() = arguments?.getBoolean(StringConstants.isFromFav, false)

    private fun getSelectedPosition() = arguments?.getInt(StringConstants.selectedPos)

    override fun hidePlayButton() {
    }

    override fun showPlayButton() {
    }

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
        binding.scvBody.setPadding(0, 0, 0, 300)
        if (!::hallSoundPlayerFragment.isInitialized) {
            hallSoundPlayerFragment = HallSoundPlayerFragment(isPaused)
            val bundle = bundleOf(BundleConstant.playerDetail to orchestra)
            hallSoundPlayerFragment.arguments = bundle
            hallSoundPlayerFragment.audioFileName = fileName
            hallSoundPlayerFragment.path=filePath
            Router.attachFragment(
                childFragmentManager, binding.frtPlayer.id, hallSoundPlayerFragment
            )
        } else {
            hallSoundPlayerFragment.audioFileName = fileName
            hallSoundPlayerFragment.path=filePath
            hallSoundPlayerFragment.playNext(orchestra)
        }
        if (navBar?.visibility == View.VISIBLE) navBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        presenter.onDestroy(requireContext())
        super.onDestroy()
    }
}