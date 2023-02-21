package com.co.brasso.feature.auth.signUp.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentEmailBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.PreferenceUtils

class EmailFragment : BaseFragment<EmailFragmentView, EmailFragmentPresenter>(),
    EmailFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentEmailBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener(this)
        binding.txvHaveCode.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.btnRegister -> {
                hideKeyboard(requireView())
                proceedToRegister()
            }

            R.id.txvHaveCode -> {
                val navController = NavHostFragment.findNavController(this)
                Router.navigateFragment(navController, R.id.OTPFragment)
            }
        }
    }

    private fun proceedToRegister() {
        val email: String = binding.edvEmailAddress.text.toString()
        presenter.getOTP(EmailVerification(email))
    }

    override fun createPresenter() = EmailFragmentPresenter()

    override fun success(successMSG: String?) {
        val email: String = binding.edvEmailAddress.text.toString()
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.OTPFragment)
        PreferenceUtils.saveRegistrationEmail(context, email)
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG!!)
    }

    override fun showError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }
}
