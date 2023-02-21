package com.co.brasso.utils.util

import com.co.brasso.utils.constant.StringConstants
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun dateConverter(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getDOBYear(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateYearFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getBillHistoryDateYear(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateBillHistoryYear, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getBillHistoryDateMonth(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateBillHistoryMonth, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getBillHistoryDateDay(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateBillHistoryDay, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getDOBMonth(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateMonthFormat,Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getDOBDay(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateDayFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun convertIntToRecordTime(totalSecs: Long?): String {
        val timeString: String

        val hours = totalSecs?.div(3600)
        val minutes = totalSecs?.rem(3600)?.div(60)
        val seconds = totalSecs?.rem(60)
        timeString = if (hours == 0L) {
            String.format("%02d:%02d", minutes, seconds)
        } else {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        return timeString
    }

    fun dateFormatMonthYear(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateMonthYearFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun dateFormatMonthDayYear(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateMonthDayYearFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateYearMonthDayFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun dateFormatYearMonthDay(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateYearMonthDayFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun dateFormatYearDotMonthDotDay(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateYearDotMonthDotDayFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun dateFormatYearMonthDayFromMonthDayYear(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateYearMonthDayFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateYearMonthDayFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getMonth(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateMonthFormat,Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getDay(dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateMonthDayYearFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateDayFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }

    fun getRecordingDateAndTime(recordingDateAndTime: String?): String {
        if (recordingDateAndTime.isNullOrEmpty())
            return ""
        val inputFormat = SimpleDateFormat(StringConstants.inputDateAndTimeFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(StringConstants.outputDateAndTimeFormat, Locale.getDefault())
        val date = inputFormat.parse(recordingDateAndTime)
        return outputFormat.format(date ?: "")
    }

    fun dateFormatYearMonthDayTime(
        dateOfBirth: String?,
        outputDateFormat: String = StringConstants.outputDateYearMonthDayFormat
    ): String {
        if (dateOfBirth.isNullOrEmpty())
            return ""
        val inputFormat =
            SimpleDateFormat(StringConstants.inputDateAndTimeFormat, Locale.getDefault())
        val outputFormat =
            SimpleDateFormat(outputDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateOfBirth)
        return outputFormat.format(date ?: "")
    }
}