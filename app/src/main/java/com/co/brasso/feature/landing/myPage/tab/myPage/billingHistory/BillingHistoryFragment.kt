package com.co.brasso.feature.landing.myPage.tab.myPage.billingHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.databinding.FragmentBillingHistoryBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.viewholder.NewBillingHistoryAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class BillingHistoryFragment : BaseFragment<BillingHistoryView, BillingHistoryPresenter>(),
    BillingHistoryView {

    lateinit var binding: FragmentBillingHistoryBinding

    //    private var billingList: MutableList<BillingHistoryResponse>? = null
    private var billingList: MutableList<NewBillingHistoryResponse>? = null

    //    private var adapter: BillingHistoryAdapter? = null
    private var adapter: NewBillingHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setUp()
    }

    private fun setUp() {
        initListener()
        setRecyclerview()
        presenter.getBillingHistory(false)
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListener() {
        binding.swrBillHistory.setOnRefreshListener {
            binding.swrBillHistory.isRefreshing = false
            presenter.getBillingHistory(true)
        }
    }

    private fun setRecyclerview() {
        billingList = mutableListOf()
        adapter = NewBillingHistoryAdapter(billingList)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBillingHistory.layoutManager = layoutManager
        binding.rvBillingHistory.adapter = adapter
    }

    override fun createPresenter() = BillingHistoryPresenter()

    @SuppressLint("NotifyDataSetChanged")
    override fun setBillingHistory(list: List<NewBillingHistoryResponse>) {
        if (list.isNotEmpty()) {
            hideErrorView()
            billingList?.clear()
            billingList?.addAll(list)
            adapter?.notifyDataSetChanged()
        } else {
            showErrorView()
            billingList?.clear()
            adapter?.notifyDataSetChanged()
        }
    }

    override fun showProgressBar() {
        binding.rgProgressBar.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.rgProgressBar.cnsProgress.viewGone()
    }

    override fun showErrorView() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    override fun hideErrorView() {
        binding.lltNoData.rllNoData.viewGone()
    }
}