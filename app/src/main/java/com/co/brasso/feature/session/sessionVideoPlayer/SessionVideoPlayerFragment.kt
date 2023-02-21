package com.co.brasso.feature.session.sessionVideoPlayer

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.databinding.ActivitySessionVideoPlayerBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.SettingsOptionItem
import com.co.brasso.feature.shared.model.recording.RecordingEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCart
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.NetworkUtils
import com.co.brasso.utils.util.PreferenceUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.video.spherical.SphericalGLSurfaceView
import com.google.gson.Gson
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SessionVideoPlayerFragment :
    BaseFragment<SessionVideoPlayerView, SessionVideoPlayerPresenter>(), SessionVideoPlayerView {

    private var player: ExoPlayer? = null
    private var premiumPlayer: ExoPlayer? = null
    private var appendixPlayer: ExoPlayer? = null
    private lateinit var binding: ActivitySessionVideoPlayerBinding
    private var playWhenReady = true
    private var path: String? = null
    private var instrumentDetailResponse: InstrumentDetailResponse? = null
    private var recPath: File? = null
    private lateinit var requestMicPermissionLauncher: ActivityResultLauncher<String>
    private var mediaRecorder: MediaRecorder? = null
    private var countdownTimer: CountDownTimer? = null
    private var isFirst = true
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var option: String? = null
    private var settingOption = SettingsOptionItem(0, 0, 0, 0, 0)
    var purchasedItemsList: MutableList<CartListResponse> = mutableListOf()
    var isPurchaseUpdatedFromMultiPart: Boolean = false
    private var audioManager: AudioManager? = null

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val keyType = intent?.getStringExtra(Constants.keyType)
            if (keyType.equals(getString(R.string.keyUp))) {
                initVolumeUp()
            } else if (keyType.equals(getString(R.string.keyDown))) {
                initVolumeDown()
            }
        }
    }

    private fun initVolumeDown() {
        audioManager?.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
    }

    private fun initVolumeUp() {
        audioManager?.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = ActivitySessionVideoPlayerBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenLandScape()
        initMicPermission()
        if (isFirst) {
            instrumentDetailResponse =
                arguments?.getSerializable(Constants.videoUrl) as? InstrumentDetailResponse
            if (instrumentDetailResponse?.isDownloaded == true) {
                setup()
            } else {
                startStreaming()
            }
            isFirst = false
        }
        initToolbar()
    }

    private fun initReceiver() {
        val filter = IntentFilter().apply { addAction(Constants.volumeControl) }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
    }

    private var purchaseCompleteBoardCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            val sessionId = intent?.getStringExtra(BundleConstant.sessionId)
            if (sessionId == instrumentDetailResponse?.hallSoundDetail?.sessionId.toString()) {
                isPurchaseUpdatedFromMultiPart = true
            }
        }
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.hideToolbar()
        (activity as? LandingActivity)?.hideBottomBar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setup() {
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        getVideoFile()
        initListeners()
        initReceiver()
        getPreference()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            purchaseCompleteBoardCast,
            IntentFilter(StringConstants.sessionVideoPlayerBroadCastAction)
        )
        (binding.videoPlayer.videoSurfaceView as SphericalGLSurfaceView)
            .setDefaultStereoMode(C.STEREO_MODE_MONO)
        (binding.premiumVideoPlayer.videoSurfaceView as SphericalGLSurfaceView)
            .setDefaultStereoMode(C.STEREO_MODE_MONO)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStreaming() {

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showMessageDialog(R.string.error_no_internet_connection)
            return
        }

        if (!validateStreamNetworkStatus()) {
            return
        }

        if (!PreferenceUtils.getStreamOnWifi(requireContext()) && PreferenceUtils.getNotifyingWhenMobileData(
                requireContext()
            ) && !NetworkUtils.isWIFINetworkAvailable(requireContext())
        ) {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.text_cellular_stream_warning),
                {
                    setup()
                },
                { findNavController().popBackStack() },
                isCancelable = false,
                showNegativeBtn = true
            )
        } else {
            setup()
        }
    }

    private fun backPress() {
        findNavController().popBackStack()
    }

    private fun validateStreamNetworkStatus(): Boolean {
        if (PreferenceUtils.getStreamOnWifi(requireContext()) && !NetworkUtils.isWIFINetworkAvailable(
                requireContext()
            )
        ) {
            showMessageDialog(getString(R.string.text_wifi_stream_only)) { backPress() }
            return false
        }
        return true
    }

    private fun getPreference() {
        option = PreferenceUtils.getFileTypePosition(requireContext())
        if (!option.isNullOrEmpty()) {
            settingOption = Gson().fromJson(option, SettingsOptionItem::class.java)
        }
    }

    private fun restrictPartVideoRotation() {
        (binding.videoPlayer.videoSurfaceView as SphericalGLSurfaceView).setUseSensorRotation(false)
        (binding.videoPlayer.videoSurfaceView as SphericalGLSurfaceView).isEnabled = false
    }

    private fun enablePartVideoRotation() {
        (binding.videoPlayer.videoSurfaceView as SphericalGLSurfaceView).setUseSensorRotation(true)
        (binding.videoPlayer.videoSurfaceView as SphericalGLSurfaceView).isEnabled = true
    }

    private fun enablePremiumVideoRotation() {
        (binding.premiumVideoPlayer.videoSurfaceView as SphericalGLSurfaceView).setUseSensorRotation(
            true
        )
        (binding.premiumVideoPlayer.videoSurfaceView as SphericalGLSurfaceView).isEnabled = true
    }

    private fun restrictPremiumVideoRotation() {
        (binding.premiumVideoPlayer.videoSurfaceView as SphericalGLSurfaceView).setUseSensorRotation(
            false
        )
        (binding.premiumVideoPlayer.videoSurfaceView as SphericalGLSurfaceView).isEnabled = false
    }

    private fun purchaseValidation(boughtProducts: BoughtProducts?) {
        val cartItem =
            AppInfoGlobal.cartInfo?.find { it.orchestraId == instrumentDetailResponse?.hallSoundDetail?.sessionId && it.sessionType == instrumentDetailResponse?.type && it.musicianId == instrumentDetailResponse?.hallSoundDetail?.musicianId }
        boughtProducts?.cartItems = mutableListOf()
        if (cartItem != null)
            boughtProducts?.cartItems?.add(cartItem)
        presenter.purchaseVerify(boughtProducts)
    }

    private fun updateSessionDetail() {
        val action = if (instrumentDetailResponse?.isFormSession == true) {
            StringConstants.sessionBroadCastAction
        } else if (instrumentDetailResponse?.isFromPremiumPage == true) {
            StringConstants.sessionPremiumBroadCastAction
        } else {
            StringConstants.sessionAppendixVideoBroadCastAction
        }
        val intent = Intent()
        intent.action = action
        intent.putExtra(
            BundleConstant.sessionId,
            instrumentDetailResponse?.hallSoundDetail?.sessionId.toString()
        )
        intent.putExtra(
            BundleConstant.session,
            instrumentDetailResponse?.type
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun initMicPermission() {
        requestMicPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    DialogUtils.showRecordDialog(requireContext(), { startCountDown() }, false)
                } else {
                    DialogUtils.showAlertDialog(
                        requireContext(),
                        getString(R.string.text_mic_permission_denied),
                        {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO))
                                requestMicPermission()
                            else
                                openAppSettings()
                        }, {

                        }, isCancelable = false, showNegativeBtn = true
                    )
                }
            }
    }

    private fun openAppSettings() {
        try {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        } catch (e: Exception) {
        }
    }

    private fun getVideoFile() {
        if (instrumentDetailResponse?.type.equals(Constants.part)) {
            binding.videoPlayer.viewVisible()
            binding.premiumVideoPlayer.viewGone()
            binding.premiumVideoAppendixPlayer.viewGone()
            setNormalData()
            initializePlayer()
        } else {
            if (instrumentDetailResponse?.isFromAppendixVideo == false) {
                binding.videoPlayer.viewGone()
                binding.premiumVideoPlayer.viewVisible()
                binding.premiumVideoAppendixPlayer.viewGone()
                setPremiumData()
                initializePremiumPlayer()
            } else {
                binding.videoPlayer.viewGone()
                binding.premiumVideoPlayer.viewGone()
                binding.premiumVideoAppendixPlayer.viewVisible()
                initializeAppendixVideo()
                setAppendixVideo()
            }
        }
        checkIsBrought(instrumentDetailResponse)
    }

    private fun setNormalData() {
        if (instrumentDetailResponse?.hallSoundDetail?.title.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtEngTitle).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtEngTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.title
        }

        if (instrumentDetailResponse?.hallSoundDetail?.titleJp.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtJpnTitle).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtJpnTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.titleJp
        }

        if (instrumentDetailResponse?.name.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtInstrument).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtInstrument).text =
                instrumentDetailResponse?.name
        }

        if (instrumentDetailResponse?.hallSoundDetail?.businessType.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtBusinessType).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtBusinessType).text =
                instrumentDetailResponse?.hallSoundDetail?.businessType
        }
    }

    private fun setPremiumData() {
        if (instrumentDetailResponse?.hallSoundDetail?.title.isNullOrEmpty()) {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtEngTitle).viewGone()
        } else {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtEngTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.title
        }

        if (instrumentDetailResponse?.hallSoundDetail?.titleJp.isNullOrEmpty()) {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtJpnTitle).viewGone()
        } else {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtJpnTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.titleJp
        }

        if (instrumentDetailResponse?.name.isNullOrEmpty()) {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtInstrument).viewGone()
        } else {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtInstrument).text =
                instrumentDetailResponse?.name
        }

        if (instrumentDetailResponse?.hallSoundDetail?.businessType.isNullOrEmpty()) {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtBusinessType).viewGone()
        } else {
            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtBusinessType).text =
                instrumentDetailResponse?.hallSoundDetail?.businessType
        }
    }

    private fun setAppendixVideo() {
        if (instrumentDetailResponse?.hallSoundDetail?.title.isNullOrEmpty()) {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtEngTitle).viewGone()
        } else {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtEngTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.title
        }

        if (instrumentDetailResponse?.hallSoundDetail?.titleJp.isNullOrEmpty()) {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtJpnTitle).viewGone()
        } else {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtJpnTitle).text =
                instrumentDetailResponse?.hallSoundDetail?.titleJp
        }

        if (instrumentDetailResponse?.name.isNullOrEmpty()) {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtInstrument).viewGone()
        } else {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtInstrument).text =
                instrumentDetailResponse?.name
        }

        if (instrumentDetailResponse?.hallSoundDetail?.businessType.isNullOrEmpty()) {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtBusinessType)
                .viewGone()
        } else {
            binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtBusinessType).text =
                instrumentDetailResponse?.hallSoundDetail?.businessType
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord).setOnClickListener {
            if (mediaRecorder != null) {
                showStopRecordingDialog()
            } else {
                showRecordingDialog()
            }
        }

        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord).setOnClickListener {
            if (mediaRecorder != null) {
                showStopRecordingDialog()
            } else {
                showRecordingDialog()
            }
        }

        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord)
            .setOnClickListener {
                if (mediaRecorder != null) {
                    showStopRecordingDialog()
                } else {
                    showRecordingDialog()
                }
            }

        binding.videoPlayer.findViewById<TextView>(R.id.txtBuy).setOnClickListener {
            showBuyDialog()
        }

        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtBuy).setOnClickListener {
            showPremiumDialog()
        }

        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtBuy).setOnClickListener {
            showPremiumDialog()
        }

        binding.videoPlayer.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
            stopRecording()
            stopPlayer()
            findNavController().popBackStack()
        }

        binding.premiumVideoPlayer.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
            stopRecording()
            stopPlayer()
            findNavController().popBackStack()
        }

        binding.premiumVideoAppendixPlayer.findViewById<ImageView>(R.id.imgClose)
            .setOnClickListener {
                stopRecording()
                stopPlayer()
                findNavController().popBackStack()
            }

        player?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (player?.playbackState == ExoPlayer.STATE_ENDED) {
                    player?.seekTo(0)
                    player?.pause()
                    //showStopRecordingDialog()
                }

