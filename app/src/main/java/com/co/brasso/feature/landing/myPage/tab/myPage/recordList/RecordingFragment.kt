package com.co.brasso.feature.landing.myPage.tab.myPage.recordList

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
import com.co.brasso.databinding.FragmentRecordingListBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.myPage.tab.myPage.recordList.recordPlayer.RecordingPlayerActivity
import com.co.brasso.feature.shared.RecordingListClick
import com.co.brasso.feature.shared.adapter.recordingList.RecordingListAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router

class RecordingFragment : BaseFragment<RecordingFragmentView, RecordingFragmentPresenter>(),
    RecordingFragmentView, RecordingListClick,TextWatcher{

    private lateinit var binding: FragmentRecordingListBinding
    private lateinit var recordingListAdapter: RecordingListAdapter
    private var currentPage: Int = 0
    private var searchSlug: String = StringConstants.emptyString
    var playerList: MutableList<RecordingListResponse>? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
    }

    override fun createPresenter() = RecordingFragmentPresenter()

    private fun setup() {
        initListener()
        setPageCount()
        getRecordingList()
        initRecordingListRecyclerView()
        (activity as LandingActivity).showCartIcon()
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartCount()
    }

    private fun initListener() {
        initSearchActionListener()
        initTextChangeListener()
        initPullToRefresh()
    }

    private fun initPullToRefresh() {
        binding.swrRecording.setOnRefreshListener {
            binding.swrRecording.isRefreshing = false
            if(!binding.incSearch.edvSearch.text.isNullOrEmpty()){
                binding.incSearch.edvSearch.removeTextChangedListener(this)
                binding.incSearch.edvSearch.setText(StringConstants.emptyString)
                searchSlug=StringConstants.emptyString
                initTextChangeListener()
            }
            currentPage=1
            presenter.getRecordListOnPullToRefresh(searchSlug,currentPage)
        }
    }

    private fun initTextChangeListener() {
        binding.incSearch.edvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun initSearchActionListener() {
        binding.incSearch.edvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                searchSlug = binding.incSearch.edvSearch.text.toString()
                presenter.getRecordList(searchSlug, currentPage)
                hideKeyboard(binding.incSearch.edvSearch)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setPageCount() {
        currentPage++
    }

    private fun initRecordingListRecyclerView() {
        playerList = mutableListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcvRecordingList.layoutManager = layoutManager
        recordingListAdapter = RecordingListAdapter(playerList, this) {
            currentPage += 1
            getRecordingList()
        }
        binding.rcvRecordingList.adapter = recordingListAdapter
    }

    private fun getRecordingList() {
        presenter.getRecordList(searchSlug, currentPage)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecordingList(recordingListResponse: List<RecordingListResponse>) {
        if (recordingListResponse.isEmpty() && currentPage == 1) {
            showErrorLayout()
            playerList?.clear()
            recordingListAdapter.notifyDataSetChanged()
        } else {
            hideErrorLayout()
            if (currentPage == 1)
                playerList?.clear()
            playerList?.addAll(recordingListResponse)
            addOrRemoveLoadingForAdapter()
            recordingListAdapter.notifyDataSetChanged()
        }
    }

    private fun addOrRemoveLoadingForAdapter() {
        val loadingIndex = playerList?.indexOfFirst {
            it.viewType == Constants.showLoading
        }
        if (loadingIndex != null && loadingIndex != -1) {
            playerList?.removeAt(loadingIndex)
        }
        if (playerList?.lastOrNull()?.hasNext == true) {
            playerList?.add(
                RecordingListResponse(viewType = Constants.showLoading, hasNext = true)
            )
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

    override fun onClick(recordingListResponse: RecordingListResponse?) {
        Router.navigateActivityWithParcelableData(
            requireContext(),
            false,
            Constants.recordData,
            recordingListResponse,
            RecordingPlayerActivity::class
        )
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.incSearch.edvSearch.text.isEmpty()) {
            hideErrorLayout()
            currentPage = 1
            searchSlug = StringConstants.emptyString
            presenter.getRecordList(searchSlug, currentPage)
            hideKeyboard(binding.incSearch.edvSearch)
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
