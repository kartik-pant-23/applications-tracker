package com.studbudd.application_tracker.common.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Returns the timestamp string parsed into date string into the format
 * of `[dd MMMM, yyyy]`.
 *
 * @param timestamp string in the format `[yyyy-MM-dd'T'HH:mm:ss.SSS'Z']`
 * as needs to come from the backend
 */
class ParseTimestampUseCase(private val timestamp: String) {

    operator fun invoke(): String {
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
}