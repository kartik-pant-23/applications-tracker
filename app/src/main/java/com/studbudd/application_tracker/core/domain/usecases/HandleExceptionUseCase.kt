package com.studbudd.application_tracker.core.domain.usecases

import android.util.Log

class HandleExceptionUseCase {
    operator fun invoke(TAG: String, e: Exception) {
        Log.e(TAG, "exception: $e")
    }
}