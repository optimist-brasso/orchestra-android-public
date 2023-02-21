package com.co.brasso.feature.landing.myPage.tab.myPage.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import com.co.brasso.databinding.FragmentMyProfileBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile.EditMyProfileActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.squareup.picasso.Picasso


class MyProfileFragment :
    BaseFragment<MyProfileFragmentView, MyProfileFragmentPresenter>(),
    MyProfileFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentMyProfileBinding
    private var myProfileResponse: MyProfileResponse? = null
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        getProfile()
        initListener()
        initEditProfileSuccess()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun getProfile() {
        presenter.getProfile()
    }

    private fun initListener() {
        binding.llyEditProfile.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.llyEditProfile -> {
                navigateToEditProfile()
            }
        }
    }

    private fun navigateToEditProfile() {
        val intent = Intent(requireContext(), EditMyProfileActivity::class.java)
        intent.putExtra(
            StringConstants.profileData,
            myProfileResponse
        )
        editProfileLauncher.launch(intent)
    }

    override fun createPresenter(): MyProfileFragmentPresenter =
        MyProfileFragmentPresenter()

    override fun success(myProfileResponse: MyProfileResponse?) {
        this.myProfileResponse = myProfileResponse
        displayUserDetails(myProfileResponse)
        binding.incProgressbar.cnsProgress.viewGone()
    }

    override fun hideProgressbar() {
        binding.incProgressbar.cnsProgress.viewGone()
    }

    override fun showProgressbar() {
        binding.incProgressbar.cnsProgress.viewVisible()
    }

    override fun hideMainView() {
        binding.lltMainLayout.viewGone()
    }


    private fun displayUserDetails(myProfileResponse: MyProfileResponse?) {
        binding.lltMainLayout.viewVisible()
        val dob = DateUtils.dateConverter(myProfileResponse?.dob)
        Picasso.get()
            .load(myProfileResponse?.profileImage)
            .into(binding.imgProfilePic)
        val fullName = myProfileResponse?.fullName
        val gender = myProfileResponse?.gender
        val userName = myProfileResponse?.username
        val musicalInstrument = myProfileResponse?.musicalInstrument
        val email = myProfileResponse?.email
        val profession = myProfileResponse?.professional
        val shortIntroduction = myProfileResponse?.shortDescription
        val postalCode = myProfileResponse?.postalCode

        if (fullName.isNullOrEmpty()) {
            binding.lltFullName.viewGone()
            binding.dvdFullName.viewGone()
        } else {
            binding.lltFullName.viewVisible()
            binding.dvdFullName.viewVisible()
        }

        if (gender.isNullOrEmpty()) {
            binding.lltGender.viewGone()
            binding.dvdGender.viewGone()
        } else {
            binding.lltGender.viewVisible()
            binding.dvdGender.viewVisible()
        }

        if (userName.isNullOrEmpty()) {
            binding.lltNickName.viewGone()
            binding.dvdNickname.viewGone()
        } else {
            binding.lltNickName.viewVisible()
            binding.dvdNickname.viewVisible()
        }

        if (musicalInstrument.isNullOrEmpty()) {
            binding.lltPart.viewGone()
            binding.dvdPart.viewGone()
        } else {
            binding.lltPart.viewVisible()
            binding.dvdPart.viewVisible()
        }

        if (dob.isEmpty()) {
            binding.lltAge.viewGone()
            binding.dvdAge.viewGone()
        } else {
            binding.lltAge.viewVisible()
            binding.dvdAge.viewVisible()
        }

        if (email.isNullOrEmpty()) {
            binding.lltEmail.viewGone()
            binding.dvdEmail.viewGone()
        } else {
            binding.lltEmail.viewVisible()
            binding.dvdEmail.viewVisible()
        }

        if (profession.isNullOrEmpty()) {
            binding.lltProfession.viewGone()
            binding.dvdProfession.viewGone()
        } else {
            binding.lltProfession.viewVisible()
            binding.dvdProfession.viewVisible()
        }

        if (postalCode.isNullOrEmpty()) {
            binding.lltPostalCode.viewGone()
            binding.dvdPostalCode.viewGone()
        } else {
            binding.lltPostalCode.viewVisible()
            binding.dvdPostalCode.viewVisible()
        }

        if (shortIntroduction.isNullOrEmpty()) {
            binding.lltSelfIntroduction.viewGone()
            binding.dvdSelfIntroduction.viewGone()
        } else {
            binding.lltSelfIntroduction.viewVisible()
            binding.dvdSelfIntroduction.viewVisible()
        }

        binding.txtFullName.text = fullName
        binding.txtGender.text = gender
        binding.txtNickName.text = userName
        binding.txtPart.text = musicalInstrument
        binding.txtAge.text = dob
        binding.txtEmail.text = email
        binding.txtProfession.text = profession
        binding.txtPostalCode.text = postalCode
        binding.txtSelfIntroduction.text = shortIntroduction
    }

    private fun initEditProfileSuccess() {
        editProfileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    getProfile()
                }
            }
    }
}
