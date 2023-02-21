package com.co.brasso.utils.util

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.co.brasso.R
import com.co.brasso.databinding.*
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.toPxFromDp
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

object DialogUtils {

    fun showAlertDialog(
        context: Context?, message: String?, positiveBlock: () -> Unit, negativeBlock: () -> Unit,
        isCancelable: Boolean = false, showNegativeBtn: Boolean = false,
        positiveBtnName: String? = context?.getString(R.string.ok),
        negativeBtnName: String? = context?.getString(R.string.cancel)
    ) {
        try {
            if (context != null) {
                AlertDialog.Builder(context)
                    .create()
                    .apply {
                        requestWindowFeature(Window.FEATURE_NO_TITLE)
                        setCancelable(isCancelable)
                        setMessage(message)
                        setButton(
                            AlertDialog.BUTTON_POSITIVE,
                            positiveBtnName
                        ) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            positiveBlock()
                        }
                        if (showNegativeBtn)
                            setButton(
                                AlertDialog.BUTTON_NEUTRAL, negativeBtnName
                            ) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                negativeBlock()
                            }
                        show()
                    }

            }
//                if (context != null) {
//                    val bind = LayoutCustomDialogBinding.inflate(LayoutInflater.from(context))
//                    val alertDialog = AlertDialog.Builder(context)
//                        .create()
//                        .apply {
//                            requestWindowFeature(Window.FEATURE_NO_TITLE)
//                            setCancelable(isCancelable)
//                            setView(bind.root)
//                            window?.setBackgroundDrawableResource(android.R.color.transparent)
//                            show()
//                        }
//                    bind.txtOk.text=positiveBtnName
//                     if(showNegativeBtn){
//                         bind.imgClose.viewVisible()
//                         bind.txtReturn.viewVisible()
//                     }else{
//                         bind.imgClose.viewGone()
//                         bind.txtReturn.viewGone()
//                     }
//                    bind.imgClose.setOnClickListener {
//                        alertDialog.dismiss()
//                        negativeBlock()
//                    }
//
//                    bind.txtReturn.setOnClickListener {
//                        alertDialog.dismiss()
//                        negativeBlock()
//                    }
//
//                    bind.txtOk.setOnClickListener {
//                        alertDialog.dismiss()
//                        positiveBlock()
//                    }
//                }

        } catch (e: Exception) {
            Log.d("showAlertDialog", e.localizedMessage ?: "")
        }
    }

    fun selectCameraGallery(context: Context?, openCamera: () -> Unit, openGallery: () -> Unit) {
        val items = arrayOf(
            context?.getString(R.string.text_choose_from_library),
            context?.getString(R.string.text_take_photo),
            context?.getString(R.string.cancel)
        )

        val title = TextView(context)
        title.text = context?.getString(R.string.text_add_photo)
        title.setBackgroundColor(Color.BLACK)
        title.setPadding(10, 15, 15, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 22f

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setCustomTitle(title)

        builder.setItems(items) { dialog, item ->
            when {
                items[item] == context?.getString(R.string.text_take_photo) -> {
                    openCamera()
                }
                items[item] == context?.getString(R.string.text_choose_from_library) -> {
                    openGallery()
                }
                items[item] == context?.getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun showImagePickerDialog(
        context: Context?,
        openCamera: () -> Unit,
        openGallery: () -> Unit,
        saveImage: () -> Unit,
        cancelled: () -> Unit,
    ): CustomImagePickerBinding? {
        if (context != null) {
            val bind = CustomImagePickerBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context, R.style.full_screen_dialog)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(true)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            alertDialog.setOnCancelListener {
                cancelled()
            }

            val width = DensityUtils.getScreenWidth(context) - 62.toPxFromDp(context)
            bind.cslMain.layoutParams.width = width

            bind.imvGallery.setOnClickListener {
                openGallery()
            }

            bind.imvCamera.setOnClickListener {
                openCamera()
            }

            bind.btnSave.setOnClickListener {
                saveImage()
                alertDialog.dismiss()
            }
            return bind
        }

        return null
    }

    fun showPurchaseRequestDialog(
        context: Context?,
        orchestra: HallSoundResponse?,
        buyOrchestra: () -> Unit,
        addToCart: () -> Unit
    ): DialogConfrimationPurchaseBinding? {
        if (context != null) {
            val bind = DialogConfrimationPurchaseBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(false)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }
            bind.txvTitle.text = orchestra?.title
            bind.txvTitleJp.text = orchestra?.titleJp
            if (orchestra?.venueTitle.isNullOrEmpty()){
                bind.txvBandName.viewGone()
            }else{
                bind.txvBandName.viewVisible()
                bind.txvBandName.text = orchestra?.venueTitle
            }

            bind.txtPrice.text =
                "%,d".format(
                    (orchestra?.hallSoundPrice?.toInt() ?: 0)
                ) + " " + context.getString(R.string.points)
            bind.imvCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.btnBuy.setOnClickListener {
                alertDialog.dismiss()
                buyOrchestra()
            }

            bind.btnAddToCart.setOnClickListener {
                alertDialog.dismiss()
                addToCart()
            }

            bind.txvCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            return bind
        }
        return null
    }

//    @SuppressLint("SetTextI18n")
//    fun showPointsPurchaseRequestDialog(
//        context: Context?,
//        orchestra: BundleListItem?,
//        buyOrchestra: () -> Unit,
//        addToCart: () -> Unit
//    ): DialogConfrimationPurchaseBinding? {
//        if (context != null) {
//            val bind = DialogConfrimationPurchaseBinding.inflate(LayoutInflater.from(context))
//            val alertDialog = AlertDialog.Builder(context)
//                .create()
//                .apply {
//                    requestWindowFeature(Window.FEATURE_NO_TITLE)
//                    setCancelable(false)
//                    setView(bind.root)
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    show()
//                }
//            bind.txvTitle.text = orchestra?.title
//            bind.txvTitleJp.text = orchestra?.description
//            bind.txvBandName.text =
//                orchestra?.price.toString() + " " + context.getString(R.string.points)
//
//            bind.imvCancel.setOnClickListener {
//                alertDialog.dismiss()
//            }
//
//            bind.btnBuy.setOnClickListener {
//                alertDialog.dismiss()
//                buyOrchestra()
//            }
//
//            bind.btnAddToCart.setOnClickListener {
//                alertDialog.dismiss()
//                addToCart()
//            }
//
//            bind.txvCancel.setOnClickListener {
//                alertDialog.dismiss()
//            }
//            return bind
//        }
//        return null
//    }

    fun showLogoutDialog(
        context: Context?,
        logout: () -> Unit,
        isCancelable: Boolean = false
    ): DialogConfrimationLogoutBinding? {
        if (context != null) {
            val bind = DialogConfrimationLogoutBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imvCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.btnLogout.setOnClickListener {
                alertDialog.dismiss()
                logout()
            }

            bind.txvCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            return bind
        }
        return null
    }

    fun showLogoutSuccessDialog(
        context: Context?,
        logout: () -> Unit,
        isCancelable: Boolean = false
    ): DialogLogoutSuccessBinding? {
        if (context != null) {
            val bind = DialogLogoutSuccessBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imvCancel.setOnClickListener {
                alertDialog.dismiss()
                logout()
            }

            return bind
        }
        return null
    }

    fun showCheckoutDialog(
        context: Context?,
        checkout: () -> Unit,
        isCancelable: Boolean = false
    ): DialogConfrimationCheckoutBinding? {
        if (context != null) {
            val bind = DialogConfrimationCheckoutBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imvCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.btnCheckout.setOnClickListener {
                alertDialog.dismiss()
                checkout()
            }

            bind.txvCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            return bind
        }
        return null
    }

    fun showCheckoutSuccessDialog(
        context: Context?,
        isCancelable: Boolean = false
    ): DialogLogoutSuccessBinding? {
        if (context != null) {
            val bind = DialogLogoutSuccessBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imvCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            return bind
        }
        return null
    }

    fun showRecordDialog(
        context: Context?,
        showCountDown: () -> Unit,
        isCancelable: Boolean = false
    ): CustomRecordingDialogBinding? {
        if (context != null) {
            val bind = CustomRecordingDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.txtCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.txtRecord.setOnClickListener {
                showCountDown()
                alertDialog.dismiss()
            }
            return bind
        }
        return null
    }

    fun showStopRecordDialog(
        context: Context?,
        isCancelable: Boolean = true,
        saveRecording: () -> Unit,
        stopRecording: () -> Unit,
        startRecording: () -> Unit
    ) {
        if (context != null) {
            val bind = CustomStopRecordingDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    show()
                }

            bind.txtRetake.setOnClickListener {
                alertDialog.dismiss()
                stopRecording()
                startRecording()
            }

            bind.txtSaveRecording.setOnClickListener {
                alertDialog.dismiss()
                stopRecording()
                saveRecording()
            }
        }
    }

    fun showCountDownDialog(
        context: Context?,
        isCancelable: Boolean = true,
        startRecording: () -> Unit
    ): CustomRecordingStartDialogBinding? {
        if (context != null) {
            val bind = CustomRecordingStartDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            val countdownTimer = object : CountDownTimer(4000, 1000) {
                override fun onFinish() {
                    alertDialog.dismiss()
                }

                override fun onTick(sec: Long) {

                    val seconds = (sec / 1000) % 60

                    if (seconds == 0L) {
                        alertDialog.dismiss()
                        startRecording()
                    }

                    bind.txtTimer.text = seconds.toString()
                }
            }
            countdownTimer.start()
            return bind
        }

        return null
    }

    fun showSessionMultiPartCheckoutDialog(
        context: Context?,
        checkout: () -> Unit,
        buyOrchestra: () -> Unit,
        isCancelable: Boolean = false
    ) {
        if (context != null) {
            val bind = CustomMultiPartBuyDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.txtToBuy.setOnClickListener {
                alertDialog.dismiss()
                checkout()
            }

            bind.txtAddToCart.setOnClickListener {
                alertDialog.dismiss()
                buyOrchestra()
            }

        }
    }

    fun showSessionMultiPartCheckoutSuccessDialog(
        hallSoundResponse: HallSoundResponse?,
        context: Context?,
        size: Int?,
        isCancelable: Boolean = true
    ) {
        if (context != null) {
            val bind =
                CustomMultiPartBuySuccessDialogBinding.inflate(LayoutInflater.from(context))
            bind.txtEng.text = hallSoundResponse?.title
            bind.txtJp.text = hallSoundResponse?.titleJp
            bind.txtSongNumber.text = size.toString() + " " + context.getString(R.string.songs)
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.txtReturn.setOnClickListener {
                alertDialog.dismiss()
            }

        }
    }

    fun showSessionMultiPartAddToCartSuccessDialog(
        context: Context?,
        cartItem: Int?,
        hallSoundResponse: HallSoundResponse?,
        isCancelable: Boolean = true,
        moveToCart: () -> Unit
    ) {
        if (context != null) {
            val bind =
                CustomMultiPartAddToCartDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }
            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
            bind.txtMoveToCart.setOnClickListener {
                alertDialog.dismiss()
                moveToCart()
            }
            bind.txtTitleEng.text = hallSoundResponse?.title
            bind.txtTitleJap.text = hallSoundResponse?.titleJp
            bind.txtNumberOfSongs.text =
                cartItem.toString() + " " + context.getString(R.string.songs)
            bind.textView11.text =
                context.getString(R.string.in_a_cart) + "\n" + context.getString(R.string.IPutItIn)
        }
    }

    fun showSessionCheckoutDialog(
        context: Context?,
        instrumentDetailResponse: InstrumentDetailResponse?,
        buy: () -> Unit,
        addTOCart: () -> Unit,
        isCancelable: Boolean = false,
        type: String?
    ) {
        if (context != null) {
            val bind = CustomSessionBuyDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.txtTitle.text =
                instrumentDetailResponse?.hallSoundDetail?.title + "/\n" + instrumentDetailResponse?.hallSoundDetail?.titleJp

            when (type) {
                Constants.comboType -> {
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]"
                    bind.textPremiumVideo.viewVisible()
                    bind.txtPlus.viewVisible()
                    bind.txtPrice.text =
                        "%,d".format(
                            (instrumentDetailResponse?.comboPrice?.toInt() ?: 0)
                        ) + " " + context.getString(R.string.points)
                    bind.txtConfirmation.text = context.getString(R.string.buyMultiPartSet)
                }
                Constants.premium -> {
                    bind.txtDescription.text =
                        context.getString(R.string.textPremiumVideo) + "\n" + "[" + instrumentDetailResponse?.name + "]"
                    bind.txtPlus.viewGone()
                    bind.textPremiumVideo.viewGone()
                    bind.txtPrice.text = "%,d".format(
                        (instrumentDetailResponse?.premiumVideoPrice?.toInt()
                            ?: 0)
                    ) + " " + context.getString(R.string.points)
                    bind.txtConfirmation.text = context.getString(R.string.wouldYouLikeToBuy)
                }

                else -> {
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]"
                    bind.txtPlus.viewGone()
                    bind.textPremiumVideo.viewGone()
                    bind.txtPrice.text =
                        "%,d".format(
                            (instrumentDetailResponse?.partPrice?.toInt() ?: 0)
                        ) + " " + context.getString(R.string.points)
                    bind.txtConfirmation.text = context.getString(R.string.wouldYouLikeToBuy)
                }
            }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.txtToBuy.setOnClickListener {
                alertDialog.dismiss()
                buy()
            }

            bind.txtAddToCart.setOnClickListener {
                alertDialog.dismiss()
                addTOCart()
            }

            bind.txtCancel.setOnClickListener {
                alertDialog.dismiss()
            }

        }
    }

    fun showSessionCheckoutSuccessDialog(
        context: Context?,
        instrumentDetailResponse: InstrumentDetailResponse?,
        isCancelable: Boolean = true,
        type: String?
    ): CustomSessionBuySuccessDialogBinding? {
        if (context != null) {
            val bind =
                CustomSessionBuySuccessDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.txtTitle.text =
                instrumentDetailResponse?.hallSoundDetail?.title + "/\n" + instrumentDetailResponse?.hallSoundDetail?.titleJp

            when (type) {
                Constants.premium -> {
                    bind.txtDescription.text =
                        context.getString(R.string.textPremiumVideo) + "\n" + "[" + instrumentDetailResponse?.name + "]"
                    bind.txtConfirmation.text = context.getString(R.string.iBought)
                }
                Constants.comboType -> {
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]" + "\n" + "+" + "\n" + context.getString(
                            R.string.textPremiumVideo
                        )
                    bind.txtConfirmation.text = context.getString(R.string.textSetBought)
                }
                else -> {
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]"
                    bind.txtConfirmation.text = context.getString(R.string.iBought)
                }
            }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
            return bind
        }
        return null
    }

    fun showSessionAddToCartSuccessDialog(
        context: Context?,
        instrumentDetailResponse: InstrumentDetailResponse?,
        isCancelable: Boolean = true,
        proceedToCart: () -> Unit,
        type: String?
    ) {
        if (context != null) {
            val bind =
                CustomSessionAddToCartSuccessDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            when (type) {
                Constants.premium -> {
                    bind.txtDescription.text =
                        context.getString(R.string.textPremiumVideo) + "\n" + "[" + instrumentDetailResponse?.name + "]"
                    bind.txtPlus.viewGone()
                    bind.txtPremiumVideo.viewGone()
                }
                Constants.comboType -> {
                    bind.txtPlus.viewVisible()
                    bind.txtPremiumVideo.viewVisible()
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]"
                }
                else -> {
                    bind.txtPlus.viewGone()
                    bind.txtPremiumVideo.viewGone()
                    bind.txtDescription.text =
                        context.getString(R.string.imgPart) + "[" + instrumentDetailResponse?.name + "]"
                }
            }
            bind.txtMoveCart.setOnClickListener {
                alertDialog.dismiss()
                proceedToCart()
            }
            bind.txtTitle.text =
                instrumentDetailResponse?.hallSoundDetail?.title + "/\n" + instrumentDetailResponse?.hallSoundDetail?.titleJp

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun showVideoBuyDialog(
        context: Context?,
        isCancelable: Boolean = true,
        instrumentDetailResponse: InstrumentDetailResponse?,
        buy: () -> Unit,
        proceedToCart: () -> Unit,
        proceedToPremium: () -> Unit,
        proceedToMultiPart: () -> Unit
    ) {
        if (context != null) {
            val bind =
                CustomVideoBuyDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            if (instrumentDetailResponse?.isPartBought == true) {
                bind.txtBuyThisPart.viewGone()
                bind.lltAlreadyBought.viewVisible()
                bind.txtAddToCart.viewGone()
            }

            bind.txtBuyThisPart.setOnClickListener {
                alertDialog.dismiss()
                buy()
            }

            bind.txtBuyPremium.setOnClickListener {
                alertDialog.dismiss()
                proceedToPremium()
            }

            bind.txtAddToCart.setOnClickListener {
                alertDialog.dismiss()
                proceedToCart()
            }

            bind.txtBuyMultiPart.setOnClickListener {
                alertDialog.dismiss()
                proceedToMultiPart()
            }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun showPremiumVideoBuyDialog(
        context: Context?,
        isCancelable: Boolean = true,
        instrumentDetailResponse: InstrumentDetailResponse?,
        buy: () -> Unit,
        proceedToCart: () -> Unit,
        proceedToAppendixVideo: () -> Unit
    ) {
        if (context != null) {
            val bind =
                CustomPremiumVideoBuyDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            if (instrumentDetailResponse?.isFromAppendixVideo == false)
                bind.txtAppendixVideo.viewVisible()
            else
                bind.txtAppendixVideo.viewGone()

            if (instrumentDetailResponse?.type == Constants.comboType) {
                bind.txtConfirmation.text =
                    context.getString(R.string.wouldYouLikeToBuyPremiumFootage)
                bind.txtBuyPremium.text = context.getString(R.string.purchasePartAndPremium)
            } else if (instrumentDetailResponse?.type == Constants.premium) {
                bind.txtBuyPremium.text =
                    context.getString(R.string.buyPart) + "%,d".format(
                        (instrumentDetailResponse.premiumVideoPrice?.toInt())
                    ) + " " + context.getString(
                        R.string.toBuy
                    )
                bind.txtWhatISPremiumVideo.viewGone()
            }

            if (instrumentDetailResponse?.isPremiumBought == true) {
                bind.txtBuyPremium.viewGone()
                bind.txtAddToCart.viewGone()
                bind.txtWhatISPremiumVideo.viewGone()
            }

            bind.txtBuyPremium.setOnClickListener {
                alertDialog.dismiss()
                buy()
            }

            bind.txtAddToCart.setOnClickListener {
                alertDialog.dismiss()
                proceedToCart()
            }

            bind.txtAppendixVideo.setOnClickListener {
                alertDialog.dismiss()
                proceedToAppendixVideo()
            }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun showInstrumentDetailDialog(
        context: Context?,
        sessionDetail: InstrumentResponse?,
        playerImage:String?,
        openDetail: () -> Unit,
        hideStatus: () -> Unit,
        isCancelable: Boolean = true
    ): Dialog? {
        if (context != null) {
            val bind =
                CustomDialogInstrumentDetailBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

//            if (sessionDetail?.musician_image.isNullOrEmpty()) {
//                bind.txtPlayerImage.viewVisible()
//            } else {
//                bind.txtPlayerImage.viewGone()
//            }
            if(playerImage.isNullOrEmpty()){
                bind.imgProgress.viewGone()
                Picasso.get().load(R.drawable.ic_playerlist_thumbnail).into(bind.imgPlayerImage)

            }else{
                Picasso.get().load(playerImage).error(R.drawable.ic_playerlist_thumbnail).transform(object : Transformation {
                    override fun transform(source: Bitmap?): Bitmap? {
                        val result =
                            source?.let {
                                Bitmap.createBitmap(
                                    it,
                                    0,
                                    0,
                                    source.width,
                                    source.height / 2
                                )
                            }
                        source?.recycle()
                        return result
                    }

                    override fun key(): String {
                        return System.currentTimeMillis().toString()
                    }
                }).into(bind.imgPlayerImage,object:Callback{
                    override fun onSuccess() {
                        bind.imgProgress.viewGone()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        bind.imgProgress.viewGone()
                    }

                })
            }

            if (sessionDetail?.instrument_title.isNullOrEmpty()) {
                bind.txtInstrument.viewGone()
            } else {
                bind.txtInstrument.text = sessionDetail?.instrument_title
            }

            if (sessionDetail?.instrument_musician.isNullOrEmpty()) {
                bind.txtPlayerName.viewGone()
            } else {
                bind.txtPlayerName.text = sessionDetail?.instrument_musician
            }

            if (sessionDetail?.description.isNullOrEmpty()) {
                bind.txtDescription.viewGone()
            } else {
                bind.txtDescription.text = sessionDetail?.description
            }

            bind.txtCancel.setOnClickListener {
                alertDialog.dismiss()
                hideStatus()
            }

            bind.txtNext.setOnClickListener {
                alertDialog.dismiss()
                openDetail()
            }
            return alertDialog
        }
        return null
    }

    fun showWithdrawalDialog(
        context: Context?,
        isCancelable: Boolean = false,
        withdraw: () -> Unit
    ) {
        if (context != null) {
            val bind =
                CustomWithdrawalDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.txtConfirm.setOnClickListener {
                withdraw()
                alertDialog.dismiss()
            }

            bind.txtCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun showWithdrawalSuccessDialog(
        context: Context?,
        isCancelable: Boolean = false,
        navigateToLogin: () -> Unit
    ) {
        if (context != null) {
            val bind =
                CustomWithdrawalSuccessDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context)
                .create()
                .apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setCancelable(isCancelable)
                    setView(bind.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            bind.imgClose.setOnClickListener {
                alertDialog.dismiss()
                navigateToLogin()
            }
        }
    }
}