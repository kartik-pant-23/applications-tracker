package com.studbudd.application_tracker.common.domain

import android.util.Log

class HandleException {
    operator fun invoke(TAG: String, e: Exception) {
        Log.e(TAG, "exception: $e")
    }
}