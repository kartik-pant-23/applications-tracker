package com.studbudd.application_tracker.core.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

object TimestampHelper {

    private const val FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DEFAULT = "dd MMMM, yyyy"
    const val SHORT_MONTH = "dd MMM, yyyy"
    const val DETAILED = "dd-MM-yyyy hh:mm a"

    private fun getDateFormat(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.ENGLISH)
    }

    fun getDate(timestamp: String, format: String = FORMAT): Date? {
        return try {
            getDateFormat(format).parse(timestamp)
        } catch (e: Exception) {
            Log.e("TimestampHelper", "failed parsing timestamp - $timestamp")
            null
        }
    }

    fun getDateString(date: Date?, format: String = FORMAT): String? {
        if (date == null)
            return null
        return try {
            getDateFormat(format).format(date)
        } catch (e: Exception) {
            Log.e("TimestampHelper", "failed formatting date - $date")
            null
        }
    }

    fun getCalendar(timestamp: String, format: String = FORMAT): Calendar? {
        val date = getDate(timestamp, format) ?: return null
        return Calendar.getInstance().apply {
            time = date
        }
    }

    fun getFormattedString(timestamp: String, format: String = DEFAULT): String {
        return getDateString(getDate(timestamp), format) ?: "-"
    }
    fun getFormattedString(date: Date, format: String): String {
        return getDateString(date, format) ?: "-"
    }

    operator fun invoke(timestamp: String): String {
        return try {
            val date =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(timestamp)
            if (date != null) SimpleDateFormat(
                "dd MMMM, yyyy",
                Locale.ENGLISH
            ).format(date) else "-"
        } catch (e: Exception) {
            Log.e("ParseTimestampUseCase", "cannot parse $timestamp")
            "-"
        }
    }

    fun getCurrentTimestamp(): String {
        return getDateString(Date())!!
    }

}