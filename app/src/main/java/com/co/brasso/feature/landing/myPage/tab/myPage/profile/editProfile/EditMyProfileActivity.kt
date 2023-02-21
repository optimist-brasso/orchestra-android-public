package com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.databinding.ActivityEditMyProfileBinding
import com.co.brasso.databinding.CustomImagePickerBinding
import com.co.brasso.feature.shared.base.BaseActivity
import com.co.brasso.feature.shared.cropImage.CustomCropImageActivity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.feature.shared.model.updateProfile.UpdateProfileEntity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DateUtils
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.ImageCompressUtils
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditMyProfileActivity :
    BaseActivity<EditMyProfileActivityView, EditMyProfileActivityPresenter>(),
    EditMyProfileActivityView, View.OnClickListener {

    private lateinit var binding: ActivityEditMyProfileBinding
    private var myProfileResponse: MyProfileResponse? = null
    private var profilePic: File? = null
    private var uploadProfileFile: File? = null
    private lateinit var resultCameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private var dialogView: CustomImagePickerBinding? = null
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private var imagePickerType: String = Constants.gallery
    private var year: String? = null
    private var month: String? = null
    private var day: String? = null
    private lateinit var professionalID: String
    private var isProfileUpdated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditMyProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        myProfileResponse =
            intent.getSerializableExtra(StringConstants.profileData) as MyProfileResponse

        initListener()
        setData()
        initCameraLauncher()
        initGalleryLauncher()
        initCameraPermissionLauncher()
        initGalleryPermissionLauncher()
        initCropImageLauncher()
        initToolbar()
    }

    private fun initToolbar() {
        binding.incToolBar.imgNotify.viewGone()
        binding.incToolBar.cnlNotificationCount.viewGone()
        binding.incToolBar.imgCart.viewGone()
        binding.incToolBar.cnlCartCount.viewGone()
    }

    private fun initCameraLauncher() {
        resultCameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage()
                }
            }
    }

    private fun initGalleryLauncher() {
        resultGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleGalleryImage(result.data)
                }
            }
    }

    private fun initCameraPermissionLauncher() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    openCamera()
                }
            }
    }

    private fun initGalleryPermissionLauncher() {
        requestGalleryPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    openGallery()
                }
            }
    }

    private fun initCropImageLauncher() {
        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val imageUri = result.data?.data
                    compressImage(imageUri)

                }
            }
    }

    private fun initListener() {
        binding.incToolBar.imgBack.setOnClickListener(this)
        binding.btnImagePicker.setOnClickListener(this)
        binding.incCancel.llyCancel.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.btnDeleteImage.setOnClickListener(this)
        binding.rdgGender.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rdbOther.id) {
                binding.rltAddGender.viewVisible()
            } else
                binding.rltAddGender.viewGone()
        }
        binding.rltYear.clicks().throttle()?.subscribe {
            openDatePicker()
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.rltMonth.clicks().throttle()?.subscribe {
            openDatePicker()
        }?.let {
            compositeDisposable?.add(it)
        }

        binding.rltDay.clicks().throttle()?.subscribe {
            openDatePicker()
        }?.let {
            compositeDisposable?.add(it)
        }

    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = this.year?.toInt() ?: calendar.get(Calendar.YEAR)
        val month = this.month?.toInt() ?: calendar.get(Calendar.MONTH)
        val day = this.day?.toInt() ?: calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this, R.style.customDatePickerStyle, { _, finalYear, monthOfYear, dayOfMonth ->
                this.month = (monthOfYear + 1).toString()
                this.year = finalYear.toString()
                this.day = dayOfMonth.toString()
                setCurrentDateInDOB(this.year, this.month, this.day)
            }, year, month - 1, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.incToolBar.imgBack -> {
                if (!isProfileUpdated) {
                    onBackPressed()
                } else {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            binding.btnImagePicker -> {
                showFilePickerOptions()
            }
            binding.incCancel.llyCancel -> {
                if (!isProfileUpdated) {
                    onBackPressed()
                } else {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            binding.btnUpdate -> {
                proceedToUpdateProfile()
            }
            binding.btnDeleteImage -> {
                DialogUtils.showAlertDialog(
                    this, getString(R.string.removeImage), {
                        proceedToDeleteImage()
                    },
                    {},
                    isCancelable = false,
                    showNegativeBtn = true
                )
            }
        }
    }

    private fun proceedToDeleteImage() {
        if (!myProfileResponse?.profileImage.isNullOrEmpty()) {
            presenter.deleteProfilePic()
        } else if (uploadProfileFile != null) {
            binding.imgProfilePic.setImageDrawable(null)
            setData()
        }
    }

    private fun setData() {
        val dobYear = DateUtils.getDOBYear(myProfileResponse?.dob)
        val dobMonth = DateUtils.getDOBMonth(myProfileResponse?.dob)
        val dobDay = DateUtils.getDOBDay(myProfileResponse?.dob)
        setCurrentDateInDOB(dobYear, dobMonth, dobDay)
        val occupationID = myProfileResponse?.professionalId
        val gender = myProfileResponse?.gender

        if (myProfileResponse?.profileImage.isNullOrEmpty()) {
            binding.btnDeleteImage.viewGone()
        } else {
            binding.btnDeleteImage.viewVisible()
        }

        Picasso.get()
            .load(myProfileResponse?.profileImage)
            .placeholder(R.drawable.ic_profile_pic)
            .into(binding.imgProfilePic)
        binding.edtFullName.setText(myProfileResponse?.fullName)
        binding.edtNickName.setText(myProfileResponse?.username)
        binding.edtPostalCode.setText(myProfileResponse?.postalCode)
        binding.edtMusicalInstruments.setText(myProfileResponse?.musicalInstrument)
        if (myProfileResponse?.email.isNullOrEmpty()) {
            binding.lltEmail.viewGone()
        } else {
            binding.lltEmail.viewVisible()
            binding.edtEmailAddress.setText(myProfileResponse?.email)
        }

        binding.edtSelfIntroduction.setText(myProfileResponse?.shortDescription)
        initProfessionSpinner(occupationID)
        setGender(gender)
    }

    private fun setCurrentDateInDOB(year: String?, month: String?, day: String?) {
        this.year = year
        this.month = month
        this.day = day
        binding.txvYear.text = year
        binding.txvMonth.text = month
        binding.txvDay.text = day
    }

    private fun setGender(gender: String?) {
        when {
            gender.equals(getString(R.string.male)) -> {
                binding.rdbMale.isChecked = true
            }
            gender.equals(getString(R.string.female)) -> {
                binding.rdbFemale.isChecked = true
            }
            else -> {
                binding.rdbOther.isChecked = true
                binding.edtOtherGender.setText(gender)
            }
        }
    }

    private fun showFilePickerOptions() {
        dialogView = DialogUtils.showImagePickerDialog(this, {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }, {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }, {
            setImageInMainView()
        }, {
            uploadProfileFile = null
        })
    }

    private fun openGallery() {
        resultGalleryLauncher.launch(getGalleryIntent())
    }

    private fun compressImage(imageUri: Uri?) {
        ImageCompressUtils.compressImage(
            this,
            imageUri,
            imagePickerType
        ).subscribe({
            uploadProfileFile = it
            setImageInDialogView(it)
        }, {
            Toast.makeText(
                this,
                getString(R.string.text_error_while_compressing_image),
                Toast.LENGTH_SHORT
            ).show()
        }).let {
            compositeDisposable?.add(it)
        }

    }

    private fun setImageInMainView() {
        if (uploadProfileFile != null) {
            proceedToUpdateProfilePic()
        } else
            showMessageDialog(R.string.profile_image_not_selected)
    }

    private fun handleGalleryImage(data: Intent?) {
        if (data != null && data.data != null) {
            val fileUri = data.data
            imagePickerType = Constants.gallery
            openImageCropper(fileUri)
        }
    }

    private fun openImageCropper(fileUri: Uri?) {
        cropImageLauncher.launch(getImageCropperIntent(fileUri))
    }

    private fun getImageCropperIntent(fileUri: Uri?): Intent {
        val intent = Intent(this, CustomCropImageActivity::class.java)
        intent.putExtra(BundleConstant.imagePath, fileUri)
        return intent
    }

    private fun handleCameraImage() {
        if (profilePic != null) {
            val contentUri = Uri.fromFile(File(profilePic?.path.toString()))
            imagePickerType = Constants.camera
            openImageCropper(contentUri)
        }
    }

    private fun setImageInDialogView(file: File?) {
        dialogView?.imvProfilePic?.setImageURI(Uri.fromFile(file))
        dialogView?.txvSelectPicture?.viewGone()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getPictureIntent(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                profilePic = createImageFile()
                profilePic?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
        }
    }

    private fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            profilePic = this.absoluteFile
        }
    }

    private fun openCamera() {
        resultCameraLauncher.launch(getPictureIntent())
    }

    private fun initProfessionSpinner(occupationID: Int?) {
        var professionID = 0
        val profession = AppInfoGlobal.appInfo?.profession
        val professionName = mutableListOf<String>()

        profession?.forEach {
            professionName.add(it.title.toString())
        }

        if (profession != null) {
            for (i in profession.indices) {
                if (profession[i].id == occupationID) {
                    professionID = i
                }
            }
        }

        val professionAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, professionName)

        professionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnOccupation.adapter = professionAdapter

        binding.spnOccupation.setSelection(professionID)

        binding.spnOccupation.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                professionalID = profession?.get(position)?.id.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun proceedToUpdateProfile() {
        presenter.updateProfile(getUploadData())
    }

    private fun proceedToUpdateProfilePic() {
        presenter.updateProfilePic(uploadProfileFile)
    }

    private fun getUploadData(): UpdateProfileEntity {
        val fullName = binding.edtFullName.text.toString()
        val musicInstrument = binding.edtMusicalInstruments.text.toString()
        val dob = "$year/$month/$day"
        val shortDesc = binding.edtSelfIntroduction.text.toString()
        val postalCode = binding.edtPostalCode.text.toString()
        val nickName = binding.edtNickName.text.toString()
        val gender = when {
            binding.rdbMale.isChecked -> {
                getString(R.string.male)
            }
            binding.rdbFemale.isChecked -> {
                getString(R.string.female)
            }
            else -> {
                binding.edtOtherGender.text.toString()
            }
        }
        return UpdateProfileEntity(
            username = nickName,
            gender = gender,
            dob = dob,
            musicalInstrument = musicInstrument,
            shortDescription = shortDesc,
            professionalID = professionalID,
            postalCode = postalCode,
            name = fullName
        )
    }

    override fun createPresenter() = EditMyProfileActivityPresenter()

    override fun editProfileDetailSuccess() {
        showMessageDialog(R.string.profile_update_success) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun editProfilePicSuccess(myProfileResponse: MyProfileResponse) {
        this.myProfileResponse = myProfileResponse
        isProfileUpdated = true
        setData()
    }

    override fun deleteProfilePicSuccess() {
        myProfileResponse?.profileImage = null
        binding.imgProfilePic.setImageDrawable(null)
        setData()
    }
}
