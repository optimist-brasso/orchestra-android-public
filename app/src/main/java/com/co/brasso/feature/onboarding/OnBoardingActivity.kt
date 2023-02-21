package com.co.brasso.feature.onboarding

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.co.brasso.R
import com.co.brasso.databinding.ActivityOnboardingBinding
import com.co.brasso.feature.auth.signUpOptions.SignUpOptionActivity
import com.co.brasso.feature.shared.adapter.OnBoardingItemsAdapter
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.model.OnboardingItem
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.PreferenceUtils

class OnBoardingActivity : BaseActivity<OnBoardingView, OnBoardingPresenter>(),
    OnBoardingView {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onBoardingItemsAdapter: OnBoardingItemsAdapter
    private var flowType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val view = binding.root
        setContentView(view)

        setup()
    }

    private fun setup() {
        getIntentData()
        setOnBoardingItems()
        setupIndicators()
        setCurrentIndicator(0)
        initListeners()
        hideFinishButton()
        changeToNextPreview()
    }

    private fun hideFinishButton() {
        if (flowType.equals(StringConstants.onBoardingFromMypage)) {
            binding.incIndicator.llyIndicatorContainer.viewVisible()
            binding.incIndicator.txtSkip.viewGone()
        } else {
            binding.incIndicator.llyIndicatorContainer.viewGone()
            binding.incIndicator.txtSkip.viewVisible()
        }
    }

    private fun getIntentData() {
        flowType = intent.getStringExtra(BundleConstant.onBoarding)
    }

    private fun initListeners() {
        binding.incIndicator.txtSkip.setOnClickListener {
            navigateToLoginOptionActivity()
        }
    }

    private fun setOnBoardingItems() {
        onBoardingItemsAdapter = OnBoardingItemsAdapter(
            listOf(
                OnboardingItem(
                    background = R.drawable.first_page_background,
                    frame = R.drawable.first_page_frame
                ), OnboardingItem(
                    background = R.drawable.second_page_background,
                    frame = R.drawable.second_page_frame
                ), OnboardingItem(
                    background = R.drawable.third_page_background,
                    frame = R.drawable.third_page_frame
                ), OnboardingItem(
                    background = R.drawable.forth_page_background,
                    frame = R.drawable.forth_page_frame
                ),
                OnboardingItem(
                    background = R.drawable.first_page_background,
                    frame = R.drawable.fifth_page_frame
                ),
                OnboardingItem(
                    background = R.drawable.sixth_page_background,
                    frame = R.drawable.sixth_page_frame
                ),
                OnboardingItem(
                    background = R.drawable.seventh_page_background,
                    frame = R.drawable.seventh_page_frame
                )
            )
        )
        binding.viewPager2.adapter = onBoardingItemsAdapter
        binding.viewPager2.offscreenPageLimit=5
        binding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (binding.viewPager2.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.onboarding_indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                binding.incIndicator.llyIndicatorContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.incIndicator.llyIndicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.incIndicator.llyIndicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.onboarding_indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.onboarding_indicator_inactive_background
                    )
                )
            }

            if (position + 1 == childCount) {
                changeToCompletionPreview()
            } else {
                changeToNextPreview()
            }
        }
    }

    private fun changeToNextPreview() {
        binding.incIndicator.llyIndicatorContainer.viewVisible()
        binding.incIndicator.txtSkip.viewGone()
    }

    private fun changeToCompletionPreview() {
        binding.incIndicator.llyIndicatorContainer.viewGone()
        binding.incIndicator.txtSkip.viewVisible()
    }

    private fun navigateToLoginOptionActivity() {
        when (flowType) {
            StringConstants.onBoardingFromMypage -> {
                finish()
            }
            StringConstants.onBoardingFromSplash -> {
                setWalkThroughSeen()
                Router.navigateActivity(this, true, SignUpOptionActivity::class)
                finish()
            }
        }
    }

    private fun setWalkThroughSeen() {
        PreferenceUtils.setWalkThroughSeen(this, true)
    }

    override fun createPresenter(): OnBoardingPresenter = OnBoardingPresenter()
}