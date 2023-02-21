package com.co.brasso.feature.orchestra.conductorDetail.organizationchart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.co.brasso.R
import com.co.brasso.databinding.ActivityOrganizationChartBinding
import com.co.brasso.utils.constant.BundleConstant
import com.squareup.picasso.Picasso

class OrganizationChartActivity : AppCompatActivity() {
    var imageUrl: String? = null
    private lateinit var binding: ActivityOrganizationChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationChartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imageUrl = intent.getStringExtra(BundleConstant.url)
        setImage(imageUrl)
        initListeners()
    }

    private fun initListeners() {
        binding.imgBack.setOnClickListener { onBackPressed() }
    }

    private fun setImage(imageUrl: String?) {
        Picasso.get().load(imageUrl)?.error(R.drawable.ic_thumbnail)?.into(binding.imvView)
    }
}