package com.co.brasso.feature.vlcPlayer

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ScaleGestureDetectorCompat
import com.co.brasso.databinding.ActivityVlcPlayerBinding
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.extension.*
import com.jakewharton.rxbinding3.view.clicks
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.io.IOException
import kotlin.math.abs


class VLCPlayerActivity : AppCompatActivity(), MediaPlayer.EventListener {

    var orchestra: HallSoundResponse? = null
    private val useTextureView = true
    private val enableSubtitles = true
    private val minFov = 20f
    private val maxFov = 80f
    private val touchNone = 0
    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: MediaPlayer? = null
    var fov: Float = 80.toFloat()
    private var yaw = 0f
    private var pitch = 0f
    private val defaultFov = 80f
    private var touchY = -1f
    private var touchX = -1f
    private var initTouchY = 0f
    private var initTouchX = 0f
    private var touchAction = touchNone
    var yRange = 0
    var xRange = 0
    private val touchIgnore = 5
    private val touchMove = 3
    private var verticalTouchActive = false
    private var totalYawAngle = 0f
    private var totalPitchAngle = 0f
    private var restrictionAngleY = 60f
    var numberOfTaps = 0
    var lastTapTimeMs: Long = 0L
    var touchDownMs: Long = 0
    var handler = Handler(Looper.getMainLooper())
    private val noLengthProgressMax = 1000
    private var dragging: Boolean = false
    private lateinit var binding: ActivityVlcPlayerBinding
    private var isControllerVisible: Boolean = false
    private var dm = DisplayMetrics()
    private var videoDuration: String? = null
    private val maxFovNoAngleRestriction = 150f