//                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
//                    startRecorder(binding.videoPlayer)
//                } else if (playWhenReady) {
//                    startRecorder(binding.videoPlayer)
//                } else {
//                    pauseRecorder(binding.videoPlayer)
//                }
            }
        })

        premiumPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (premiumPlayer?.playbackState == ExoPlayer.STATE_ENDED) {
                    premiumPlayer?.seekTo(0)
                    premiumPlayer?.pause()
                  //  showStopRecordingDialog()
                }

//                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
//                    startRecorder(binding.premiumVideoPlayer)
//                } else if (playWhenReady) {
//                    startRecorder(binding.premiumVideoPlayer)
//                } else {
//                    pauseRecorder(binding.premiumVideoPlayer)
//                }
            }
        })

        appendixPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (appendixPlayer?.playbackState == ExoPlayer.STATE_ENDED) {
                    appendixPlayer?.seekTo(0)
                    appendixPlayer?.pause()
                   // showStopRecordingDialog()
                }

                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
                    startRecorder(binding.premiumVideoAppendixPlayer)
                } else if (playWhenReady) {
                    startRecorder(binding.premiumVideoAppendixPlayer)
                } else {
                    pauseRecorder(binding.premiumVideoAppendixPlayer)
                }
            }
        })
    }

    private fun stopPlayer() {
        releasePlayer()
        releasePremiumPlayer()
        releaseAppendixPlayer()
        releaseRecorder()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showStopRecordingDialog() {
        player?.pause()
        premiumPlayer?.pause()
        appendixPlayer?.pause()
        DialogUtils.showStopRecordDialog(
            requireContext(),
            true,
            { saveRecordingFile() },
            { stopRecording() },
            { DialogUtils.showRecordDialog(requireContext(), { startCountDown() }, false) })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveRecordingFile() {
        presenter.saveRecordingFile(getUploadData())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUploadData(): RecordingEntity {
        val durationInMillis = (endTime?.minus(startTime ?: 0))
        val durationInSeconds = durationInMillis?.div(1000)?.toString()
        val orchestraId = instrumentDetailResponse?.hallSoundDetail?.id.toString()
        val title = instrumentDetailResponse?.hallSoundDetail?.title
        val calendar = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val formatted = calendar.format(formatter)
        val recordingFile = recPath
        return RecordingEntity(
            orchestraId = orchestraId,
            title = title,
            duration = durationInSeconds,
            recordingDate = formatted,
            recordingFile = recordingFile
        )
    }

    private fun showPremiumDialog() {
        DialogUtils.showPremiumVideoBuyDialog(
            requireContext(),
            true,
            instrumentDetailResponse, {
                proceedToBuy()
            }, {
                proceedToAddToCart()
            }, {
                proceedToAppendix()
            })
    }

    private fun showBuyDialog() {
        DialogUtils.showVideoBuyDialog(
            requireContext(),
            true,
            instrumentDetailResponse,
            {
                proceedToBuy()
            },
            {
                proceedToAddToCart()
            }, {
                proceedToPremium()
            }, {
                proceedToMultiPart()
            })
    }

    private fun proceedToMultiPart() {
        val hallSoundDetail = instrumentDetailResponse?.hallSoundDetail
        hallSoundDetail?.isFromVideoPlayer = true
        val bundle = bundleOf(Constants.session to hallSoundDetail)
        Router.navigateFragmentWithData(findNavController(), R.id.buyMultiPartFragment, bundle)
    }

    private fun proceedToPremium() {
        stopRecording()
        player?.release()
        instrumentDetailResponse?.type = checkPurchaseAndCartType()
        checkIsBrought(instrumentDetailResponse)
        getVideoFile()
    }

    private fun proceedToAppendix() {
        stopRecording()
        premiumPlayer?.release()
        instrumentDetailResponse?.type = checkPurchaseAndCartType()
        instrumentDetailResponse?.isFromAppendixVideo = true
        getVideoFile()
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

    private fun showRecordingDialog() {
        checkMicPermission()
    }

    private fun proceedToAddToCart() {
        val cartItems: MutableList<AddToCart> = mutableListOf()
        cartItems.add(
            AddToCart(
                orchestraId = instrumentDetailResponse?.hallSoundDetail?.sessionId,
                type = ApiConstant.session,
                sessionType = instrumentDetailResponse?.type,
                price = getPrice(),
                musicianId = instrumentDetailResponse?.playerDetail?.id,
                identifier = getIdentifier()
            )
        )
        presenter.addToCart(
            AddToCartList(cartItems)
        )
    }

    private fun proceedToBuy() {
        val cartItem =
            AppInfoGlobal.cartInfo?.find {
                it.orchestraId == instrumentDetailResponse?.hallSoundDetail?.sessionId && it.sessionType == instrumentDetailResponse?.type && it.musicianId == instrumentDetailResponse?.hallSoundDetail?.musicianId
                        && it.instrumentId == instrumentDetailResponse?.instrumentId
            }
        if (cartItem != null)
            purchasedItemsList.add(
                cartItem
            )
        else
            purchasedItemsList.add(
                CartListResponse(
                    orchestraId = instrumentDetailResponse?.hallSoundDetail?.sessionId,
                    type = ApiConstant.session,
                    sessionType = instrumentDetailResponse?.type,
                    musicianId = instrumentDetailResponse?.hallSoundDetail?.musicianId,
                    instrumentId = instrumentDetailResponse?.instrumentId,
                    price = getPrice(),
                    identifier = getIdentifier()
                )
            )

        presenter.buyOrchestra(purchasedItemsList)
    }

    private fun getPrice(): Double? {
        return when (instrumentDetailResponse?.type) {
            Constants.part -> {
                instrumentDetailResponse?.partPrice
            }
            Constants.premium -> {
                instrumentDetailResponse?.premiumVideoPrice
            }
            else -> {
                instrumentDetailResponse?.comboPrice
            }
        }

    }

    private fun getIdentifier(): String? {
        return when (instrumentDetailResponse?.type) {
            Constants.part -> {
                instrumentDetailResponse?.partIdentifier
            }
            Constants.premium -> {
                instrumentDetailResponse?.premiumVideoIdentifier
            }
            else -> {
                instrumentDetailResponse?.comboVideoIdentifier
            }
        }

    }

    private fun checkMicPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            DialogUtils.showRecordDialog(requireContext(), { startCountDown() }, false)
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.text_mic_permission_denied),
                {
                    requestMicPermission()
                },
                {},
                isCancelable = true,
                showNegativeBtn = true
            )
        } else {
            requestMicPermission()
        }
    }

    private fun requestMicPermission() {
        requestMicPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun startCountDown() {
        DialogUtils.showCountDownDialog(
            requireContext(),
            true
        ) { startRecording() }
    }

    private fun startRecording() {
        val fileType =
            resources.getStringArray(R.array.fileType)[settingOption.fileTypePosition]

        val samplingRate =
            resources.getStringArray(R.array.samplingRate)[settingOption.samplingRatePosition].replace(
                ",",
                ""
            ).substringBefore(
                "Hz"
            ).toInt()

        val bitrate =
            resources.getStringArray(R.array.bitRate)[settingOption.bitRatePosition].substringBefore(
                "kbps"
            ).toInt() * 1000

        if (instrumentDetailResponse?.type.equals(Constants.part)) {
            player?.seekTo(0)
            player?.play()
        } else {
            if (instrumentDetailResponse?.isFromAppendixVideo == false) {
                premiumPlayer?.seekTo(0)
                premiumPlayer?.play()
            } else {
                appendixPlayer?.seekTo(0)
                appendixPlayer?.pause()
            }
        }
        val sdcard: String =
            ContextWrapper(requireContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?.absolutePath.toString() + instrumentDetailResponse?.name.toString()+ "."+ fileType.lowercase()

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setAudioEncodingBitRate(bitrate)
        mediaRecorder?.setAudioSamplingRate(samplingRate)
        mediaRecorder?.setOutputFile(sdcard)
        mediaRecorder?.prepare()
        mediaRecorder?.start()

        recPath = File(sdcard)

//        binding.videoPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewVisible()
//        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewVisible()
//        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewVisible()

        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.suspend)
        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.suspend)
        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.suspend)

        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_record, 0, 0, 0)
        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_record, 0, 0, 0)
        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_record, 0, 0, 0)

        startTime = System.currentTimeMillis()

    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

