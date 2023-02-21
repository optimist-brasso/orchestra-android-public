package com.co.brasso.feature.hallSound.hallSoundPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.co.brasso.R
import com.co.brasso.databinding.FragmentHallSoundPlayerBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.MimeTypes
import com.squareup.picasso.Picasso


class HallSoundPlayerFragment(var isPaused: Boolean) :
    BaseFragment<HallSoundPlayerFragmentView, HallSoundPlayerFragmentPresenter>() {

    private lateinit var binding: FragmentHallSoundPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    var path: String? = null
    private var hallSoundResponse: HallSoundResponse? = null
    private var audioManager: AudioManager? = null
    var audioFileName: String? = null
    var isSeekPressed: Boolean? = false

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHallSoundPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
        initToolbar()
    }

    private fun initToolbar() {
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun setup() {
        hallSoundResponse =
            arguments?.getSerializable(BundleConstant.playerDetail) as HallSoundResponse
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        initListener()
        viewDetails()
        initReceiver()
    }

    private fun initReceiver() {
        val filter = IntentFilter().apply { addAction(Constants.volumeControl) }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
    }

    private fun initListener() {
        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        binding.skbVolume.max = maxVolume ?: 0

        binding.skbVolume.progress = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!

        binding.skbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekbar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                audioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    private fun viewDetails() {
        if (hallSoundResponse?.title == null) {
            binding.txtTitleEng.viewGone()
        } else {
            binding.txtTitleEng.text = hallSoundResponse?.title
            binding.txtTitleEng.isSelected = true
            binding.txtTitleEng.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.txtTitleEng.marqueeRepeatLimit = -1
            binding.txtTitleEng.isSingleLine = true
            binding.txtTitleEng.isFocusable = true
            binding.txtTitleEng.isFocusableInTouchMode = true
        }

        if (hallSoundResponse?.titleJp == null) {
            binding.txtTitleJpn.viewGone()
        } else {
            binding.txtTitleJpn.text = hallSoundResponse?.titleJp
            binding.txtTitleJpn.isSelected = true
            binding.txtTitleJpn.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.txtTitleJpn.marqueeRepeatLimit = -1
            binding.txtTitleJpn.isSingleLine = true
            binding.txtTitleEng.isFocusable = true
            binding.txtTitleEng.isFocusableInTouchMode = true
        }

        if (hallSoundResponse?.venueTitle == null) {
            binding.txtBand.viewGone()
        } else {
            binding.txtBand.text = hallSoundResponse?.venueTitle
        }

        val hallSound = if (hallSoundResponse?.musicTag != null) {
            hallSoundResponse?.hallSound?.find { it.position == hallSoundResponse?.musicTag }
        } else {
            hallSoundResponse?.hallSound?.find { it.fileName == audioFileName }
        }
        if (hallSound?.type == null) {
            binding.txtPlace.viewGone()
        } else {
            binding.txtPlace.text = hallSound.type
        }
//        path = hallSound?.audioFile
        Picasso.get().load(hallSound?.image).placeholder(R.drawable.ic_thumbnail).into(
            binding.imgMusicThumbnail
        )
        initializePlayer()
        initControls()
    }

    private fun initializePlayer() {
        binding.exoMusicPlayer.controllerShowTimeoutMs = 0
        binding.exoMusicPlayer.cameraDistance = 30F
        exoPlayer = ExoPlayer.Builder(requireContext()).setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000).build()
        binding.exoMusicPlayer.player = exoPlayer
        val mediaItem = path?.let { buildMediaItem(it) }
        mediaItem?.let { exoPlayer?.setMediaItem(it) }
        exoPlayer?.playWhenReady = true
        exoPlayer?.prepare()
        if (isPaused) exoPlayer?.pause()
    }

    private fun initControls() {
        binding.imgPlay.setOnClickListener {
            exoPlayer?.play()
            binding.imgPlay.visibility = View.INVISIBLE
            binding.imgPause.visibility = View.VISIBLE
        }

        binding.imgPause.setOnClickListener {
            exoPlayer?.pause()
            binding.imgPause.visibility = View.INVISIBLE
            binding.imgPlay.visibility = View.VISIBLE
        }

        binding.imgFfw.setOnClickListener {
            if (exoPlayer?.isPlaying == true) isSeekPressed = true
            exoPlayer?.seekTo(exoPlayer?.currentPosition?.plus(10000) ?: 0L)
        }

        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isSeekPressed == true) {
                    isSeekPressed = false
                    return
                }
                if (isPlaying) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.imgPause.visibility = View.VISIBLE
                } else {
                    binding.imgPause.visibility = View.INVISIBLE
                    binding.imgPlay.visibility = View.VISIBLE
                }
            }

        })

        exoPlayer?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (exoPlayer?.playbackState == ExoPlayer.STATE_ENDED) {
                    exoPlayer?.seekTo(0)
                    exoPlayer?.pause()
                    isSeekPressed = false
                }

            }
        })
    }

    private fun buildMediaItem(source: String): MediaItem {
        return MediaItem.Builder().setUri(source).setMimeType(MimeTypes.APPLICATION_MP4).build()
    }

    fun releasePlayer() {
        exoPlayer?.release()
    }

    fun playerPause() {
        exoPlayer?.pause()
    }

    override fun createPresenter() = HallSoundPlayerFragmentPresenter()

    fun playNext(orchestra: HallSoundResponse?) {
        releasePlayer()
        hallSoundResponse = orchestra
        viewDetails()
    }

    fun initVolumeUp() {
        val index: Int = binding.skbVolume.progress
        binding.skbVolume.progress = index + 1
    }

    fun initVolumeDown() {
        val index: Int = binding.skbVolume.progress
        binding.skbVolume.progress = index - 1
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }
}