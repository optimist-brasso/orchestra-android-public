package com.co.brasso.feature.shared.cropImage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.co.brasso.databinding.ActivityCropImageBinding
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.customView.imageCropper.CropImageView


class CustomCropImageActivity : AppCompatActivity(), CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {

    private lateinit var binding: ActivityCropImageBinding
    private var imagePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()

    }

    private fun setUp() {
        initListener()
        getIntentData()
    }

    private fun getIntentData() {
        imagePath = intent?.getParcelableExtra(BundleConstant.imagePath)
        setImageUri()
    }

    private fun setImageUri() {
        binding.crpImageView.setImageUriAsync(imagePath)
        binding.crpImageView.setAspectRatio(1,1)
    }

    private fun initListener() {
        binding.imvBack.setOnClickListener {
            finish()
        }

        binding.crpImageView.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
        }

        binding.txvCrop.setOnClickListener {
            binding.crpImageView.croppedImageAsync()
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error != null) {
            Log.e("AIC", "Failed to load image by URI", error)
            Toast.makeText(this, "Image load failed: " + error.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        handleCropResult(result)
    }

    private fun handleCropResult(result: CropImageView.CropResult?) {
        if (result != null && result.error == null) {
            setImageCropResult(result.uriContent)
        } else {
            Log.e("AIC", "Failed to crop image", result?.error)
            Toast
                .makeText(this, "Crop failed: ${result?.error?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setImageCropResult(uriContent: Uri?) {
        val intent = Intent()
        intent.data = uriContent
        setResult(RESULT_OK, intent)
        finish()
    }

}