//        binding.videoPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewGone()
//        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewGone()
//        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecordIcon).viewGone()

        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.record)
        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.record)
        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord).text =
            getString(R.string.record)

        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic, 0, 0, 0)
        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic, 0, 0, 0)
        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord)
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic, 0, 0, 0)

        endTime = System.currentTimeMillis()

    }

    private fun initializePlayer() {
        path = instrumentDetailResponse?.vrFile
        if (path.isNullOrEmpty()) return
        player = ExoPlayer.Builder(requireContext())
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
            .also { exoPlayer ->
                binding.videoPlayer.player = exoPlayer
                val uri = Uri.parse(path)
                val mediaSource = buildMediaSource(uri)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = playWhenReady
                binding.videoPlayer.onResume()
            }

        player?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == ExoPlayer.STATE_READY) {
                    if (instrumentDetailResponse?.isPartBought == false) {
                        initSamplePlayer(player, Constants.part)
                    }
                } else {
                    countdownTimer?.cancel()
                }
            }
        })

        player?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    stopRecording()
//                }
            }
        })
    }

    private fun initializePremiumPlayer() {
        path = instrumentDetailResponse?.vrFile
        if (path.isNullOrEmpty()) return
        premiumPlayer = ExoPlayer.Builder(requireContext())
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
            .also { exoPlayer ->
                binding.premiumVideoPlayer.player = exoPlayer
                val uri = Uri.parse(path)
                val mediaSource = buildMediaSource(uri)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = playWhenReady
                binding.premiumVideoPlayer.onResume()
            }

        premiumPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == ExoPlayer.STATE_READY) {
                    if (instrumentDetailResponse?.isPremiumBought == false && instrumentDetailResponse?.isPartBought == false) {
                        initSamplePlayer(premiumPlayer, Constants.premium)
                    }
                } else {
                    countdownTimer?.cancel()
                }
            }
        })

        premiumPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    stopRecording()
