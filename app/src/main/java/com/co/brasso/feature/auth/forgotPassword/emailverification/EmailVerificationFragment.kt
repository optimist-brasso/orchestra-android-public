package com.co.brasso.feature.auth.forgotPassword.emailverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentEmailVerificationBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.router.Router

class EmailVerificationFragment :
    BaseFragment<EmailVerificationFragmentView, EmailVerificationFragmentPresenter>(),
    EmailVerificationFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentEmailVerificationBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.btnReset.setOnClickListener(this)
    }

    override fun createPresenter() = EmailVerificationFragmentPresenter()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnReset -> {
                hideKeyboard(requireView())
                proceedToReset()
            }
        }
    }

    private fun proceedToReset() {
        val email: String = binding.edvEmailAddress.text.toString()
        presenter.forgotPassword(EmailVerification(email))
    }

    override fun success(successMSG: String?) {
        val email: String = binding.edvEmailAddress.text.toString()
        val bundle = bundleOf(BundleConstant.emailAddress to email)
        val navController = NavHostFragment.findNavController(this)
        Router.navigateFragmentWithData(navController, R.id.resetFragmentFragment, bundle)
    }

    override fun noData(errorMSG: String?) {
        showMessageDialog(errorMSG!!)
    }
}