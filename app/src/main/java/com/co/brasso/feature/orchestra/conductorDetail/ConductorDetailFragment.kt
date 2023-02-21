package com.co.brasso.feature.orchestra.conductorDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentConductorDetailBinding
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.conductor.ConductorFavouriteFragment
import com.co.brasso.feature.landing.listSong.listSongOrchestra.ListSongOrchestraFragment
import com.co.brasso.feature.orchestra.conductorDetail.organizationchart.OrganizationChartActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.feature.vlcPlayer.VLCPlayerActivity
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

class ConductorDetailFragment : BaseFragment<ConductorDetailView, ConductorDetailPresenter>(),
    ConductorDetailView {

    private lateinit var binding: FragmentConductorDetailBinding
    private var isFirst = true
    private var orchestraId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentConductorDetailBinding.inflate(inflater, container, false)
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
            //  presenter.refreshData()
            presenter.getDownloadedFilePath()
        }
        initToolBar()
    }

    private fun setUp() {
        presenter.initBoardCastListener(requireContext())
        presenter.initVideoDownloader(requireContext())
        hideProgress()
        getOrchestraId()
        initClickListener()
        initListener()
    }

    private fun initListener() {
        binding.swpPlayer.setOnRefreshListener {
            binding.swpPlayer.isRefreshing = false
            getOrchestraId()
        }
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    override fun createPresenter() = ConductorDetailPresenter()

    private fun initClickListener() {
        binding.imvPlay.setOnClickListener {
            presenter.playVideo(requireContext())
        }

        binding.txvOrganizationChart.setOnClickListener {
            openOrganizationChart()
        }

        binding.btnFavorite.setOnClickListener {
            if (!getLoginState()) {
                DialogUtils.showAlertDialog(
                    requireContext(),
                    getString(R.string.please_login),
                    {
                        StateManagement.pageName = Constants.conductorDetail
                        StateManagement.id = orchestraId
                        navigateToLoginWithOutData()
                    },
                    {},
                    isCancelable = false,
                    showNegativeBtn = true,
                    getString(R.string.login)
                )
            } else {
                if (isNetworkAvailable())
                    addToFavourite()
                else
                    showMessageDialog(R.string.error_no_internet_connection)
            }
        }

        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(presenter.orchestra?.title, requireContext())
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.imgDownload.clicks().throttle()?.subscribe {
            presenter.downloadVideo(requireContext())
        }?.let { compositeDisposable?.add(it) }

        binding.txtSession.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter.orchestra?.id)
            bundle.putString(BundleConstant.fragmentType, Constants.session)
            val navController = NavHostFragment.findNavController(this)
            Router.navigateFragmentWithData(
                navController,
                R.id.sessionLayoutFragment,
                bundle
            )
        }

        binding.txtHallSound.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter.orchestra?.id)
            bundle.putString(BundleConstant.fragmentType, Constants.hallSound)
            val navController = NavHostFragment.findNavController(this)
            Router.navigateFragmentWithData(navController, R.id.hallSoundDetailFragment, bundle)
        }

        binding.txtPlayer.setOnClickListener {
            val bundle = bundleOf(BundleConstant.orchestraId to presenter.orchestra?.id)
            NavHostFragment.findNavController(this).navigate(R.id.orchestraPlayerFragment, bundle)
        }
    }

    private fun navigateToLoginWithOutData() {
        Router.navigateActivity(requireContext(), false, LoginActivity::class)
    }

    private fun addToFavourite() {
        val isFavourite = presenter.orchestra?.isConductorFavourite ?: false
        if (isFavourite) {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_session
                )
            )

            if (getIsFromFav() == true) {
                ConductorFavouriteFragment.selectedPos = getSelectedPosition()
            } else {
                ListSongOrchestraFragment.selectedPos = getSelectedPosition()
            }
        } else {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_selected
                )
            )

            if (getIsFromFav() == true) {
                ConductorFavouriteFragment.selectedPos = null
            } else {
                ListSongOrchestraFragment.selectedPos = getSelectedPosition()
            }
        }
        presenter.orchestra?.isConductorFavourite =
            !(presenter.orchestra?.isConductorFavourite ?: false)
        presenter.addFavourite(
            FavouriteEntity(
                Constants.conductor,
                presenter.orchestra?.id.toString()
            )
        )
    }

    private fun openOrganizationChart() {
        Router.navigateActivityWithData(
            requireContext(),
            false,
            BundleConstant.url,
            presenter.orchestra?.organizationDiagram,
            OrganizationChartActivity::class
        )
    }

    private fun getOrchestraId() {
        arguments?.apply {
            orchestraId = getInt(BundleConstant.orchestraId)
            val stateManagement = arguments?.getBoolean(BundleConstant.stateManagement)
            if (stateManagement == true) {
                (requireContext() as? LandingActivity)?.shouldRefreshHome = true
            }
            if (orchestraId != null) {
                val videoSupport = if (VideoResolution.is4kSupported() == true) {
                    ApiConstant.fourKSupport
                } else {
                    ApiConstant.hdSupport
                }
                presenter.getOrchestraDetail(
                    orchestraId,
                    videoSupport
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setConductorDetail(conductor: HallSoundResponse) {
        binding.srvMainView.viewVisible()
        binding.lnlView.viewVisible()

        presenter.orchestra = conductor

//        if (conductor.vrThumbnail.isNullOrEmpty())
//            Picasso.get().load(conductor.image).placeholder(R.drawable.ic_thumbnail)
//                .into(binding.imvConductorImage)
//        else
            Picasso.get().load(conductor.vrThumbnail).placeholder(R.drawable.ic_thumbnail)
                .into(binding.imvConductorImage)

        binding.txtTitle.text = conductor.title?.plus("\n").plus(conductor.titleJp)

        conductor.introduction?.let { binding.txvDescription.text = conductor.introduction }
            ?: kotlin.run { binding.txvDescription.viewGone() }

        conductor.composer?.let {
            val splitText = conductor.composer.split(",")
            if (splitText.size > 1) {
                binding.txvComposerJpn.viewVisible()
                binding.txvComposerEng.text = splitText[0].trim()
                binding.txvComposerJpn.text = splitText[1].trim()
            } else {
                binding.txvComposerEng.text = splitText[0].trim()
            }
        }
            ?: kotlin.run { binding.rltComposer.viewGone() }

        conductor.band?.let { binding.txvOrchestraNameEng.text = conductor.band }
            ?: kotlin.run { binding.lnlOrchestraName.viewGone() }

        conductor.conductor?.let {
            val splitText = conductor.conductor.split(",")

            if (splitText.size > 1) {
                binding.txvConductorJpn.viewVisible()
                binding.txvConductorEng.text = splitText[0].trim()
                binding.txvConductorJpn.text = splitText[1].trim()
            } else {
                binding.txvConductorEng.text = splitText[0].trim()
            }
        }
            ?: kotlin.run { binding.rltConductor.viewGone() }

        conductor.organization?.let { binding.txvOrganization.text = conductor.organization }
            ?: kotlin.run { binding.lnlOrganization.viewGone() }

        conductor.venueTitle?.let {
            val splitText = conductor.venueTitle.split(",")

            if (splitText.size > 1) {
                binding.txvVenueNameJpn.viewVisible()
                binding.txvVenueNameEng.text = splitText[0].trim()
                binding.txvVenueNameJpn.text = splitText[1].trim()
            } else {
                binding.txvVenueNameEng.text = splitText[0].trim()
            }
        }
            ?: kotlin.run { binding.rltVenueName.viewGone() }

        conductor.releaseDate?.let {
            binding.txvDate.text = conductor.releaseDate
        }
            ?: kotlin.run { binding.lnlData.viewGone() }

        conductor.duration?.let {
            binding.txvLap.text = DateUtils.convertIntToRecordTime(conductor.duration)
        }
            ?: kotlin.run { binding.lnlLap.viewGone() }

        conductor.organizationDiagram?.let { binding.txvOrganizationChart.viewVisible() }
            ?: kotlin.run { binding.txvOrganizationChart.viewGone() }

        conductor.jasracLicenseNumber?.let {
            binding.txvLicence.text = conductor.jasracLicenseNumber
        } ?: kotlin.run { binding.lnlLicence.viewGone() }

        conductor.businessType?.let {
            binding.txvBusinessType.text = it
        } ?: kotlin.run { binding.txvBusinessType.viewGone() }

        val isFavourite = presenter.orchestra?.isConductorFavourite ?: false
        if (isFavourite) {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_selected
                )
            )
        } else {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_session
                )
            )
        }

        if (conductor.organizationDiagram.isNullOrEmpty())
            binding.txvOrganizationChart.viewGone()
        else
            binding.txvOrganizationChart.viewVisible()
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun noInternet(errorNoInternetConnection: Int) {
        binding.srvMainView.viewGone()
        DialogUtils.showAlertDialog(requireContext(), getString(errorNoInternetConnection), {}, {})
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

    override fun navigateToPlayer(
        orchestra: HallSoundResponse?,
        filePath: String?,
        fileName: String?
    ) {
        orchestra?.vrFile = filePath
        orchestra?.isBoughtForUnity = true
        orchestra?.playDuration = null
        Router.navigateActivityWithParcelableData(
            context,
            false,
            BundleConstant.orchestraId,
            orchestra,
            VLCPlayerActivity::class
        )
    }

    override fun hideDownloadButton() {
        binding.imgDownload.viewGone()
    }

    override fun onDestroy() {
        presenter.onDestroy(requireContext())
        super.onDestroy()
    }
}