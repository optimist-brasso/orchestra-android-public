package com.co.brasso.feature.orchestra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentOrchestraBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.orchestra.conductor.ConductorFragment
import com.co.brasso.feature.player.PlayerListFragment
import com.co.brasso.feature.shared.adapter.favourite.FavouritePagerAdapter
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.setHeaderTextColorAccent
import com.co.brasso.utils.extension.setHeaderTextColorGrey
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible

class OrchestraFragment : BaseFragment<OrchestraView, OrchestraPresenter>(), OrchestraView {

    private var isFirst = true
    private lateinit var binding: FragmentOrchestraBinding
    private var orchestraType = Constants.conductor
    private lateinit var conductorFragment: ConductorFragment
    private lateinit var sessionFragment: ConductorFragment
    private lateinit var hallSoundFragment: ConductorFragment
    private lateinit var playerFragment: PlayerListFragment
    private var favouritePagerAdapter: FavouritePagerAdapter? = null
    private var fragmentList: MutableList<Fragment>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentOrchestraBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        if (isFirst) {
            initClickListener()
            initViewPager()
            isFirst = false
        }
        (activity as? LandingActivity)?.hideToolBarTitle()
        (activity as? LandingActivity)?.showToolbar()
        (activity as? LandingActivity)?.showBottomBar()
        (activity as? LandingActivity)?.showCartIcon()
        (activity as? LandingActivity)?.showNotificationIcon()
        (activity as? LandingActivity)?.showCartCount()
    }

    private fun initViewPager() {
        if(!isNetworkAvailable()) {
            showMessageDialog(R.string.error_no_internet_connection)
        }
        fragmentList = mutableListOf()

        if (!::conductorFragment.isInitialized) {
            conductorFragment = ConductorFragment()
        }

        conductorFragment.arguments = getBundle(Constants.conductor)
        fragmentList?.add(conductorFragment)

        if (!::sessionFragment.isInitialized) {
            sessionFragment = ConductorFragment()
        }
        sessionFragment.arguments = getBundle(Constants.session)
        fragmentList?.add(sessionFragment)

        if (!::hallSoundFragment.isInitialized) {
            hallSoundFragment = ConductorFragment()
        }
        hallSoundFragment.arguments = getBundle(Constants.hallSound)
        fragmentList?.add(hallSoundFragment)

        if (!::playerFragment.isInitialized) {
            playerFragment = PlayerListFragment()
        }
        playerFragment.arguments = getBundle(Constants.player)
        fragmentList?.add(playerFragment)

        favouritePagerAdapter = FavouritePagerAdapter(fragmentList, requireActivity())
        binding.vwpOrchestra.adapter = favouritePagerAdapter
        binding.vwpOrchestra.offscreenPageLimit = 3
        binding.vwpOrchestra.isUserInputEnabled = false
        binding.vwpOrchestra.isSaveEnabled = false
        getFragmentType()
    }

    private fun getBundle(fragmentType: String): Bundle {
        val bundle = Bundle()
        bundle.putString(BundleConstant.fragmentType, fragmentType)
        return bundle
    }

    override fun createPresenter() = OrchestraPresenter()

    private fun getFragmentType() {
        (activity as LandingActivity).showNotificationIcon()
        (activity as LandingActivity).showCartIcon()
        orchestraType = arguments?.getString(BundleConstant.fragmentType) ?: Constants.conductor
        setCurrentItem()
    }

    private fun setCurrentItem() {
        when (orchestraType) {
            Constants.conductor ->
                setConductorImageSelected()
            Constants.session ->
                setSessionImageSelected()
            Constants.hallSound ->
                setHallSoundImageSelected()
            Constants.player ->
                setPlayerImageSelected()
        }
    }

    private fun initClickListener() {

        binding.header.imvPlayer.setOnClickListener {
            setPlayerImageSelected()
        }

        binding.header.imvHallsound.setOnClickListener {
            setHallSoundImageSelected()
        }

        binding.header.imvSession.setOnClickListener {
            setSessionImageSelected()
        }

        binding.header.imvConductor.setOnClickListener {
            setConductorImageSelected()
        }
    }

    private fun setConductorImageSelected() {
        binding.txvOrchestraType.viewVisible()
        binding.txvOrchestraType.text=getString(R.string.text_conductor_display)
        binding.vwpOrchestra.setCurrentItem(0, false)
        binding.header.imvConductor.borderWidth = 6
        binding.header.imvSession.borderWidth = 0
        binding.header.imvHallsound.borderWidth = 0
        binding.header.imvPlayer.borderWidth = 0
        binding.header.txvConductor.setHeaderTextColorAccent(requireContext())
        binding.header.txvSession.setHeaderTextColorGrey(requireContext())
        binding.header.txvHallSound.setHeaderTextColorGrey(requireContext())
        binding.header.txvPlayer.setHeaderTextColorGrey(requireContext())
    }

    private fun setSessionImageSelected() {
        binding.txvOrchestraType.viewVisible()
        binding.txvOrchestraType.text=getString(R.string.text_Session_display)
        binding.vwpOrchestra.setCurrentItem(1, false)
        binding.header.imvConductor.borderWidth = 0
        binding.header.imvSession.borderWidth = 6
        binding.header.imvHallsound.borderWidth = 0
        binding.header.imvPlayer.borderWidth = 0
        binding.header.txvConductor.setHeaderTextColorGrey(requireContext())
        binding.header.txvSession.setHeaderTextColorAccent(requireContext())
        binding.header.txvHallSound.setHeaderTextColorGrey(requireContext())
        binding.header.txvPlayer.setHeaderTextColorGrey(requireContext())
    }

    private fun setHallSoundImageSelected() {
        binding.txvOrchestraType.viewVisible()
        binding.txvOrchestraType.text=getString(R.string.text_hall_sound_display)
        binding.vwpOrchestra.setCurrentItem(2, false)
        binding.header.imvConductor.borderWidth = 0
        binding.header.imvSession.borderWidth = 0
        binding.header.imvHallsound.borderWidth = 6
        binding.header.imvPlayer.borderWidth = 0
        binding.header.txvConductor.setHeaderTextColorGrey(requireContext())
        binding.header.txvSession.setHeaderTextColorGrey(requireContext())
        binding.header.txvHallSound.setHeaderTextColorAccent(requireContext())
        binding.header.txvPlayer.setHeaderTextColorGrey(requireContext())
    }

    private fun setPlayerImageSelected() {
        binding.txvOrchestraType.viewGone()
        binding.vwpOrchestra.setCurrentItem(3, false)
        binding.header.imvConductor.borderWidth = 0
        binding.header.imvSession.borderWidth = 0
        binding.header.imvHallsound.borderWidth = 0
        binding.header.imvPlayer.borderWidth = 6
        binding.header.txvConductor.setHeaderTextColorGrey(requireContext())
        binding.header.txvSession.setHeaderTextColorGrey(requireContext())
        binding.header.txvHallSound.setHeaderTextColorGrey(requireContext())
        binding.header.txvPlayer.setHeaderTextColorAccent(requireContext())
    }

}