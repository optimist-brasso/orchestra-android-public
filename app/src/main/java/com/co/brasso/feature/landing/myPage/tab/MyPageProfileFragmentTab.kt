package com.co.brasso.feature.landing.myPage.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.co.brasso.databinding.FragmentMyPageProfileTabBinding


class MyPageProfileFragmentTab : Fragment() {

    private lateinit var binding: FragmentMyPageProfileTabBinding
    private  var navController: NavController?=null
    private var isFirst = true

    override fun onCreateView(
        @NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (isFirst) {
            binding = FragmentMyPageProfileTabBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirst) {
            setUp()
            isFirst = false
        }
    }

    private fun setUp() {
        setUpNavController()
    }

    private fun setUpNavController() {
        val navHostFragment = binding.navHostFragment.getFragment<NavHostFragment>()
        navController = navHostFragment.navController
    }

    fun popUpBackStack() {
        if (navController == null)
            setUpNavController()
        navController?.navigateUp()
    }
}