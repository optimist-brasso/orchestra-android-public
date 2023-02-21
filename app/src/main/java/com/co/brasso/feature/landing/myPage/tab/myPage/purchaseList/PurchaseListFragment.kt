package com.co.brasso.feature.landing.myPage.tab.myPage.purchaseList

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.brasso.databinding.FragmentPurchaseListBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.adapter.purchaseListAdapter.ConductorPurchaseListAdapter
import com.co.brasso.feature.shared.adapter.purchaseListAdapter.HallSoundPurchaseListAdapter
import com.co.brasso.feature.shared.adapter.purchaseListAdapter.PartPurchaseListAdapter
import com.co.brasso.feature.shared.adapter.purchaseListAdapter.PremiumPurchaseListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.purchaseList.*
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class


PurchaseListFragment :
    BaseFragment<PurchaseListFragmentView, PurchaseListFragmentPresenter>(),
    PurchaseListFragmentView, TextWatcher {

    private lateinit var binding: FragmentPurchaseListBinding

    private var hallSoundPurchaseListAdapter: HallSoundPurchaseListAdapter? = null
    private var conductorPurchaseListAdapter: ConductorPurchaseListAdapter? = null
    private var partPurchaseListListAdapter: PartPurchaseListAdapter? = null
    private var premiumPurchaseListAdapter: PremiumPurchaseListAdapter? = null

    private var hallSoundList = mutableListOf<HallSoundItem>()
    private var conductorList = mutableListOf<ConductorItem>()
    private var partList = mutableListOf<PartItem>()
    private var premiumList = mutableListOf<PremiumItem>()

    private var tempHallSoundList = mutableListOf<HallSoundItem>()
    private var tempConductorList = mutableListOf<ConductorItem>()
    private var tempPartList = mutableListOf<PartItem>()
    private var tempPremiumList = mutableListOf<PremiumItem>()

    private var isFirst = true

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentPurchaseListBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setup()
            isFirst = false
        }
        initToolbar()
    }

    private fun initToolbar() {
        binding.incSearch.imvSort.viewGone()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    override fun createPresenter() = PurchaseListFragmentPresenter()

    private fun setup() {
        initRecyclerView()
        initPullToRefreshListener()
        getPurchaseList()
        initSearchActionListener()
        initTextChangeListener()
    }

    private fun initPullToRefreshListener() {
        binding.swrPurchaseList.setOnRefreshListener {
            binding.swrPurchaseList.isRefreshing = false
            if (!binding.incSearch.edvSearch.text.isNullOrEmpty()) {
                binding.incSearch.edvSearch.removeTextChangedListener(this)
                binding.incSearch.edvSearch.setText(StringConstants.emptyString)
                initTextChangeListener()
            }
            presenter.getPurchaseList(true)
        }
    }

    private fun initRecyclerView() {
        hallSoundList = mutableListOf()
        val hallSoundLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvHallsoundList.layoutManager = hallSoundLayoutManager
        hallSoundPurchaseListAdapter =
            HallSoundPurchaseListAdapter(hallSoundList) {
                ((activity as LandingActivity).navigateToHallSoundDetail(
                    hallSoundList[it ?: 0].id
                ))
            }
        binding.rcvHallsoundList.adapter = hallSoundPurchaseListAdapter

        conductorList = mutableListOf()
        val conductorLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvConductorList.layoutManager = conductorLayoutManager
        conductorPurchaseListAdapter =
            ConductorPurchaseListAdapter(conductorList) {
                ((activity as LandingActivity).navigateToConductorDetail(
                    conductorList[it ?: 0].id
                ))
            }
        binding.rcvConductorList.adapter = conductorPurchaseListAdapter

        partList = mutableListOf()
        val partLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvPartList.layoutManager = partLayoutManager
        partPurchaseListListAdapter = PartPurchaseListAdapter(partList) {
            ((activity as LandingActivity).navigateToPartDetail(
                partList[it ?: 0].id, partList[it ?: 0].instrumentId, partList[it ?: 0].musicianId
            ))
        }
        binding.rcvPartList.adapter = partPurchaseListListAdapter

        premiumList = mutableListOf()
        val premiumLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvPremiumList.layoutManager = premiumLayoutManager
        premiumPurchaseListAdapter = PremiumPurchaseListAdapter(premiumList) {
            ((activity as LandingActivity).navigateToPremiumDetail(
                premiumList[it ?: 0].id,
                premiumList[it ?: 0].instrumentId,
                premiumList[it ?: 0].musicianId
            ))
        }
        binding.rcvPremiumList.adapter = premiumPurchaseListAdapter
    }

    private fun getPurchaseList() {
        presenter.getPurchaseList(false)
    }

    private fun initTextChangeListener() {
        binding.incSearch.edvSearch.addTextChangedListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetHallSoundData(data: MutableList<HallSoundItem>) {
        hallSoundList.clear()
        hallSoundList.addAll(data)
        hallSoundPurchaseListAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetConductorData(data: MutableList<ConductorItem>) {
        conductorList.clear()
        conductorList.addAll(data)
        conductorPurchaseListAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetPartData(data: MutableList<PartItem>) {
        partList.clear()
        partList.addAll(data)
        partPurchaseListListAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetPremiumData(data: MutableList<PremiumItem>) {
        premiumList.clear()
        premiumList.addAll(data)
        premiumPurchaseListAdapter?.notifyDataSetChanged()
    }

    private fun initSearchActionListener() {
        binding.incSearch.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterList()
                hideKeyboard(binding.incSearch.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList() {
        if (!binding.incSearch.edvSearch.text.isNullOrEmpty()) {
            val filterHallsoundList = mutableListOf<HallSoundItem>()
            val filterConductorList = mutableListOf<ConductorItem>()
            val filterPartList = mutableListOf<PartItem>()
            val filterPremiumList = mutableListOf<PremiumItem>()
            val edtText = binding.incSearch.edvSearch.text.toString().lowercase()

            val hallSoundContainsList =
                tempHallSoundList.filter { a ->
                    a.title?.lowercase()?.contains(edtText) == true
                            || a.titleJp?.lowercase()?.contains(edtText) == true
                }
            filterHallsoundList.addAll(hallSoundContainsList)
            filterHallsoundList.distinct()

            val conductorContainsList =
                tempConductorList.filter { a ->
                    a.title?.lowercase()?.contains(edtText) == true
                            || a.titleJp?.lowercase()?.contains(edtText) == true
                }
            filterConductorList.addAll(conductorContainsList)
            filterConductorList.distinct()

            val partContainsList =
                tempPartList.filter { a ->
                    a.title?.lowercase()?.contains(edtText) == true || a.titleJp?.lowercase()
                        ?.contains(edtText) == true
                            || a.instrumentTitle?.lowercase()?.contains(edtText) == true
                }
            filterPartList.addAll(partContainsList)
            filterPartList.distinct()

            val premiumContainsList =
                tempPremiumList.filter { a ->
                    a.title?.lowercase()?.contains(edtText) == true || a.titleJp?.lowercase()
                        ?.contains(edtText) == true
                            || a.instrumentTitle?.lowercase()?.contains(edtText) == true
                }
            filterPremiumList.addAll(premiumContainsList)
            filterPremiumList.distinct()

            if (filterHallsoundList.isEmpty() && filterConductorList.isEmpty() && filterPartList.isEmpty()
                && filterPremiumList.isEmpty()
            ) {
                showErrorLayout()
                hideKeyboard(binding.incSearch.edvSearch)
            } else {
                hideErrorLayout()
            }

            if (filterHallsoundList.isEmpty()) {
                binding.rcvHallsoundList.viewGone()
                binding.txtHallSound.viewGone()
                hideKeyboard(binding.incSearch.edvSearch)
            } else {
                binding.rcvHallsoundList.viewVisible()
                binding.txtHallSound.viewVisible()
            }

            if (filterConductorList.isEmpty()) {
                binding.rcvConductorList.viewGone()
                binding.txtConductor.viewGone()
                hideKeyboard(binding.incSearch.edvSearch)
            } else {
                binding.rcvConductorList.viewVisible()
                binding.txtConductor.viewVisible()
            }

            if (filterPartList.isEmpty()) {
                binding.rcvPartList.viewGone()
                binding.txtPart.viewGone()
                hideKeyboard(binding.incSearch.edvSearch)
            } else {
                binding.rcvPartList.viewVisible()
                binding.txtPart.viewVisible()
            }

            if (filterPremiumList.isEmpty()) {
                binding.rcvPremiumList.viewGone()
                binding.txtPremium.viewGone()
                hideKeyboard(binding.incSearch.edvSearch)
            } else {
                binding.rcvPremiumList.viewVisible()
                binding.txtPremium.viewVisible()
            }

            resetHallSoundData(filterHallsoundList.toMutableList())
            resetConductorData(filterConductorList.toMutableList())
            resetPartData(filterPartList.toMutableList())
            resetPremiumData(filterPremiumList.toMutableList())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecordingList(purchaseListResponse: OrchestraPurchaseListResponse) {
        if (purchaseListResponse.conductor.isNullOrEmpty() && purchaseListResponse.hallSound.isNullOrEmpty()
            && purchaseListResponse.part.isNullOrEmpty() && purchaseListResponse.premium.isNullOrEmpty()
        ) {
            showErrorLayout()
            this.hallSoundList.clear()
            this.conductorList.clear()
            this.partList.clear()
            this.premiumList.clear()
            hallSoundPurchaseListAdapter?.notifyDataSetChanged()
            conductorPurchaseListAdapter?.notifyDataSetChanged()
            partPurchaseListListAdapter?.notifyDataSetChanged()
            premiumPurchaseListAdapter?.notifyDataSetChanged()
        } else {
            hideErrorLayout()
            clearList()
            getListToPopulate(purchaseListResponse)
            if (purchaseListResponse.hallSound.isNullOrEmpty()) {
                binding.rcvHallsoundList.viewGone()
                binding.txtHallSound.viewGone()
            } else {
                binding.rcvHallsoundList.viewVisible()
                binding.txtHallSound.viewVisible()
                this.hallSoundList.clear()
                this.hallSoundList.addAll(purchaseListResponse.hallSound)
                hallSoundPurchaseListAdapter?.notifyDataSetChanged()
            }

            if (purchaseListResponse.conductor.isNullOrEmpty()) {
                binding.rcvConductorList.viewGone()
                binding.txtConductor.viewGone()
            } else {
                binding.rcvConductorList.viewVisible()
                binding.txtConductor.viewVisible()
                this.conductorList.clear()
                this.conductorList.addAll(purchaseListResponse.conductor)
                conductorPurchaseListAdapter?.notifyDataSetChanged()
            }

            if (purchaseListResponse.part.isNullOrEmpty()) {
                binding.rcvPartList.viewGone()
                binding.txtPart.viewGone()
            } else {
                binding.rcvPartList.viewVisible()
                binding.txtPart.viewVisible()
                this.partList.clear()
                this.partList.addAll(purchaseListResponse.part)
                partPurchaseListListAdapter?.notifyDataSetChanged()
            }

            if (purchaseListResponse.premium.isNullOrEmpty()) {
                binding.rcvPremiumList.viewGone()
                binding.txtPremium.viewGone()
            } else {
                binding.rcvPremiumList.viewVisible()
                binding.txtPremium.viewVisible()
                this.premiumList.clear()
                this.premiumList.addAll(purchaseListResponse.premium)
                premiumPurchaseListAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun showProgressBar() {
        binding.pgb.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgb.cnsProgress.viewGone()
    }

    override fun showErrorLayout() {
        binding.rllNoData.rllNoData.viewVisible()
    }

    override fun hideErrorLayout() {
        binding.rllNoData.rllNoData.viewGone()
    }

    private fun getListToPopulate(list: OrchestraPurchaseListResponse) {
        this.hallSoundList.addAll(list.hallSound ?: mutableListOf())
        this.conductorList.addAll(list.conductor ?: mutableListOf())
        this.partList.addAll(list.part ?: mutableListOf())
        this.premiumList.addAll(list.premium ?: mutableListOf())

        this.tempHallSoundList.addAll(hallSoundList)
        this.tempConductorList.addAll(conductorList)
        this.tempPartList.addAll(partList)
        this.tempPremiumList.addAll(premiumList)
    }

    private fun clearList() {
        hallSoundList.clear()
        conductorList.clear()
        partList.clear()
        premiumList.clear()

        this.tempHallSoundList.clear()
        this.tempConductorList.clear()
        this.tempPartList.clear()
        this.tempPremiumList.clear()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.incSearch.edvSearch.text.isEmpty()) {
            hideErrorLayout()
            presenter.getPurchaseList(false)
            hideKeyboard(binding.incSearch.edvSearch)
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
