package com.co.brasso.feature.auth.forgotPassword.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.R
import com.co.brasso.databinding.FragmentResetPasswordBinding
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.auth.ResetPassword
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils

class ResetPasswordFragment :
    BaseFragment<ResetPasswordFragmentView, ResetPasswordFragmentPresenter>(),
    ResetPasswordFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentResetPasswordBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.btnResetPassword.setOnClickListener(this)
    }

    override fun createPresenter() = ResetPasswordFragmentPresenter()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnResetPassword -> {
                hideKeyboard(requireView())
                proceedToResetPassword()
            }
        }
    }

    private fun proceedToResetPassword() {
        val email: String = arguments?.getString(BundleConstant.emailAddress) ?: ""
        val otpToken: String = binding.edtOtpToken.text.toString()
        val password: String = binding.edtNewPassword.text.toString()
        presenter.resetPassword(ResetPassword(otpToken, email, password, password))
    }

    override fun success(successMSG: String?) {
        DialogUtils.showAlertDialog(
            requireContext(), successMSG, {
                Router.navigateActivity(requireContext(), true, LoginActivity::class)
            },
            {},
            isCancelable = false,
            showNegativeBtn = false
        )
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG!!)
    }
}