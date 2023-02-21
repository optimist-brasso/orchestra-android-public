package com.co.brasso.feature.session

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.databinding.ActivitySessionLayoutBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.feature.shared.model.response.sessionBoughtStatus.SessionBoughtStatus
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.toPxFromDp
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DensityUtils
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.VideoResolution
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso

class SessionLayoutFragment : BaseFragment<SessionLayoutView, SessionLayoutPresenter>(),
    SessionLayoutView {

    private lateinit var binding: ActivitySessionLayoutBinding
    private var sessionId: Int? = 0
    private var screenWidth = 500
    private var isFirst = true
    private var navBar: BottomNavigationView? = null
    private var screenHeight = 300
    private var mobileScreenHeight: Int? = null
    private var backGroundImageWidth: Int? = null
    private var tollBarHeight = 73
    private var partBoughtStatus: MultiPartListResponseItem? = null
    private var dialog: Dialog? = null
    private var instrumentResponse: InstrumentResponse? = null
    private var instrumentDialog: Dialog? = null
    private var selectedPos: Int? = null
    private var isFromFav: Boolean? = false
    private var sessionLayoutResponse: SessionLayoutResponse? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = ActivitySessionLayoutBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenLandScape()
        if (isFirst) {
            setup()
            isFirst = false
        }
        initToolbar()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolbar()
        (activity as? LandingActivity)?.hideBottomBar()
    }

    private fun setup() {
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)
        if (navBar?.visibility == View.VISIBLE) {
            navBar?.visibility = View.GONE
        }
        (activity as LandingActivity).hideToolbar()
        setBackgroundImageResolution()
        initListeners()
        getIntentData()
        presenter.getSessionLayout(sessionId)
        showDialogBox()
    }

    private fun setBackgroundImageResolution() {
        backGroundImageWidth = getBackgroundImageWidth()
        binding.imgBackground.layoutParams?.width = backGroundImageWidth
        binding.areaLayout.layoutParams?.width = backGroundImageWidth
        mobileScreenHeight =
            DensityUtils.getScreenWidth(requireContext()) - tollBarHeight.toPxFromDp(requireContext())
    }

    private fun initListeners() {
        binding.layout.imvSessionLayoutClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getIntentData() {
        sessionId = arguments?.getInt(BundleConstant.orchestraId) ?: 0
        selectedPos = arguments?.getInt(StringConstants.selectedPos) ?: 0
        isFromFav = arguments?.getBoolean(StringConstants.isFromFav, false)
        val stateManagement = arguments?.getBoolean(BundleConstant.stateManagement)
        if (stateManagement == true) {
            (requireContext() as? LandingActivity)?.shouldRefreshHome = true
        }
    }

    override fun createPresenter() = SessionLayoutPresenter()

    override fun setSessionLayout(sessionLayoutResponse: SessionBoughtStatus) {
        (requireContext() as? LandingActivity)?.sessionLayoutResponse =
            sessionLayoutResponse.sessionLayoutResponse
        this.sessionLayoutResponse = sessionLayoutResponse.sessionLayoutResponse

        val isPartBought = sessionLayoutResponse.multipartResponse?.find { it.isPartBought == true }
        val isPremiumBought =
            sessionLayoutResponse.multipartResponse?.find { it.isPremiumBought == true }

        if (isPartBought != null || isPremiumBought != null) {
            binding.layout.imvSessionLayoutClick.viewVisible()
            binding.layout.txvBoughtStatus.viewVisible()
        } else {
            binding.layout.imvSessionLayoutClick.viewGone()
            binding.layout.txvBoughtStatus.viewGone()
        }

        if (sessionLayoutResponse.sessionLayoutResponse?.instrumentResponse.isNullOrEmpty()) {
            showMessageDialog(R.string.noInstrument) {
                popUpBackStack()
            }
            return
        }

        Picasso.get().load(sessionLayoutResponse.sessionLayoutResponse?.base_image)
            .placeholder(R.drawable.ic_thumbnail).into(binding.imgBackground)

        val list = sessionLayoutResponse.sessionLayoutResponse?.instrumentResponse

        sessionLayoutResponse.sessionLayoutResponse?.instrumentResponse?.indices?.forEach { i ->
            val inflater =
                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.layout_adapter_session_layout, null)
            val backgroundView: RelativeLayout = view.findViewById(R.id.rltBackgroundLayout)
            val llSessionLayout: RelativeLayout = view.findViewById(R.id.llSessionLayout)
            val imvBoughtStatus: ImageView? = view.findViewById(R.id.imvBoughtStatus)
            llSessionLayout.x = aspectRatioX(list?.get(i)?.x?.toFloat() ?: 0f)
            llSessionLayout.y = aspectRatioY(list?.get(i)?.y?.toFloat() ?: 0f)
            partBoughtStatus = sessionLayoutResponse.multipartResponse?.find {
                it.id == list?.get(i)?.instrument_id && it.player?.id == list?.get(
                    i
                )?.musicianId
            }

            if (partBoughtStatus != null && partBoughtStatus?.isPartBought == true) {
                imvBoughtStatus.viewVisible()
            } else {
                imvBoughtStatus.viewGone()
            }

            val layoutParams = RelativeLayout.LayoutParams(

                aspectRatioX(list?.get(i)?.w?.toFloat() ?: 0f).toInt(),

                aspectRatioY(list?.get(i)?.h?.toFloat() ?: 0f).toInt()

            )
            if (BuildConfig.FLAVOR == "qa") {
                backgroundView.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.session_layout_background_square
                )
            }

            backgroundView.layoutParams = layoutParams

            backgroundView.clicks().subscribe {
                val instrument =
                    sessionLayoutResponse.sessionLayoutResponse.instrumentResponse?.get(i)
                val playerImage =
                    sessionLayoutResponse.multipartResponse?.find { instrument?.instrument_id == it.id && instrument?.musicianId == it.player?.id }?.player?.image
                partBoughtStatus =
                    sessionLayoutResponse.multipartResponse?.find { it.id == list?.get(i)?.instrument_id }
                if (instrumentDialog != null) instrumentDialog?.dismiss()
                instrument?.musician_image = partBoughtStatus?.musicianImage
                instrumentDialog = DialogUtils.showInstrumentDetailDialog(
                    requireContext(),
                    instrument,
                    playerImage,
                    {
                        if (!getLoginState()) {
                            DialogUtils.showAlertDialog(
                                requireContext(),
                                getString(R.string.please_login),
                                {
                                    StateManagement.pageName = Constants.session
                                    StateManagement.id = sessionId
                                    navigateToLogin()
                                },
                                {},
                                isCancelable = false,
                                showNegativeBtn = true,
                                getString(R.string.login)
                            )

                        } else {
                            if (isNetworkAvailable()) {
                                sessionLayoutResponse.sessionLayoutResponse.instrumentResponse?.forEach {
                                    it.isClickedInstrument = false
                                }
                                val instrumentResponse =
                                    sessionLayoutResponse.sessionLayoutResponse.instrumentResponse?.get(
                                        i
                                    )
                                instrumentResponse?.sessionId = sessionId
                                instrumentResponse?.isClickedInstrument = true
                                getInstrumentData(
                                    instrumentResponse?.instrument_id,
                                    instrument?.sessionId,
                                    instrument?.musicianId
                                )
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    getString(R.string.error_no_internet_connection),
                                    { findNavController().popBackStack() },
                                    {})
                            }
                        }
                    },
                    {},
                    true
                )
            }?.let {
                compositeDisposable?.add(it)
            }
            binding.areaLayout.addView(view)
        }
    }

    private fun getInstrumentData(instrumentId: Int?, sessionId: Int?, musicianId: Int?) {
        val videoSupport = if (VideoResolution.is4kSupported() == true) {
            ApiConstant.fourKSupport
        } else {
            ApiConstant.hdSupport
        }
        navigateToDetail()
//        presenter.getInstrumentDetail(
//            instrumentId,
//            sessionId,
//            musicianId,
//            videoSupport
//        )
    }

    override fun navigateToDetail() {
        instrumentResponse =
            sessionLayoutResponse?.instrumentResponse?.find { it.isClickedInstrument == true }
        (requireContext() as? LandingActivity)?.setSelectedInstrument(instrumentResponse)
        val bundle = bundleOf(Constants.session to instrumentResponse)
        bundle.putInt(StringConstants.selectedPos, selectedPos ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, true)
        Router.navigateFragmentWithData(findNavController(), R.id.sessionDetailFragment, bundle)
    }

    override fun navigateToPlayerDetail(musicianId: Int?) {
        val bundle = bundleOf(BundleConstant.orchestraId to musicianId)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.playerDetailFragment, bundle)
    }

    override fun showDialogBox() {
        dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(R.layout.custom_dialog_sessionlayout)
        dialog?.setCancelable(true)
        dialog?.show()
    }

    override fun popUpBackStack() {
        findNavController().popBackStack()
    }

    override fun onPause() {
        dialog?.dismiss()
        super.onPause()
    }

    override fun onDestroy() {
        dialog?.dismiss()
        super.onDestroy()
    }

    private fun getBackgroundImageWidth(): Int {
        return (DensityUtils.getScreenWidth(requireContext()) - tollBarHeight.toPxFromDp(
            requireContext()
        )).div(
            getImageRatio()
        ).toInt()
    }

    private fun getImageRatio(): Float {
        return 300 / 500f
    }

    private fun aspectRatioY(y: Float): Float {
        return (y.toPxFromDp(requireContext()) * (mobileScreenHeight ?: 0)).div(
            screenHeight.toPxFromDp(requireContext()).toFloat()
        )
    }

    private fun aspectRatioX(x: Float): Float {
        return (x.toPxFromDp(requireContext()) * (backGroundImageWidth ?: 0)).div(
            screenWidth.toPxFromDp(requireContext()).toFloat()
        )
    }

}