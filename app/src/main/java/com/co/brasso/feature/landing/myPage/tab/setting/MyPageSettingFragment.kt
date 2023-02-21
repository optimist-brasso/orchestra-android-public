package com.co.brasso.feature.landing.myPage.tab.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.databinding.FragmentMyPageSettingBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class MyPageSettingFragment :
    BaseFragment<MyPageSettingFragmentView, MyPageSettingFragmentPresenter>(),
    MyPageSettingFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentMyPageSettingBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValue()
        initListeners()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    @SuppressLint("SetTextI18n")
    private fun setValue() {
        binding.txtAppVersionNumber.text = getString(R.string.ver) + BuildConfig.VERSION_NAME
        binding.swtPushNotification.isChecked =
            AppInfoGlobal.myProfileResponse?.notificationStatus ?: false
        setGuestLoginStatus()
    }

    private fun setGuestLoginStatus() {
        if (getLoginState()) {
            binding.swtPushNotification.viewVisible()
            binding.vwContents.viewVisible()
        } else {
            binding.swtPushNotification.viewGone()
            binding.vwContents.viewGone()
        }
    }

    private fun initListeners() {
        binding.txtStreamDownload.setOnClickListener(this)
        binding.imgStreamDownloadTab.setOnClickListener(this)
        binding.txtRecording.setOnClickListener(this)
        binding.imgRecordingTab.setOnClickListener(this)
        binding.txtDataManagement.setOnClickListener(this)
        binding.imgDataManagementTab.setOnClickListener(this)
        setSwitchListener()
    }

    private fun setSwitchListener() {
        binding.swtPushNotification.setOnCheckedChangeListener { compoundButton, b ->
            presenter.setNotificationStatus()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtStreamDownload -> {
                navigateToStreamDownloadTab()
            }
            R.id.imgStreamDownloadTab -> {
                navigateToStreamDownloadTab()
            }
            R.id.txtRecording -> {
                navigateToRecordingTab()
            }
            R.id.imgRecordingTab -> {
                navigateToRecordingTab()
            }
            R.id.txtDataManagement -> {
                navigateToDataManagementTab()
            }
            R.id.imgDataManagementTab -> {
                navigateToDataManagementTab()
            }
        }
    }

    private fun navigateToDataManagementTab() {
        Navigation.findNavController(requireView()).navigate(R.id.dataManagementFragment)
    }

    private fun navigateToRecordingTab() {
        Navigation.findNavController(requireView()).navigate(R.id.recordingFragment)
    }

    private fun navigateToStreamDownloadTab() {
        Navigation.findNavController(requireView()).navigate(R.id.streamingDownloadFragment)
    }

    override fun createPresenter() =
        MyPageSettingFragmentPresenter()

    override fun changeSwitchState() {
        binding.swtPushNotification.isChecked= !(binding.swtPushNotification.isChecked)
    }


}
