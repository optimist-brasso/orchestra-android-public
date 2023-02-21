package com.co.brasso.feature.auth.signUp.registration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.co.brasso.BuildConfig
import com.co.brasso.R
import com.co.brasso.databinding.CustomImagePickerBinding
import com.co.brasso.databinding.FragmentRegistrationBinding
import com.co.brasso.feature.landing.LandingActivity
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.feature.shared.cropImage.CustomCropImageActivity
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.registration.UserRegistrationEntity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.throttle
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.router.Router
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.ImageCompressUtils
import com.co.brasso.utils.util.PreferenceUtils
import com.jakewharton.rxbinding3.view.clicks
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment :
    BaseFragment<RegistrationFragmentView, RegistrationFragmentPresenter>(),
    RegistrationFragmentView, View.OnClickListener {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var gender: String
    private var professionalID: String? = null
    private var year: String = StringConstants.emptyString
    private var month: String = StringConstants.emptyString
    private var day: String = StringConstants.emptyString
    private var profilePic: File? = null
    private var uploadProfileFile: File? = null
    private lateinit var resultCameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private var dialogView: CustomImagePickerBinding? = null
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private var imagePickerType: String = Constants.gallery
    private var registrationSuccessMessage: String? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
    }

    private fun setUp() {
        initListener()
        initProfessionSpinner()
        initCameraLauncher()
        initGalleryLauncher()
        initCameraPermissionLauncher()
        initGalleryPermissionLauncher()
        initCropImageLauncher()
        setCurrentDateInDOB()
        showHideDeleteImage()
    }

    private fun showHideDeleteImage() {
        if (uploadProfileFile != null) {
            binding.btnDeleteImage.viewVisible()
        } else {
            binding.btnDeleteImage.viewGone()
        }
    }

    private fun setCurrentDateInDOB() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        binding.txvYear.hint = year.toString()
        binding.txvMonth.hint = month.toString()
        binding.txvDay.hint = day.toString()
    }

    private fun initProfessionSpinner() {
        val profession = AppInfoGlobal.appInfo?.profession
        val professionName = mutableListOf<String>()
        profession?.forEach {
            professionName.add(it.title.toString())
        }
        val professionAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, professionName)
        professionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spnOccupation.adapter = professionAdapter
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

    private fun initListener() {
        binding.btnImagePicker.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
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
        val year: Int
        val month: Int
        val day: Int
        year = if (this.year.isEmpty())
            calendar.get(Calendar.YEAR)
        else
            this.year.toInt()

        month = if (this.year.isEmpty())
            calendar.get(Calendar.MONTH)
        else
            this.month.toInt() - 1

        day = if (this.year.isEmpty())
            calendar.get(Calendar.DAY_OF_MONTH)
        else
            this.day.toInt()

        val datePickerDialog = DatePickerDialog(
            requireContext(),R.style.customDatePickerStyle, { _, finalYear, monthOfYear, dayOfMonth ->
                this.month = (monthOfYear + 1).toString()
                this.year = finalYear.toString()
                this.day = dayOfMonth.toString()
                setDobInView()
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setDobInView() {
        binding.txvYear.text = year
        binding.txvMonth.text = month
        binding.txvDay.text = day
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

    private fun openGallery() {
        resultGalleryLauncher.launch(getGalleryIntent())
    }

    private fun compressImage(imageUri: Uri?) {
        ImageCompressUtils.compressImage(
            requireContext(),
            imageUri,
            imagePickerType
        ).subscribe({
            uploadProfileFile = it
            setImageInDialogView(it)
        }, {
            Toast.makeText(
                requireContext(),
                getString(R.string.text_error_while_compressing_image),
                Toast.LENGTH_SHORT
            ).show()
        }).let {
            compositeDisposable?.add(it)
        }

    }

    override fun onClick(view: View?) {
        when (view) {

            binding.btnImagePicker -> {
                showFilePickerOptions()
            }

            binding.btnRegister -> {
                hideKeyboard(requireView())
                getGender()
                proceedToRegister()
            }

            binding.btnDeleteImage -> {
                DialogUtils.showAlertDialog(
                    requireContext(), getString(R.string.removeImage), {
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
        uploadProfileFile = null
        binding.imvProfilePic.setImageDrawable(null)
        binding.btnDeleteImage.viewGone()
    }

    override fun deleteProfilePicSuccess() {
        uploadProfileFile = null
        binding.imvProfilePic.setImageResource(0)
        showHideDeleteImage()
    }

    private fun showFilePickerOptions() {
        dialogView = DialogUtils.showImagePickerDialog(requireContext(), {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }, {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }, {
            setImageInMainView()
        }, {
            uploadProfileFile = null
        })
    }

    private fun setImageInMainView() {
        if (uploadProfileFile != null) {
            binding.imvProfilePic.setImageURI(Uri.fromFile(uploadProfileFile))
            showHideDeleteImage()
        } else {
            showMessageDialog(R.string.profile_image_not_selected)
        }
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
        val intent = Intent(requireContext(), CustomCropImageActivity::class.java)
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

    private fun setImageInDialogView(file: File) {
        dialogView?.imvProfilePic?.setImageURI(Uri.fromFile(file))
        dialogView?.txvSelectPicture?.viewGone()
    }

    private fun initCameraPermissionLauncher() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    openCamera()
                }
            }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getPictureIntent(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {

                profilePic = createImageFile()

                profilePic?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
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
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

    private fun getGender() {
        gender = when {
            binding.rdbMale.isChecked -> {
                "male"
            }
            binding.rdbFemale.isChecked -> {
                "female"
            }
            else -> {
                binding.edtOtherGender.text.toString()
            }
        }
    }

    private fun proceedToRegister() {
        presenter.register(getUploadData())
    }

    private fun getUploadData(): UserRegistrationEntity {
        val fullName = binding.edtFullName.text.toString()
        val email = PreferenceUtils.getRegistrationEmail(context)
        val musicInstrument = binding.edtMusicalInstruments.text.toString()
        val dob: String = if (year.isEmpty() && month.isEmpty() && day.isEmpty())
            ""
        else
            "$year/$month/$day"
        val shortDesc = binding.edtSelfIntroduction.text.toString()
        val postalCode = binding.edtPostalCode.text.toString()
        val nickName = binding.edtNickName.text.toString()
        return UserRegistrationEntity(
            username = nickName,
            profileImage = uploadProfileFile,
            email = email,
            gender = gender,
            dob = dob,
            musicInstrument = musicInstrument,
            shortDescription = shortDesc,
            professionalID = professionalID,
            postalCode = postalCode,
            name = fullName
        )
    }

    override fun createPresenter() = RegistrationFragmentPresenter()

    override fun success(successMSG: String?) {
        registrationSuccessMessage = successMSG
        if (getLoginState())
            showMessageDialog(registrationSuccessMessage ?: StringConstants.successFullMessage) {
                proceedToLanding()
            }
        else
            presenter.emailLogin(
                EmailLoginEntity(
                    email = PreferenceUtils.getRegistrationEmail(
                        requireContext()
                    ),
                    password = PreferenceUtils.getRegistrationPassword(requireContext()),
                    grantType = ApiConstant.grantTypePassword
                )
            )

    }

    private fun proceedToLanding() {
        Router.navigateClearingAllActivity(
            requireContext(),
            LandingActivity::class
        )
    }

    override fun navigateToMain() {
        showMessageDialog(registrationSuccessMessage ?: StringConstants.successFullMessage) {
            proceedToLanding()
        }
    }

    override fun setLoginType(loginType: String) {
        PreferenceUtils.setLoginType(requireContext(), loginType)
    }
}