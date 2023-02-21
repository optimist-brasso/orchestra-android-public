package com.co.brasso.feature.landing.myPage.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.databinding.FragmentMyPageSettingTabBinding


class MyPageSettingFragmentTab : Fragment() {

    private lateinit var binding: FragmentMyPageSettingTabBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageSettingTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
    }

    private fun setUp() {
        setUpNavController()
    }

    private fun setUpNavController() {
        val navHostFragment = binding.navHostFragment.getFragment<NavHostFragment>()
        navController = navHostFragment.navController
    }

    fun popUpBackStack() {
        navController.navigateUp()
    }
}