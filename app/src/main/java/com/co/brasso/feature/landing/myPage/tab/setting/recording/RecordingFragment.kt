package com.co.brasso.feature.landing.myPage.tab.setting.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import com.co.brasso.R
import com.co.brasso.databinding.FragmentRecordingBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.SettingsOptionItem
import com.co.brasso.utils.extension.setTint
import com.co.brasso.utils.util.PreferenceUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class RecordingFragment :
    BaseFragment<RecordingFragmentView, RecordingFragmentPresenter>(),
    RecordingFragmentView {

    private lateinit var binding: FragmentRecordingBinding

    private var fileTypeSize: Int = 0
    private var fileQualitySize: Int = 0
    private var samplingRateSize: Int = 0
    private var bitRateSize: Int = 0
    private var channelSize: Int = 0

    private var option: String? = null

    private var settingOption = SettingsOptionItem(0, 0, 0, 0, 0)

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
    }

    private fun setup() {
        getListSize()
        getFileSetting()
        initListener()
    }

    private fun getFileSetting() {
        option = PreferenceUtils.getFileTypePosition(requireContext())
        if (!option.isNullOrEmpty()) {
            settingOption = Gson().fromJson(option, SettingsOptionItem::class.java)
        }
        setSelectedOption()
    }

    private fun getListSize() {
        fileTypeSize = resources.getStringArray(R.array.fileType).size
        fileQualitySize = resources.getStringArray(R.array.fileQuality).size
        samplingRateSize = resources.getStringArray(R.array.samplingRate).size
        bitRateSize = resources.getStringArray(R.array.bitRate).size
        channelSize = resources.getStringArray(R.array.channelType).size
    }

    private fun setSelectedOption() {
        setTextValue(
            resources.getStringArray(R.array.fileType)[settingOption.fileTypePosition],
            binding.txvFileFormat
        )

        setTextValue(
            resources.getStringArray(R.array.fileQuality)[settingOption.fileQualityPosition],
            binding.txvEncodingQuality
        )

        setTextValue(
            resources.getStringArray(R.array.samplingRate)[settingOption.samplingRatePosition],
            binding.txvSamplingRate
        )

        setTextValue(
            resources.getStringArray(R.array.bitRate)[settingOption.bitRatePosition],
            binding.txvBitRate
        )

        setTextValue(
            resources.getStringArray(R.array.channelType)[settingOption.channelTypePosition],
            binding.txvChannel
        )

        setTintColor(
            binding.imgFileFormatLeft,
            binding.imgFileFormatRight,
            settingOption.fileTypePosition,
            fileTypeSize
        )

        setTintColor(
            binding.imgEncodingQualityLeft,
            binding.imgEncodingQualityRight,
            settingOption.fileQualityPosition,
            fileQualitySize
        )

        setTintColor(
            binding.imgSamplingRateLeft,
            binding.imgSamplingRateRight,
            settingOption.samplingRatePosition,
            samplingRateSize
        )

        setTintColor(
            binding.imgBitRateLeft,
            binding.imgBitRateRight,
            settingOption.bitRatePosition,
            bitRateSize
        )

        setTintColor(
            binding.imgChannelLeft,
            binding.imgChannelRight,
            settingOption.channelTypePosition,
            channelSize
        )
    }

    private fun initListener() {
        binding.imgFileFormatLeft.setOnClickListener {
            if ((settingOption.fileTypePosition) > 0) {
                settingOption.fileTypePosition -= 1
                setTextValue(
                    resources.getStringArray(R.array.fileType)[settingOption.fileTypePosition],
                    binding.txvFileFormat
                )
            }

            setTintColor(
                binding.imgFileFormatLeft,
                binding.imgFileFormatRight,
                settingOption.fileTypePosition,
                fileTypeSize
            )
        }

        binding.imgFileFormatRight.setOnClickListener {
            if (settingOption.fileTypePosition < fileTypeSize - 1) {
                settingOption.fileTypePosition += 1
                setTextValue(
                    resources.getStringArray(R.array.fileType)[settingOption.fileTypePosition],
                    binding.txvFileFormat
                )
            }

            setTintColor(
                binding.imgFileFormatLeft,
                binding.imgFileFormatRight,
                settingOption.fileTypePosition,
                fileTypeSize
            )
        }

        binding.imgEncodingQualityLeft.setOnClickListener {
            if (settingOption.fileQualityPosition > 0) {
                settingOption.fileQualityPosition -= 1
                setTextValue(
                    resources.getStringArray(R.array.fileQuality)[settingOption.fileQualityPosition],
                    binding.txvEncodingQuality
                )
            }

            setTintColor(
                binding.imgEncodingQualityLeft,
                binding.imgEncodingQualityRight,
                settingOption.fileQualityPosition,
                fileQualitySize
            )
        }

        binding.imgEncodingQualityRight.setOnClickListener {
            if (settingOption.fileQualityPosition < fileQualitySize - 1) {
                settingOption.fileQualityPosition += 1
                setTextValue(
                    resources.getStringArray(R.array.fileQuality)[settingOption.fileQualityPosition],
                    binding.txvEncodingQuality
                )
            }

            setTintColor(
                binding.imgEncodingQualityLeft,
                binding.imgEncodingQualityRight,
                settingOption.fileQualityPosition,
                fileQualitySize
            )
        }

        binding.imgSamplingRateLeft.setOnClickListener {
            if (settingOption.samplingRatePosition > 0) {
                settingOption.samplingRatePosition -= 1
                setTextValue(
                    resources.getStringArray(R.array.samplingRate)[settingOption.samplingRatePosition],
                    binding.txvSamplingRate
                )
            }

            setTintColor(
                binding.imgSamplingRateLeft,
                binding.imgSamplingRateRight,
                settingOption.samplingRatePosition,
                samplingRateSize
            )
        }

        binding.imgSamplingRateRight.setOnClickListener {
            if (settingOption.samplingRatePosition < samplingRateSize - 1) {
                settingOption.samplingRatePosition += 1
                setTextValue(
                    resources.getStringArray(R.array.samplingRate)[settingOption.samplingRatePosition],
                    binding.txvSamplingRate
                )
            }

            setTintColor(
                binding.imgSamplingRateLeft,
                binding.imgSamplingRateRight,
                settingOption.samplingRatePosition,
                samplingRateSize
            )
        }

        binding.imgBitRateLeft.setOnClickListener {
            if (settingOption.bitRatePosition > 0) {
                settingOption.bitRatePosition -= 1
                setTextValue(
                    resources.getStringArray(R.array.bitRate)[settingOption.bitRatePosition],
                    binding.txvBitRate
                )
            }

            setTintColor(
                binding.imgBitRateLeft,
                binding.imgBitRateRight,
                settingOption.bitRatePosition,
                bitRateSize
            )
        }

        binding.imgBitRateRight.setOnClickListener {
            if (settingOption.bitRatePosition < bitRateSize - 1) {
                settingOption.bitRatePosition += 1
                setTextValue(
                    resources.getStringArray(R.array.bitRate)[settingOption.bitRatePosition],
                    binding.txvBitRate
                )
            }

            setTintColor(
                binding.imgBitRateLeft,
                binding.imgBitRateRight,
                settingOption.bitRatePosition,
                bitRateSize
            )
        }

        binding.imgChannelLeft.setOnClickListener {
            if (settingOption.channelTypePosition > 0) {
                settingOption.channelTypePosition -= 1
                setTextValue(
                    resources.getStringArray(R.array.channelType)[settingOption.channelTypePosition],
                    binding.txvChannel
                )
            }

            setTintColor(
                binding.imgChannelLeft,
                binding.imgChannelRight,
                settingOption.channelTypePosition,
                channelSize
            )
        }

        binding.imgChannelRight.setOnClickListener {
            if (settingOption.channelTypePosition < channelSize - 1) {
                settingOption.channelTypePosition += 1
                setTextValue(
                    resources.getStringArray(R.array.channelType)[settingOption.channelTypePosition],
                    binding.txvChannel
                )
            }

            setTintColor(
                binding.imgChannelLeft,
                binding.imgChannelRight,
                settingOption.channelTypePosition,
                channelSize
            )
        }
    }

    private fun setTintColor(leftView: ImageView, rightView: ImageView, position: Int, size: Int) {
        if (position == 0) {
            setTintColorDeActive(leftView)
            setTintColorActive(rightView)
        } else if (position < size - 1) {
            setTintColorActive(leftView)
            setTintColorActive(rightView)
        } else if (position == size - 1) {
            setTintColorActive(leftView)
            setTintColorDeActive(rightView)
        }

        PreferenceUtils.setFileTypePosition(
            requireContext(),
            GsonBuilder().create().toJson(settingOption)
        )
    }

    private fun setTextValue(value: String?, view: TextView) {
        view.text = value
    }

    private fun setTintColorDeActive(view: ImageView) {
        view.setTint(R.color.tintGrey)
        view.isClickable = false
    }

    private fun setTintColorActive(view: ImageView) {
        view.setTint(R.color.black)
        view.isClickable = true
    }

    override fun createPresenter() = RecordingFragmentPresenter()
}