    private val scaleGestureDetector by lazy(LazyThreadSafetyMode.NONE) {
        ScaleGestureDetector(
            this, mScaleListener
        ).apply { ScaleGestureDetectorCompat.setQuickScaleEnabled(this, false) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVlcPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        getIntentData()
        setData()
        initDensityPixel()
        initVideoPlayer()
        initListener()
        initUI()
        hideNavigation()
    }

    private fun hideNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(
                WindowInsets.Type.navigationBars()
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    override fun onResume() {
        overridePendingTransition(0, 0)
        super.onResume()
    }

    private fun initUI() {
        binding.root.run { keepScreenOn = true }
    }

    private fun showHideMediaController() {
        isControllerVisible = if (isControllerVisible) {
            binding.customPlayer.rllMediaController.viewGone()
            false
        } else {
            binding.customPlayer.rllMediaController.viewVisible()
            true
        }
    }

    fun restrictDurationValue(): Long {
        return orchestra?.playDuration?.times(1000L) ?: 0L
    }

    fun shouldLimitPlayTimeAndAngleRestrict(): Boolean {
        return orchestra?.isBoughtForUnity != true
    }

    private fun setData() {
        binding.customPlayer.txtEngTitle.text = orchestra?.title
        binding.customPlayer.txtJpnTitle.text = orchestra?.titleJp
        if (orchestra?.isPremium == true) {
            binding.customPlayer.txtPremium.viewVisible()
        } else {
            binding.customPlayer.txtPremium.viewGone()
        }

        if (!orchestra?.instrumentName.isNullOrEmpty()) {
            binding.customPlayer.txtInstrument.viewVisible()
            binding.customPlayer.txtInstrument.text = orchestra?.instrumentName
        } else {
            binding.customPlayer.txtInstrument.viewGone()
        }

        if (!orchestra?.businessType.isNullOrEmpty()) {
            binding.customPlayer.txtBusinessType.viewVisible()
            binding.customPlayer.txtBusinessType.text = orchestra?.businessType
        } else {
            binding.customPlayer.txtBusinessType.viewGone()
        }

        if (showSample()) binding.customPlayer.txtSampleTitle.viewVisible()
        else binding.customPlayer.txtSampleTitle.viewGone()
    }

    private fun showSample(): Boolean {
        return if (orchestra?.isBoughtForUnity == true) false
        else if (orchestra?.isPremium == true) {
            orchestra?.playDuration != 0
        } else orchestra?.playDuration != 0
    }

    private val seekListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            dragging = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            dragging = false
            if (shouldLimitPlayTimeAndAngleRestrict()) {
                if (restrictDurationValue() != 0L && seekBar.progress > restrictDurationValue()) {
                    restartPlayer()
                    return
                }
            }
            seek(seekBar.progress.toLong())
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!isFinishing && fromUser) {
                setProgressValue(
                    binding.customPlayer.vlcPosition, progress.toLong()
                )
            }
        }
    }

    private fun setProgressValue(vlcProgress: TextView, progress: Long) {
        vlcProgress.text = millisToString(
            progress, text = false, seconds = true, large = false
        )
    }

    private fun seek(time: Long, length: Long? = mMediaPlayer?.length) {
        mMediaPlayer?.time = time
    }

    private fun initListener() {
        binding.customPlayer.imgClose.setOnClickListener {
            finish()
        }
        binding.customPlayer.vlcPlay.setOnClickListener {
            playPause()
        }

        binding.customPlayer.vlcPause.setOnClickListener {
            playPause()
        }

        binding.customPlayer.vlcFfwd.clicks().throttle(400)?.subscribe {
            rewindAndForward(mMediaPlayer?.time?.plus(10000L) ?: 0L)
        }?.let {}

        binding.customPlayer.vlcRew.clicks().throttle(400)?.subscribe {
            rewindAndForward(mMediaPlayer?.time?.minus(10000L) ?: 0L)
        }?.let { }

        mMediaPlayer?.setEventListener(this)
        binding.customPlayer.vlcProgress.setOnSeekBarChangeListener(seekListener)
    }

    private fun rewindAndForward(time: Long?) {
        if ((time ?: 0L) >= 0L) {
            if (shouldLimitPlayTimeAndAngleRestrict()) {
                if (restrictDurationValue() != 0L && (time ?: 0) > restrictDurationValue()) {
                    restartPlayer()
                    return
                }
            }

            if ((time ?: 0L) > (mMediaPlayer?.length ?: 0L)) return
            if ((time ?: 0L) <= 10000L) {
                mMediaPlayer?.time = 0L
            } else {
                mMediaPlayer?.time = time ?: 0L

            }
            setProgressBarValue(time ?: 0L)
            Log.d("jpDragValueForward", time.toString())
            setProgressValue(binding.customPlayer.vlcPosition, time ?: 0L)
        } else {
            mMediaPlayer?.time = 0L
            setProgressBarValue(0L)
            setProgressValue(binding.customPlayer.vlcPosition, 0L)
        }

    }


    private fun playPause() {
        if (mMediaPlayer?.isPlaying == true) {
            pauseVideo()
        } else {
            mMediaPlayer?.play()
            binding.customPlayer.vlcPlay.viewGone()
            binding.customPlayer.vlcPause.viewVisible()
        }
    }

    private fun initDensityPixel() {
        dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        yRange = dm.widthPixels.coerceAtMost(dm.heightPixels)
        xRange = dm.widthPixels.coerceAtLeast(dm.heightPixels)
    }

    private fun getIntentData() {
        orchestra = intent?.getSerializableExtra(BundleConstant.orchestraId) as HallSoundResponse?
    }

    private val mScaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private var savedScale: MediaPlayer.ScaleType? = null

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return xRange != 0 || fov == 0f
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val diff = defaultFov * (1 - detector.scaleFactor)
            if (mMediaPlayer?.updateViewpoint(
                    totalYawAngle, totalPitchAngle, 0f, fov, true
                ) == true
            ) {
                fov = if ((getRightAngleRestriction() ?: 0) >= 360) (fov + diff).coerceIn(
                    minFov, maxFovNoAngleRestriction
                )
                else (fov + diff).coerceIn(minFov, maxFov)
                return true
            }
            return false
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            if (fov == 0f) {
                val grow = detector.scaleFactor > 1.0f
                if (grow && mMediaPlayer?.videoScale != MediaPlayer.ScaleType.SURFACE_FIT_SCREEN) {
                    savedScale = mMediaPlayer?.videoScale
                    mMediaPlayer?.videoScale = (MediaPlayer.ScaleType.SURFACE_FIT_SCREEN)
                } else if (!grow && savedScale != null) {
                    mMediaPlayer?.videoScale = savedScale as MediaPlayer.ScaleType
                    savedScale = null
                } else if (!grow && mMediaPlayer?.videoScale == MediaPlayer.ScaleType.SURFACE_FIT_SCREEN) {
                    mMediaPlayer?.videoScale = MediaPlayer.ScaleType.SURFACE_BEST_FIT
                }
                touchAction = touchNone
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val xChanged = if (touchX != -1f && touchY != -1f) event?.x?.minus(touchX) else 0f
        val yChanged = if (touchX != -1f && touchY != -1f) event?.y?.minus(touchY) else 0f
        scaleGestureDetector.onTouchEvent(event!!)
        if (scaleGestureDetector.isInProgress) {
            touchAction = touchIgnore
            return true
        }
        val now = System.currentTimeMillis()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownMs = now
                verticalTouchActive = false
                initTouchY = event.y
                initTouchX = event.x
                touchY = event.y
                touchX = event.x
                touchAction = touchNone
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchAction == touchIgnore) return false
                touchY = event.y
                touchX = event.x
                touchAction = touchMove
                yaw = fov * -(xChanged ?: 0f) / xRange.toFloat()
                pitch = fov * -(yChanged ?: 0f) / xRange.toFloat()
                totalYawAngle += yaw
                totalPitchAngle += pitch

                Log.d("valueYaw", yaw.toString())
                Log.d("valuePitch", pitch.toString())
                if ((getRightAngleRestriction() ?: 0) >= 360 && (getLeftAngleRestriction()
                        ?: 0) >= 360) {
                    if ((totalPitchAngle < restrictionAngleY && totalPitchAngle > -restrictionAngleY)) {
                        mMediaPlayer?.updateViewpoint(
                                yaw, pitch, 0f, 0f, false
                            )
                    }
                } else {
                    if((getRightAngleRestriction() ?: 0f) == 0 && (getLeftAngleRestriction()
                            ?: 0f) == 0 && (totalPitchAngle < restrictionAngleY && totalPitchAngle > -restrictionAngleY)
                    ) {
                        mMediaPlayer?.updateViewpoint(
                            0f, pitch, 0f, 0f, false
                        )
                    }else if ((totalYawAngle < (getRightAngleRestriction()
                            ?: 0) && totalYawAngle > -(getLeftAngleRestriction()
                            ?: 0)) && (totalPitchAngle < restrictionAngleY && totalPitchAngle > -restrictionAngleY)
                    ) {
                        Log.d("isValueUpdated", "True")
                        mMediaPlayer?.updateViewpoint(
                            yaw, pitch, 0f, 0f, false
                        )
                    }
                    if (totalYawAngle > (getRightAngleRestriction() ?: 0)) {
                        totalYawAngle = (getRightAngleRestriction() ?: 0f).toFloat()
                    } else if (totalYawAngle < -(getLeftAngleRestriction() ?: 0)) {
                        totalYawAngle = -(getLeftAngleRestriction() ?: 0).toFloat()
                    }
                }
                if (totalPitchAngle > restrictionAngleY) {
                    totalPitchAngle = restrictionAngleY
                } else if (totalPitchAngle < -restrictionAngleY) {
                    totalPitchAngle = -restrictionAngleY
                }
            }

            MotionEvent.ACTION_UP -> {
                if (touchAction == touchIgnore) touchAction = touchNone
                touchX = -1f
                touchY = -1f
                val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

                if (now - touchDownMs > ViewConfiguration.getDoubleTapTimeout()) {
                    //it was not a tap
                    numberOfTaps = 0
                    lastTapTimeMs = 0L
                }
                handler.removeCallbacksAndMessages(null)

                if (abs(event.x - initTouchX) < touchSlop && abs(event.y - initTouchY) < touchSlop) {
                    if (numberOfTaps > 0 && now - lastTapTimeMs < ViewConfiguration.getDoubleTapTimeout()) {
                        numberOfTaps += 1
                    } else {
                        numberOfTaps = 1
                    }
                }
                lastTapTimeMs = now

                val showHideUIRunnable = Runnable {
                    when (numberOfTaps) {
                        1 -> showHideMediaController()
                    }
                }

                showHideUIRunnable.run()
                handler.postDelayed(showHideUIRunnable, 5000)
            }
        }

        return touchAction != touchNone
    }

    private fun getRightAngleRestriction(): Int? {
        return if (orchestra?.isBoughtForUnity == true || orchestra?.isPremium == true || ((orchestra?.leftViewAngle?.plus(
                orchestra?.rightViewAngle ?: 0
            ))?.plus(80) ?: 0) >= 360
        ) 360
        else orchestra?.rightViewAngle

    }

    private fun getLeftAngleRestriction(): Int? {
        return if (orchestra?.isBoughtForUnity == true || orchestra?.isPremium == true || ((orchestra?.leftViewAngle?.plus(
                orchestra?.rightViewAngle ?: 0
            ))?.plus(80) ?: 0) >= 360
        ) 360
        else orchestra?.leftViewAngle
    }

    private fun initVideoPlayer() {
        val args: ArrayList<String> = ArrayList()
        args.add("-vvv")
        mLibVLC = LibVLC(this, args)
        mMediaPlayer = MediaPlayer(mLibVLC)
        val vt = mMediaPlayer?.currentVideoTrack
        mMediaPlayer?.attachViews(
            binding.viewVlcLayout, null, enableSubtitles, useTextureView
        )
        try {
            val media = Media(mLibVLC, orchestra?.vrFile)
            mMediaPlayer?.media = media
            media.release()
        } catch (e: IOException) {
            throw RuntimeException("Invalid asset folder")
        }
        mMediaPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mLibVLC?.release()
    }


    override fun onPause() {
        val finishing = isFinishing
        if (finishing) overridePendingTransition(0, 0)
        super.onPause()
        pauseVideo()
    }

    private fun pauseVideo() {
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
            binding.customPlayer.vlcPlay.viewVisible()
            binding.customPlayer.vlcPause.viewGone()
        }
    }

    private fun onPlaying() {
        binding.customPlayer.vlcPlay.viewGone()
        binding.customPlayer.vlcPause.viewVisible()
        setProgressBarMax(mMediaPlayer?.length)
        setProgressValue(binding.customPlayer.vlcDuration, mMediaPlayer?.length ?: 0L)
        videoDuration = millisToString(
            mMediaPlayer?.length ?: 0L, text = false, seconds = true, large = false
        )
    }

    private fun setProgressBarMax(position: Long?) {
        binding.customPlayer.vlcProgress.max =
            if (mMediaPlayer?.length == 0L) noLengthProgressMax else position?.toInt() ?: 0
    }

    private fun setProgressBarValue(position: Long?) {
        binding.customPlayer.vlcProgress.progress = position?.toInt() ?: 0
    }

    override fun onEvent(event: MediaPlayer.Event?) {
        when (event?.type) {
            MediaPlayer.Event.Playing -> onPlaying()

            MediaPlayer.Event.Paused -> {

            }

            MediaPlayer.Event.Opening -> {

            }


            MediaPlayer.Event.PositionChanged -> {
                val position = mMediaPlayer?.time ?: 0L
                if (shouldLimitPlayTimeAndAngleRestrict()) {
                    if (restrictDurationValue() != 0L && position > restrictDurationValue()) {
                        restartPlayer()
                        return
                    }
                }

                if (dragging) return
                setProgressBarValue(mMediaPlayer?.time)
                setProgressValue(binding.customPlayer.vlcPosition, mMediaPlayer?.time ?: 0L)

            }

            MediaPlayer.Event.SeekableChanged -> {

            }

            MediaPlayer.Event.EndReached -> {
                restartPlayer()
            }
        }
    }

    private fun restartPlayer() {
        mMediaPlayer?.position = 0f
        binding.customPlayer.vlcProgress.progress = 0
        mMediaPlayer?.stop()
        binding.customPlayer.vlcPlay.viewVisible()
        binding.customPlayer.vlcPause.viewGone()
        setProgressBarMax(mMediaPlayer?.length)
        binding.customPlayer.vlcDuration.text = videoDuration
        setProgressValue(binding.customPlayer.vlcPosition, 0L)
        totalYawAngle=0f
        totalPitchAngle=0f
        fov=80f
    }
}