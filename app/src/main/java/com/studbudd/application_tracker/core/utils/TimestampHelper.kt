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

    const val MILLISECONDS = "milliseconds"
    const val SECONDS = "seconds"
    const val MINUTES = "minutes"
    const val HOURS = "hours"
    const val DAYS = "days"
    const val WEEKS = "weeks"
    const val MONTHS = "months"
    const val YEARS = "years"

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
        getDate(timestamp)?.let { date ->
            val diff = Date().time - date.time
            val timeMap = getTimeMap(diff)

            if (timeMap[YEARS]!! >= 1L)
                return "more than ${if (timeMap[YEARS] == 1L) "an year" else "${timeMap[YEARS]} years"} ago"
            if (timeMap[MONTHS]!! >= 1L)
                return "more than ${if (timeMap[MONTHS] == 1L) "a month" else "${timeMap[MONTHS]} months"} ago"
            if (timeMap[WEEKS]!! >= 1L)
                return "around ${if (timeMap[WEEKS] == 1L) "a week" else "${timeMap[WEEKS]} weeks"} ago"
            if (timeMap[DAYS]!! >= 1L)
                return if (timeMap[DAYS] == 1L) "yesterday" else "${timeMap[DAYS]} days ago"
            if (timeMap[HOURS]!! >= 1L)
                return "around ${if (timeMap[HOURS] == 1L) "an hour" else "${timeMap[HOURS]} hours ago"}"
            if (timeMap[MINUTES]!! >= 1L)
                return "${if (timeMap[MINUTES]!! <= 10) "a few" else "${timeMap[MINUTES]}"} minutes ago"
            return "just now"

        } ?: return ""
    }

    private fun getTimeMap(time: Long): Map<String, Long> {
        val seconds = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = months / 12

        return mapOf(
            Pair(MILLISECONDS, time),
            Pair(SECONDS, seconds),
            Pair(MINUTES, minutes),
            Pair(HOURS, hours),
            Pair(DAYS, days),
            Pair(WEEKS, weeks),
            Pair(MONTHS, months),
            Pair(YEARS, years)
        )
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