package com.co.brasso.feature.auth.signUp

import android.os.Bundle

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.ActivitySignUpBinding
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class SignUpActivity : BaseActivity<SignUpView, SignUpPresenter>() {

    private lateinit var binding: ActivitySignUpBinding

    private var isProfileUpdated: Boolean? = true
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setup()
    }

    private fun setup() {
        initNavController()
        isProfileUpdated = intent.getBooleanExtra(BundleConstant.profileStatus, true)
        updateProfileStatus()
        setListeners()
    }

    private fun updateProfileStatus() {
        if (isProfileUpdated == false) {
            navController?.popBackStack()
            navController?.navigate(R.id.registrationFragment)
            binding.incCancel.llyCancel.viewGone()
        } else {
            binding.incCancel.llyCancel.viewVisible()
        }
    }

    private fun initNavController() {
        navController = binding.navHostFragment.getFragment<NavHostFragment>().findNavController()
    }

    private fun setListeners() {
        binding.incCancel.llyCancel.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val navId = navController?.currentDestination?.id
        if ((navId != R.id.registrationFragment) || isProfileUpdated == true) {
            super.onBackPressed()
        }
    }

    override fun createPresenter() = SignUpPresenter()
}