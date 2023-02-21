package com.co.brasso.utils.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.co.brasso.R
import com.co.brasso.feature.shared.model.recording.RecordingEntity
import com.co.brasso.feature.shared.model.registration.UserRegistrationEntity
import com.co.brasso.feature.shared.model.response.billinghistory.BillingHistoryResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.toTextRequestBody
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object Utils {
    fun showKeyboard(context: Context?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(context: Context?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()
    }

    fun getMimeTypeForDownloadedFile(mime: String): String {
        if (mime.isNotEmpty()) {
            return mime.substringAfter("/")
        }
        return Constants.emptyText
    }

    fun registerPostHashMap(userRegistrationEntity: UserRegistrationEntity): HashMap<String, RequestBody?> {
        val registerHashMap = HashMap<String, RequestBody?>()
        registerHashMap[ApiConstant.username] = userRegistrationEntity.username?.toTextRequestBody()
        if (!userRegistrationEntity.email.isNullOrEmpty())
            registerHashMap[ApiConstant.email] = userRegistrationEntity.email?.toTextRequestBody()
        registerHashMap[ApiConstant.gender] = userRegistrationEntity.gender?.toTextRequestBody()
        registerHashMap[ApiConstant.dob] = userRegistrationEntity.dob?.toTextRequestBody()
        registerHashMap[ApiConstant.shortDescription] =
            userRegistrationEntity.shortDescription?.toTextRequestBody()
        registerHashMap[ApiConstant.professionalId] =
            userRegistrationEntity.professionalID?.toTextRequestBody()
        registerHashMap[ApiConstant.postalCode] =
            userRegistrationEntity.postalCode?.toTextRequestBody()
        registerHashMap[ApiConstant.musicInstrument] =
            userRegistrationEntity.musicInstrument?.toTextRequestBody()
        registerHashMap[ApiConstant.name] = userRegistrationEntity.name?.toTextRequestBody()
        return registerHashMap
    }

    fun recordingPostHashMap(recordingEntity: RecordingEntity): HashMap<String, RequestBody?> {
        val recordingHashMap = HashMap<String, RequestBody?>()
        recordingHashMap[ApiConstant.orchestraId] = recordingEntity.orchestraId?.toTextRequestBody()
        recordingHashMap[ApiConstant.title] = recordingEntity.title?.toTextRequestBody()
        recordingHashMap[ApiConstant.recordingDate] =
            recordingEntity.recordingDate?.toTextRequestBody()
        recordingHashMap[ApiConstant.duration] = recordingEntity.duration?.toTextRequestBody()
        return recordingHashMap
    }

    fun openLink(url: String?, context: Context?) {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context?.let { ContextCompat.startActivity(it, myIntent, null) }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, context?.getString(R.string.txt_no_application_found), Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    fun getFormattedBillHistoryData(billHistory: List<BillingHistoryResponse>?): MutableList<BillingHistoryResponse> {
        if (billHistory.isNullOrEmpty())
            return mutableListOf()
        val billHistoryFormattedData: MutableList<BillingHistoryResponse> = mutableListOf()
        val billHistoryWithHeader: MutableList<String> = mutableListOf()
        val billHistorySortedData = billHistory.sortedByDescending { it.date }
        var totalAmount = 0.0

        billHistorySortedData.forEach {
            it.titleDate =
                DateUtils.getBillHistoryDateYear(it.date) + "年" + DateUtils.getBillHistoryDateMonth(
                    it.date
                ) + "月"
        }
        billHistorySortedData.sortedByDescending { it.titleDate }
        billHistoryWithHeader.add(
            billHistorySortedData[0].titleDate ?: ""
        )

        val firstDateFilter =
            billHistorySortedData.filter { it.titleDate == billHistoryWithHeader[0] }

        firstDateFilter.forEach {
            totalAmount += it.price ?: 0.0
        }

        billHistoryFormattedData.add(
            BillingHistoryResponse(
                date = billHistoryWithHeader[0],
                price = totalAmount,
                viewType = Constants.billHistoryHeader
            )
        )

        totalAmount = 0.0
        billHistoryFormattedData.addAll(firstDateFilter)

        billHistorySortedData.forEach { bill ->
            if (billHistoryWithHeader.find { it == bill.titleDate } == null) {
                val filterData = billHistorySortedData.filter { it.titleDate == bill.titleDate }
                filterData.forEach {
                    totalAmount += it.price ?: 0.0
                }
                billHistoryFormattedData.add(
                    BillingHistoryResponse(
                        date = bill.titleDate,
                        price = totalAmount,
                        viewType = Constants.billHistoryHeader
                    )
                )
                billHistoryFormattedData.addAll(filterData)
                billHistoryWithHeader.add(bill.titleDate ?: "")
                totalAmount = 0.0
            }
        }

        return billHistoryFormattedData
    }

    fun playerDetailHeight(context: Context?): Int {
        return (533 / 405) * DensityUtils.getScreenWidth(context)
    }

    fun clearMyFiles(context: Context?) {
        val myDir: File = File(Environment.getExternalStorageDirectory().toString() + "/" + "Download")
        if (myDir.isDirectory) {
            val children = myDir.list()
            for (i in children.indices) {
                File(myDir, children[i]).delete()
            }
        }
    }

    val TWO_DIGITS: ThreadLocal<NumberFormat?> = object : ThreadLocal<NumberFormat?>() {
        override fun initialValue(): NumberFormat {
            val fmt = NumberFormat.getInstance(Locale.US)
            if (fmt is DecimalFormat) fmt.applyPattern("00")
            return fmt
        }
    }

}