//                }
            }
        })
    }

    private fun initializeAppendixVideo() {
        path = instrumentDetailResponse?.premiumFile
        if (path.isNullOrEmpty()) return
        appendixPlayer = ExoPlayer.Builder(requireContext())
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
            .also { exoPlayer ->
                binding.premiumVideoAppendixPlayer.player = exoPlayer
                val uri = Uri.parse(path)
                val mediaSource = buildMediaSource(uri)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = playWhenReady
                binding.premiumVideoAppendixPlayer.onResume()
            }

        appendixPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == ExoPlayer.STATE_READY) {
                    if (instrumentDetailResponse?.isPremiumBought == false) {
                        initSamplePlayer(appendixPlayer, Constants.appendix)
                    }
                } else {
                    countdownTimer?.cancel()
                }
            }
        })

        appendixPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    stopRecording()
//                }
            }
        })

    }

    private fun initSamplePlayer(player: ExoPlayer?, type: String?) {
        if (type == Constants.part || type == Constants.premium) {
            if (((player?.currentPosition?.div(1000)) ?: 0L) <= 45L) {
                val totalTime = 45000L - (player?.currentPosition ?: 0L)
                if (countdownTimer != null)
                    countdownTimer?.cancel()
                countdownTimer = object : CountDownTimer(totalTime, 1000) {
                    override fun onFinish() {
                        seekToZero(player)
                    }

                    override fun onTick(sec: Long) {
                        val playerPosition = (player?.currentPosition?.div(1000)) ?: 0L
                        if (playerPosition >= sec) {
                            seekToZero(player)
                        }
                    }
                }
                countdownTimer?.start()
            } else {
                seekToZero(player)
            }
        } else {
            if (((player?.currentPosition?.div(1000)) ?: 0L) <= 45L) {
                val totalTime = 45000L - (player?.currentPosition ?: 0L)
                if (countdownTimer != null)
                    countdownTimer?.cancel()
                countdownTimer = object : CountDownTimer(totalTime, 1000) {
                    override fun onFinish() {
                        seekToZero(player)
                    }

                    override fun onTick(sec: Long) {
                        val playerPosition = (player?.currentPosition?.div(1000)) ?: 0L
                        if (playerPosition >= sec) {
                            seekToZero(player)
                        }
                    }
                }
                countdownTimer?.start()
            } else {
                seekToZero(player)
            }
        }
    }

    fun seekToZero(player: ExoPlayer?) {
        player?.seekTo(0)
        player?.pause()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    override fun createPresenter() = SessionVideoPlayerPresenter()

    override fun success(message: String?) {
        showMessage(message)
    }

    override fun addToCartSuccess(cartListResponse: List<CartListResponse>?) {
        AppInfoGlobal.cartInfo = mutableListOf()
        AppInfoGlobal.cartInfo?.addAll(cartListResponse ?: listOf())
    }

    override fun purchaseSuccess(
        message: String?
    ) {
        changePurchaseStatus()
        if (countdownTimer != null) {
            countdownTimer?.cancel()
            countdownTimer = null
        }
        checkIsBrought(instrumentDetailResponse)
        AppInfoGlobal.cartInfo?.removeIf { it.orchestraId == instrumentDetailResponse?.hallSoundDetail?.sessionId && it.musicianId == instrumentDetailResponse?.hallSoundDetail?.musicianId && it.sessionType == instrumentDetailResponse?.type }
        updateSessionDetail()
        purchasedItemsList = mutableListOf()
        if (instrumentDetailResponse?.type == Constants.part) {
            showMessageDialog(getString(R.string.to_play_part_download), false){
                  closePlayerPage()
            }
        }
    }

    private fun closePlayerPage() {
        stopRecording()
        stopPlayer()
        findNavController().popBackStack()
    }

    private fun changePurchaseStatus() {
        when (instrumentDetailResponse?.type) {
            Constants.part -> {
                instrumentDetailResponse?.isPartBought = true
            }
            else -> {
                instrumentDetailResponse?.isPremiumBought = true
            }
        }
    }

    private fun checkIsBrought(instrumentDetailResponse: InstrumentDetailResponse?) {

        when (instrumentDetailResponse?.type) {
            Constants.part -> {
                if (instrumentDetailResponse.isPartBought == false) {
//                    binding.videoPlayer.findViewById<TextView>(R.id.txtRecord).viewGone()
                    binding.videoPlayer.findViewById<TextView>(R.id.txtSampleTitle).viewVisible()
                    binding.videoPlayer.findViewById<DefaultTimeBar>(
                        R.id.exo_progress
                    ).isEnabled =
                        false
                    restrictPartVideoRotation()
                } else {
                    if (instrumentDetailResponse.isPremiumBought == true) {
//                        binding.videoPlayer.findViewById<TextView>(R.id.txtRecord)
//                            .viewVisible()
                        binding.videoPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                            .viewGone()
                        binding.videoPlayer.findViewById<TextView>(R.id.txtBuy).viewGone()
                        enablePartVideoRotation()
                    } else {
                        binding.videoPlayer.findViewById<TextView>(R.id.txtBuy).viewGone()
                        restrictPartVideoRotation()
                    }
//                    binding.videoPlayer.findViewById<TextView>(R.id.txtRecord).viewVisible()
                    binding.videoPlayer.findViewById<TextView>(R.id.txtSampleTitle).viewGone()
                    binding.videoPlayer.findViewById<TextView>(R.id.txtBuy)?.text =
                        StringConstants.buyPremium
                }
            }
            else -> {
                if (instrumentDetailResponse?.isFromAppendixVideo == false) {
                    if (instrumentDetailResponse.isPremiumBought == false) {
                        if (instrumentDetailResponse.isPartBought == true) {
//                            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord)
//                                .viewVisible()
                            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                                .viewGone()
                        } else {
//                            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord)
//                                .viewGone()
                            binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                                .viewVisible()
                        }
                        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtBuy).viewGone()
                        restrictPremiumVideoRotation()
                    } else {
//                        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtRecord)
//                            .viewVisible()
                        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                            .viewGone()
                        binding.premiumVideoPlayer.findViewById<TextView>(R.id.txtBuy).viewGone()
                        enablePremiumVideoRotation()
                    }
                } else {
                    if (instrumentDetailResponse?.isPremiumBought == false) {
                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                            .viewVisible()
                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtBuy)
                            .viewGone()
//                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord)
//                            .viewGone()
                    } else {
                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtSampleTitle)
                            .viewGone()
                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtBuy)
                            .viewGone()
//                        binding.premiumVideoAppendixPlayer.findViewById<TextView>(R.id.txtRecord)
//                            .viewVisible()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        pausePremiumPlayer()
        pauseAppendixPlayer()
        pauseRecorder(binding.videoPlayer)
    }

    override fun onResume() {
        super.onResume()
        if (isPurchaseUpdatedFromMultiPart) {
            purchaseSuccess(StringConstants.emptyString)
            isPurchaseUpdatedFromMultiPart = false
        }
        setScreenLandScape()
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
        pausePremiumPlayer()
        pauseAppendixPlayer()
        pauseRecorder(binding.videoPlayer)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
        releasePlayer()
        releasePremiumPlayer()
        releaseAppendixPlayer()
        releaseRecorder()
    }

    private fun releasePlayer() {
        player?.stop()
        player?.release()
        player = null
    }

    private fun pausePlayer() {
        player?.pause()
    }

    private fun startRecorder(videoPlayer: PlayerView) {
        if (mediaRecorder != null) {
            videoPlayer.findViewById<TextView>(R.id.txtRecordIcon).text =
                getString(R.string.text_rec)
            mediaRecorder?.resume()
        }
    }

    private fun releaseRecorder() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun pauseRecorder(videoPlayer: PlayerView) {
        videoPlayer.findViewById<TextView>(R.id.txtRecordIcon).text =
            getString(R.string.pause)
        mediaRecorder?.pause()
    }

    private fun releasePremiumPlayer() {
        premiumPlayer?.stop()
        premiumPlayer?.release()
        premiumPlayer = null
    }

    private fun pausePremiumPlayer() {
        premiumPlayer?.pause()
    }

    private fun releaseAppendixPlayer() {
        appendixPlayer?.stop()
        appendixPlayer?.release()
        appendixPlayer = null
    }

    private fun pauseAppendixPlayer() {
        appendixPlayer?.pause()
    }

}