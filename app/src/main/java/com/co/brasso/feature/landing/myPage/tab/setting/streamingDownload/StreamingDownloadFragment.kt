package com.co.brasso.feature.landing.myPage.tab.setting.streamingDownload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentStreamingDownloadBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.utils.util.PreferenceUtils

class StreamingDownloadFragment :
    BaseFragment<StreamingDownloadFragmentView, StreamingDownloadFragmentPresenter>(),
    StreamingDownloadFragmentView {

    private lateinit var binding: FragmentStreamingDownloadBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStreamingDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
    }

    private fun setup() {
        initListener()
        setPreferenceStatus()
    }

    private fun initListener() {
        binding.swtStreamingOnWifi.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.putStreamOnWifi(requireContext(), isChecked)
        }
        binding.swtNotifyUsingMobileData.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.putNotifyingWhenMobileData(requireContext(), isChecked)
        }
        binding.swtDownloadWhenWifi.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.putDownloadOnlyOnWIFI(requireContext(), isChecked)
        }
    }

    private fun setPreferenceStatus() {
        val streamOnWifi = PreferenceUtils.getStreamOnWifi(requireContext())
        val notifyUsingMobileData = PreferenceUtils.getNotifyingWhenMobileData(requireContext())
        val downloadFromWIFI = PreferenceUtils.getDownloadOnlyOnWIFI(requireContext())
        binding.swtStreamingOnWifi.isChecked = streamOnWifi
        binding.swtDownloadWhenWifi.isChecked = downloadFromWIFI
        binding.swtNotifyUsingMobileData.isChecked = notifyUsingMobileData

    }

    override fun createPresenter() = StreamingDownloadFragmentPresenter()
}