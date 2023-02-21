package com.co.brasso.feature.landing.myPage.tab.myPage.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentChangePasswordBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.auth.ChangePasswordVerification

class ChangePasswordFragment :
    BaseFragment<ChangePasswordFragmentView, ChangePasswordFragmentPresenter>(),
    ChangePasswordFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        initListeners()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListeners() {
        binding.btnChangePassword.setOnClickListener(this)
        binding.txtCancel.setOnClickListener(this)
        binding.imgCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChangePassword -> {
                proceedToChangePassword()
            }

            R.id.txtCancel -> {
                onBackPressedHandler()
            }

            R.id.imgCancel -> {
                onBackPressedHandler()
            }
        }
    }

    private fun proceedToChangePassword() {
        val oldPassword: String = binding.edtOldPassword.text.toString()
        val password: String = binding.edtNewPassword.text.toString()
        val confirmPassword: String = binding.edtConfirmPassword.text.toString()
        presenter.changePassword(ChangePasswordVerification(oldPassword, password, confirmPassword))
    }

    private fun onBackPressedHandler() {
        findNavController(this).popBackStack()
    }

    override fun createPresenter(): ChangePasswordFragmentPresenter =
        ChangePasswordFragmentPresenter()

    override fun success(successMSG: String?) {
        showMessageDialog(successMSG!!) { onBackPressedHandler() }
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG!!)
    }

    override fun showError(errorMSG: Int) {
        showMessageDialog(errorMSG)
    }
}