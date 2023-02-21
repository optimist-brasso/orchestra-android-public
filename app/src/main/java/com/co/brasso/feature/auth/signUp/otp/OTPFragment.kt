package com.co.brasso.feature.auth.signUp.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentOtpBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.model.request.auth.OtpVerification
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.PreferenceUtils

class OTPFragment : BaseFragment<OtpFragmentView, OtpFragmentPresenter>(),
    OtpFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentOtpBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener(this)
        binding.txtSendOtpAgain.setOnClickListener(this)
        setText()
        initListeners()
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener(this)
        binding.txtSendOtpAgain.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.btnRegister -> {
                hideKeyboard(requireView())
                proceedToVerify()
            }

            R.id.txtSendOtpAgain -> {
                resendToken()
            }
        }
    }

    private fun proceedToVerify() {
        val otpToken: String = binding.edvOtpToken.text.toString()
        presenter.verifyOtp(
            OtpVerification(
                otpToken,
                PreferenceUtils.getRegistrationEmail(context)
            )
        )
    }

    private fun resendToken() {
        val email: String = binding.txvEmailAddress.text.toString()
        presenter.resendToken(EmailVerification(email))
    }

    private fun setText() {
        binding.txvEmailAddress.text = PreferenceUtils.getRegistrationEmail(context)
    }

    override fun createPresenter() = OtpFragmentPresenter()

    override fun success(successMSG: String?) {
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.passwordFragment)
    }

    override fun resendSuccess(successMSG: String?) {
        Toast.makeText(context, successMSG, Toast.LENGTH_SHORT).show()
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG!!)
    }

    override fun showError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }

}
