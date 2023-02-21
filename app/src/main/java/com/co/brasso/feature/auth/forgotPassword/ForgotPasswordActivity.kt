package com.co.brasso.feature.auth.forgotPassword

import android.os.Bundle
import com.co.brasso.databinding.ActivityForgotPasswordBinding
import com.co.brasso.feature.shared.base.BaseActivity

class ForgotPasswordActivity : BaseActivity<ForgotPasswordView, ForgotPasswordPresenter>() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
    }

    private fun setListeners() {
        binding.incCancel.llyCancel.setOnClickListener {
            onBackPressHandler()
        }
    }

    private fun onBackPressHandler() {
        onBackPressed()
    }

    override fun createPresenter() = ForgotPasswordPresenter()
}