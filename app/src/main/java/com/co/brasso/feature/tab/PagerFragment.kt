package com.co.brasso.feature.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.co.brasso.R
import com.co.brasso.databinding.FragmentBannerBinding
import com.co.brasso.feature.shared.model.response.home.Banner
import com.co.brasso.utils.constant.BundleConstant
import com.squareup.picasso.Picasso

class PagerFragment : Fragment() {

    lateinit var bind: FragmentBannerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentBannerBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        val banner = arguments?.get(BundleConstant.banners) as Banner
        Picasso.get().load(banner.image)?.error(R.drawable.ic_thumbnail)?.into(bind.imvBanner)
    }
}