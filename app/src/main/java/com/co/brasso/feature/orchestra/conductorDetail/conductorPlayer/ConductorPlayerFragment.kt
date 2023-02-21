package com.co.brasso.feature.orchestra.conductorDetail.conductorPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentConductorPlayerBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.viewGone
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class ConductorPlayerFragment : BaseFragment<ConductorPlayerView, ConductorPlayerPresenter>(),
    ConductorPlayerView {

    private lateinit var binding: FragmentConductorPlayerBinding
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var path: String? = null
    private var audioManager: AudioManager? = null
    var orchestra: HallSoundResponse? = null

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

    private fun initReceiver() {
        val filter = IntentFilter().apply { addAction(Constants.volumeControl) }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConductorPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenLandScape()
        setUp()
        initToolBar()
    }

    private fun setUp() {
        initReceiver()
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        getData()
        setData()
        initializePlayer()
        initListeners()
    }

    private fun initListeners() {

        binding.videoPlayer.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
            stopPlayer()
            findNavController().popBackStack()
        }
    }

    private fun getData() {
        orchestra =
            arguments?.getSerializable(BundleConstant.orchestraId) as? HallSoundResponse
    }

    private fun setData() {
        if (orchestra?.title.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtEngTitle).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtEngTitle).text =
                orchestra?.title
        }

        if (orchestra?.titleJp.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtJpnTitle).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtJpnTitle).text =
                orchestra?.titleJp
        }

        if (orchestra?.businessType.isNullOrEmpty()) {
            binding.videoPlayer.findViewById<TextView>(R.id.txtBusinessType).viewGone()
        } else {
            binding.videoPlayer.findViewById<TextView>(R.id.txtBusinessType).text =
                orchestra?.businessType
        }
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.hideToolbar()
        (activity as? LandingActivity)?.hideBottomBar()
    }

    private fun initializePlayer() {
        path = orchestra?.vrFile
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
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    private fun stopPlayer() {
        releasePlayer()
    }

    private fun pausePlayer() {
        player?.pause()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.stop()
        player?.release()
        player = null
    }

    private fun initVolumeDown() {
        audioManager?.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
    }

    private fun initVolumeUp() {
        audioManager?.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
    }

    override fun createPresenter() = ConductorPlayerPresenter()

}