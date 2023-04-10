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

    fun getRelativeTime(timestamp: String): String {
        getDate(timestamp)?.let {  date ->
            val diff = Date().time - date.time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = days / 30
            val years = months / 12

            if (years >= 1) return "more than ${if (years == 1L) "an year" else "$years years"} ago"
            if (months >= 1) return "more than ${if (months == 1L) "a month" else "$months months"} ago"
            if (weeks >= 1) return "around ${if (weeks == 1L) "a week" else "$weeks weeks"} ago"
            if (days >= 1) return if (days == 1L) "yesterday" else "$days days back"
            if (hours >= 1) return "around ${if (hours == 1L) "an hour" else "$hours hours ago"}"
            if (minutes >= 1) return "${if (minutes <= 10) "a few" else "$minutes"} minutes ago"
            return "just now"

        } ?: return ""
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