package com.co.brasso.feature.player.playerDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.co.brasso.R
import com.co.brasso.databinding.FragmentPlayerDetailBinding
import com.co.brasso.feature.auth.login.LoginActivity
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.landing.favorite.tab.hallsound.HallSoundFavouriteFragment
import com.co.brasso.feature.landing.favorite.tab.player.FavouritePlayerFragment
import com.co.brasso.feature.landing.listSong.listSongHallSound.ListSongHallSoundFragment
import com.co.brasso.feature.shared.adapter.PerformanceMusicAdapter
import com.co.brasso.feature.shared.adapter.PlayerDetailItemsAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.model.favourite.FavouriteMusician
import com.co.brasso.feature.shared.model.response.playerdetail.PerformancesItem
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.feature.shared.model.stateManagement.StateManagement
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.setTint
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.DialogUtils

class PlayerDetailFragment :
    BaseFragment<PlayerDetailFragmentView, PlayerDetailFragmentPresenter>(),
    PlayerDetailFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentPlayerDetailBinding
    private lateinit var performanceMusicAdapter: PerformanceMusicAdapter
    private lateinit var playerDetailItemsAdapter: PlayerDetailItemsAdapter
    private var playerId: Int? = null
    private var isFirst = true
    private var selectedPos: Int? = null
    private var isFromFav: Boolean? = false
    private var playerDetailResponse: PlayerDetailResponse? = null
    private var instrumentResponse: InstrumentResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentPlayerDetailBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            getPlayerId()
            initListeners()
            isFirst = false
        }
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun getPlayerDetail(playerId: Int?) {
        presenter.getPlayerDetail(playerId)
    }

    private fun initListeners() {
        binding.btnFavorite.setOnClickListener(this)
        binding.swpPlayer.setOnRefreshListener {
            binding.swpPlayer.isRefreshing = false
            getPlayerId()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFavorite -> {
                if (!getLoginState()) {
                    DialogUtils.showAlertDialog(
                        requireContext(),
                        getString(R.string.please_login),
                        {
                            StateManagement.pageName = Constants.playerDetail
                            StateManagement.id = playerId
                            navigateToLoginWithOutData()
                        },
                        {},
                        isCancelable = false,
                        showNegativeBtn = true,
                        getString(R.string.login)
                    )
                } else {
                    if (isNetworkAvailable())
                        favoriteChange()
                    else
                        showMessageDialog(R.string.error_no_internet_connection)
                }
            }
        }
    }

    private fun navigateToLoginWithOutData() {
        Router.navigateActivity(requireContext(), false, LoginActivity::class)
    }

    private fun getIsFromFav() = arguments?.getBoolean(StringConstants.isFromFav, false)
    private fun getSelectedPosition() = arguments?.getInt(StringConstants.selectedPos)

    private fun favoriteChange() {
        if (playerDetailResponse?.isFavourite == true) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favourite_session)
            binding.btnFavorite.setTint(R.color.txtColorGrey)
            if (getIsFromFav() == true) {
                FavouritePlayerFragment.selectedPos = getSelectedPosition()
            }
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favourite_selected)
            binding.btnFavorite.setTint(R.color.favouriteRed)
            if (getIsFromFav() == true) {
                FavouritePlayerFragment.selectedPos = null
            }
        }
        playerDetailResponse?.isFavourite = !(playerDetailResponse?.isFavourite ?: false)
        presenter.addMusicianFavourite(FavouriteMusician(playerId.toString()))
    }

    private fun setPlayerPicItems(images: List<String?>?) {
        playerDetailItemsAdapter = PlayerDetailItemsAdapter(images)
        binding.vwPlayer.adapter = playerDetailItemsAdapter
        binding.vwPlayer.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (binding.vwPlayer.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
    }

    private fun getPlayerId() {
        playerId = arguments?.getInt(BundleConstant.orchestraId)
        val stateManagement = arguments?.getBoolean(BundleConstant.stateManagement)
        if (stateManagement == true) {
            (requireContext() as? LandingActivity)?.shouldRefreshHome = true
        }
        if (playerId != null) {
            getPlayerDetail(playerId)
        }
    }

    private fun initPerformanceMusicRecyclerView(performances: List<PerformancesItem>?) {
        if (performances.isNullOrEmpty()) {
            binding.divider3.viewGone()
            binding.txtPerformanceMusic.viewGone()
            binding.divider4.viewGone()
            binding.rcvPerformanceMusic.viewGone()
        } else {
            val layoutManager = LinearLayoutManager(requireContext())
            binding.rcvPerformanceMusic.layoutManager = layoutManager
            performanceMusicAdapter = PerformanceMusicAdapter(performances) {
                checkLoginStatus(it)
            }
            binding.rcvPerformanceMusic.adapter = performanceMusicAdapter
        }
    }

    private fun checkLoginStatus(it: Int?) {
        if (!getLoginState()) {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.please_login),
                {
                    StateManagement.pageName = Constants.playerDetail
                    StateManagement.id = playerId
                    navigateToLoginWithOutData()
                },
                {},
                isCancelable = false,
                showNegativeBtn = true,
                getString(R.string.login)
            )
        } else {
            val performance = playerDetailResponse?.performances?.get(it ?: 0)
            if (!performance?.vrFile.isNullOrEmpty())
                openSessionDetail(it)
            else
                openOrchestraDetail(performance?.id)
        }
    }

    private fun openOrchestraDetail(id: Int?) {
        val bundle = bundleOf(BundleConstant.orchestraId to id)
        NavHostFragment.findNavController(this)
            .navigate(R.id.conductorDetailFragment, bundle)
    }

    private fun onBackPressedHandler() {
        NavHostFragment.findNavController(this).popBackStack()
    }

    private fun setupIndicators() {
        val imageCount = playerDetailItemsAdapter.itemCount
        if (imageCount > 1) {
            val indicators = arrayOfNulls<ImageView>(imageCount)
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            layoutParams.setMargins(8, 0, 8, 0)
            for (i in indicators.indices) {
                indicators[i] = ImageView(requireContext())
                indicators[i]?.let {
                    it.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.indicator_player_inactive_background
                        )
                    )
                    it.layoutParams = layoutParams
                    binding.llyIndicatorContainer.addView(it)
                }
            }
        } else {
            binding.llyIndicatorContainer.viewGone()
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.llyIndicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llyIndicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_player_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_player_inactive_background
                    )
                )
            }
        }
    }

    private fun setPlayerData(playerDetailResponse: PlayerDetailResponse) {

        if (playerDetailResponse.name.isNullOrEmpty()) {
            binding.txtName.viewGone()
            binding.txtNameMain.viewGone()
        } else {
            binding.txtName.text = playerDetailResponse.name
            binding.txtNameMain.text = playerDetailResponse.name
        }

        if (playerDetailResponse.manufacturer.isNullOrEmpty()) {
            binding.txtInstrumentUsed.viewGone()
            binding.txtInstrumentUsedTitle.viewGone()
        } else {
            binding.txtInstrumentUsed.text = playerDetailResponse.manufacturer
        }

        if (playerDetailResponse.instrument?.name.isNullOrEmpty()) {
            binding.txtInstrument.viewGone()
            binding.imageView4.viewGone()
        } else {
            binding.txtInstrument.text = playerDetailResponse.instrument?.name
        }

        if (playerDetailResponse.band.isNullOrEmpty()) {
            binding.txtBand.viewGone()
        } else {
            binding.txtBand.text = playerDetailResponse.band
        }

        if (playerDetailResponse.birthday.isNullOrEmpty()) {
            binding.txtBirthDay.viewGone()
            binding.txtBirthDayTitle.viewGone()
        } else {
            binding.txtBirthDay.text =
                DateUtils.getMonth(playerDetailResponse.birthday) + "月" + DateUtils.getDay(
                    playerDetailResponse.birthday
                ) + "日"
        }

        if (playerDetailResponse.bloodGroup.isNullOrEmpty()) {
            binding.txtBloodGroup.viewGone()
            binding.txtBloodGroupTitle.viewGone()
        } else {
            binding.txtBloodGroup.text = playerDetailResponse.bloodGroup
        }

        if (playerDetailResponse.birthplace.isNullOrEmpty()) {
            binding.txtBirthPlace.viewGone()
            binding.txtBirthPlaceTitle.viewGone()
        } else {
            binding.txtBirthPlace.text = playerDetailResponse.birthplace
        }

        if (playerDetailResponse.message.isNullOrEmpty()) {
            binding.txtMessage.viewGone()
            binding.txtMessageTitle.viewGone()
        } else {
            binding.txtMessage.text = playerDetailResponse.message
        }

        if (playerDetailResponse.profileLink.isNullOrEmpty()) {
            binding.txtDetailProfile.viewGone()
            binding.txtDetailProfileTitle.viewGone()
        } else {
            binding.txtDetailProfile.text = playerDetailResponse.profileLink
        }

        if (playerDetailResponse.twitter.isNullOrEmpty()) {
            binding.txtTwitterLink.viewGone()
            binding.txtTwitter.viewGone()
        } else {
            binding.txtTwitterLink.text = playerDetailResponse.twitter
        }

        if (playerDetailResponse.instagram.isNullOrEmpty()) {
            binding.txtInstagramLink.viewGone()
            binding.txtInstagram.viewGone()
        } else {
            binding.txtInstagramLink.text = playerDetailResponse.instagram
        }

        if (playerDetailResponse.facebook.isNullOrEmpty()) {
            binding.txtFacebookLink.viewGone()
            binding.txtFacebook.viewGone()
        } else {
            binding.txtFacebookLink.text = playerDetailResponse.facebook
        }

        if (playerDetailResponse.youtube.isNullOrEmpty()) {
            binding.txtYoutubeLink.viewGone()
            binding.txtYoutube.viewGone()
        } else {
            binding.txtYoutubeLink.text = playerDetailResponse.youtube
        }

        if (playerDetailResponse.isFavourite == true) {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_selected
                )
            )

            binding.btnFavorite.setTint(R.color.favouriteRed)

        } else {
            binding.btnFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite_session
                )
            )

            binding.btnFavorite.setTint(R.color.txtColorGrey)
        }
    }

    private fun openSessionDetail(position: Int?) {
        instrumentResponse =
            InstrumentResponse(
                sessionId = playerDetailResponse?.performances?.get(position ?: 0)?.id,
                musicianId = playerDetailResponse?.id,
                instrument_id = playerDetailResponse?.instrument?.id
            )
        val bundle = bundleOf(Constants.session to instrumentResponse)
        bundle.putInt(StringConstants.selectedPos, position ?: 0)
        bundle.putBoolean(StringConstants.isFromFav, false)
        Router.navigateFragmentWithData(
            findNavController(),
            R.id.sessionDetailFragment,
            bundle
        )
    }

    override fun createPresenter(): PlayerDetailFragmentPresenter = PlayerDetailFragmentPresenter()

    override fun success(playerDetailResponse: PlayerDetailResponse) {
        binding.srvMainView.viewVisible()
        this.playerDetailResponse = playerDetailResponse
        setPlayerData(playerDetailResponse)
        setPlayerPicItems(playerDetailResponse.images)
        initPerformanceMusicRecyclerView(playerDetailResponse.performances ?: mutableListOf())
        setupIndicators()
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    override fun noInternet(errorNoInternetConnection: Int) {
        binding.srvMainView.viewGone()
        DialogUtils.showAlertDialog(requireContext(), getString(errorNoInternetConnection), {}, {})
    }
}