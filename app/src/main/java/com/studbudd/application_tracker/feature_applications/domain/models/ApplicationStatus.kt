package com.studbudd.application_tracker.feature_applications.domain.models

import android.graphics.Color

data class ApplicationStatus(
    val tag: String,
    private val _color: String,
    val importanceValue: Int
) {

    val color
        get() = Color.parseColor(_color)

}