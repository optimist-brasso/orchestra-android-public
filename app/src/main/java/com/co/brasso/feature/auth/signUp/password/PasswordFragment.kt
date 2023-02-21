package com.co.brasso.feature.auth.signUp.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentPasswordBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.PreferenceUtils

class PasswordFragment : BaseFragment<PasswordFragmentView, PasswordFragmentPresenter>(),
    PasswordFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentPasswordBinding
    private lateinit var password: String

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        initListeners()
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnRegister -> {
                hideKeyboard(requireView())
                proceedToVerifyPassword()
            }
        }
    }

    private fun proceedToVerifyPassword() {
        password = binding.edvPassword.text.toString()
        presenter.verifyPassword(password,PreferenceUtils.getRegistrationEmail(requireContext()))
    }

    override fun createPresenter(): PasswordFragmentPresenter = PasswordFragmentPresenter()

    override fun success(password: String?) {
        PreferenceUtils.saveRegistrationPassword(requireContext(),password)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragment(navController, R.id.registrationFragment)
    }

    override fun showError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }
}