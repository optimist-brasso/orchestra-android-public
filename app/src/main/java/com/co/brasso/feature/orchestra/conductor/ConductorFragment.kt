package com.co.brasso.feature.orchestra.conductor

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.co.brasso.R
import com.co.brasso.databinding.FragmentConductorBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.ClickListner
import com.co.brasso.feature.shared.adapter.ConductorAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class ConductorFragment : BaseFragment<ConductorView, ConductorPresenter>(), ConductorView,
    ClickListner, TextWatcher {

    private lateinit var binding: FragmentConductorBinding
    private var adapter: ConductorAdapter? = null
    var list = mutableListOf<HallSoundResponse>()
    private var isFirst = true
    private var fragmentType: String? = Constants.conductor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentConductorBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            setUp()
            isFirst = false
        }
        initToolBar()
        initPointBalance()
    }

    private fun initToolBar() {
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    override fun createPresenter() = ConductorPresenter()

    private fun setUp() {
        if (isNetworkAvailable()) presenter.getOrchestras(false)
        initSwipeListener()
        initSearchActionListener()
        initTextChangeListener()
        getFragmentType()
    }

    private fun initPointBalance() {
        if (getLoginState()) {
            if (isNetworkAvailable())
                presenter.getBundleList()
            binding.cnlBalance.viewVisible()
            binding.rcvConductor.setPadding(0, 0, 0, 185)
        } else {
            binding.cnlBalance.viewGone()
            binding.rcvConductor.setPadding(0, 0, 0, 20)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setBundleList(bundleList: GetBundleInfo) {
        bundleList.products?.paidPoint?.let {
            binding.txtTotalPoints.text = "%,d".format(
                (bundleList.products?.paidPoint?.plus(bundleList.products?.freePoint ?: 0))
            ) + " " + getString(R.string.P)
        } ?: kotlin.run {
            binding.txtTotalPoints.text = getString(R.string.zero)
        }
    }

    private fun initSwipeListener() {
        binding.swpConductor.setOnRefreshListener {
            binding.swpConductor.isRefreshing = false
            binding.search.edvSearch.removeTextChangedListener(this)
            binding.search.edvSearch.setText(StringConstants.emptyString)
            binding.search.edvSearch.addTextChangedListener(this)
            if (isNetworkAvailable()) presenter.getOrchestras(true)
            else showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun getFragmentType() {
        fragmentType = arguments?.getString(BundleConstant.fragmentType) ?: Constants.conductor
    }

    private fun initTextChangeListener() {
        binding.search.edvSearch.addTextChangedListener(this)
    }

    private fun initSearchActionListener() {
        binding.search.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterList()
                hideKeyboard(binding.search.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList() {
        if (!binding.search.edvSearch.text.isNullOrEmpty()) {
            val edtText = binding.search.edvSearch.text.toString()
            presenter.orchestraSearch(edtText, false)
        }
    }

    override fun setOrchestraList(list: MutableList<HallSoundResponse>) {
        if (list.isEmpty()) {
            showErrorLayout()
        } else {
            hideErrorLayout()
            clearList()
            getListToPopulate(list)
            adapter = ConductorAdapter(this.list, this, fragmentType)
            binding.rcvConductor.adapter = adapter
        }
    }

    override fun showProgressBar() {
        binding.pgbConductor.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.pgbConductor.cnsProgress.viewGone()
    }

    override fun showErrorLayout() {
        binding.lltNoData.rllNoData.viewVisible()
    }

    override fun hideErrorLayout() {
        binding.lltNoData.rllNoData.viewGone()
    }

    private fun getListToPopulate(list: MutableList<HallSoundResponse>) {
        this.list.addAll(list)
    }

    override fun onClick(position: Int) {
        val id = list[position].id
        val bundle = bundleOf(BundleConstant.orchestraId to id)
        bundle.putInt(StringConstants.selectedPos, position)
        bundle.putBoolean(StringConstants.isFromFav, false)
        bundle.putString(BundleConstant.fragmentType, fragmentType)
        when (fragmentType) {

            Constants.hallSound -> {
                Router.navigateFragmentWithData(
                    findNavController(), R.id.hallSoundDetailFragment, bundle
                )
            }

            Constants.session -> {
                Router.navigateFragmentWithData(
                    findNavController(),
                    R.id.action_orchestraFragment_to_sessionLayoutFragment,
                    bundle
                )
            }

            else -> {
                Router.navigateFragmentWithData(
                    findNavController(), R.id.conductorDetailFragment, bundle
                )
            }
        }

    }

    private fun clearList() {
        list.clear()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.search.edvSearch.text.isEmpty()) {
            hideErrorLayout()
            presenter.getOrchestras(false)
            hideKeyboard(binding.search.edvSearch)
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}