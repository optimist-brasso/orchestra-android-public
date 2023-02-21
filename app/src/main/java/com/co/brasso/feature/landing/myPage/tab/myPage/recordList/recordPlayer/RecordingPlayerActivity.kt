package com.co.brasso.feature.landing.myPage.tab.myPage.recordList.recordPlayer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.widget.SeekBar
import android.widget.TextView
import com.co.brasso.R
import com.co.brasso.databinding.ActivityRecordingPlayerBinding
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DateUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.MimeTypes
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso

class RecordingPlayerActivity : BaseActivity<RecordingPlayerView, RecordingPlayerPresenter>() {

    private lateinit var binding: ActivityRecordingPlayerBinding
    private lateinit var exoPlayer: ExoPlayer
    private var path: String? = null
    private var audioManager: AudioManager? = null
    private var recordingListResponse: RecordingListResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setup()
    }

    private fun setup() {
        initToolBar()
        initAudioManager()
        getIntentData()
        initListeners()
        initializePlayer()
        initControls()
        initVolumeBar()
        setData()
    }

    private fun initToolBar() {
        binding.incToolBar.imgCart.viewGone()
        binding.incToolBar.imgNotify.viewGone()
    }

    private fun initControls() {
        exoPlayer.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (exoPlayer.playbackState == ExoPlayer.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                }
            }
        })
    }

    private fun getIntentData() {
        recordingListResponse =
            intent.getSerializableExtra(Constants.recordData) as RecordingListResponse?
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        Picasso.get().load(recordingListResponse?.image).placeholder(R.drawable.ic_thumbnail)
            .into(binding.imgMusicThumbnail)

        recordingListResponse?.edition?.let {
            binding.txtEdition.text = recordingListResponse?.edition
        } ?: kotlin.run { binding.txtEdition.viewGone() }

        recordingListResponse?.songTitle?.let {
            binding.txtTitle.text = recordingListResponse?.songTitle
        } ?: kotlin.run { binding.txtTitle.viewGone() }

        recordingListResponse?.duration?.let {
            binding.txtLap.text =
                getString(R.string.text_lap) + " " + DateUtils.convertIntToRecordTime(
                    recordingListResponse?.duration?.div(1000)
                )
            binding.exoMusicPlayer.findViewById<TextView>(R.id.exo_music_duration).text =
                DateUtils.convertIntToRecordTime(
                    recordingListResponse?.duration?.div(1000)
                )
        } ?: kotlin.run { binding.txtLap.viewGone() }

        recordingListResponse?.recordedDate?.let {
            binding.txtRecord.text =
                getString(R.string.text_rec) + " " + recordingListResponse?.recordedDate
        } ?: kotlin.run { binding.txtRecord.viewGone() }

    }

    private fun initAudioManager() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager?
    }

    private fun initListeners() {
        binding.imgShare.clicks().throttle()?.subscribe {
            Router.openShare(recordingListResponse?.songTitle, this)
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.incToolBar.imgBack.clicks().throttle()?.subscribe {
            onBackPressed()
        }?.let {
            compositeDisposable?.add(it)
        }
    }

    private fun initializePlayer() {
        path = recordingListResponse?.musicPath
        binding.exoMusicPlayer.controllerShowTimeoutMs = 0
        binding.exoMusicPlayer.cameraDistance = 30F
        exoPlayer = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
        binding.exoMusicPlayer.player = exoPlayer
        val mediaItem = path?.let { buildMediaItem(it) }
        mediaItem?.let { exoPlayer.setMediaItem(it) }

        exoPlayer.playWhenReady = true

        exoPlayer.prepare()
    }

    private fun buildMediaItem(source: String): MediaItem {
        return MediaItem.Builder()
            .setUri(source)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()
    }

    private fun releasePlayer() {
        exoPlayer.release()
    }

    private fun initVolumeBar() {
        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        binding.skbVolume.max = maxVolume ?: 0

        binding.skbVolume.progress = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!

        binding.skbVolume.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    audioManager?.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, AudioManager.FLAG_SHOW_UI
                    )
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val index: Int = binding.skbVolume.progress
                binding.skbVolume.progress = index - 1
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val index: Int = binding.skbVolume.progress
                binding.skbVolume.progress = index + 1
            }
            KeyEvent.KEYCODE_BACK -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun createPresenter(): RecordingPlayerPresenter = RecordingPlayerPresenter()

    override fun onBackPressed() {
        super.onBackPressed()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

}