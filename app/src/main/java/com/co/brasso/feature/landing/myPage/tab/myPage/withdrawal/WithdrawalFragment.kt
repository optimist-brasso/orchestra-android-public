package com.co.brasso.feature.landing.myPage.tab.myPage.withdrawal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentWithdrawalBinding
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.model.withdraw.WithdrawEntity
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DialogUtils

class WithdrawalFragment : BaseFragment<WithdrawalFragmentView, WithdrawalFragmentPresenter>(),
    WithdrawalFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentWithdrawalBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWithdrawalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
    }

    private fun setup() {
        initListeners()
    }

    private fun initListeners() {
        binding.txtWithdraw.setOnClickListener(this)
        binding.txtCancel.setOnClickListener(this)
    }

    override fun createPresenter() = WithdrawalFragmentPresenter()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtWithdraw -> {
                showWithdrawalDialog()
            }
            R.id.txtCancel -> {
                NavHostFragment.findNavController(this).popBackStack()
            }
        }
    }

    private fun showWithdrawalDialog() {
        DialogUtils.showWithdrawalDialog(requireContext(), true) { proceedToWithdrawal() }
    }

    private fun proceedToWithdrawal() {
        presenter.proceedToWithdraw(getWithdrawData())
    }

    private fun getWithdrawData(): WithdrawEntity {
        val remarks = "I got bored so I want to withdraw"

        return WithdrawEntity(
            remarks = remarks
        )
    }

    override fun withdrawSuccess(successResponse: SuccessResponse) {
        DialogUtils.showWithdrawalSuccessDialog(requireContext(), false) { logout() }
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }
}
