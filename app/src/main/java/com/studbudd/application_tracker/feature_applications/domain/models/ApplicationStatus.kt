package com.studbudd.application_tracker.feature_applications.domain.models

import android.graphics.Color

data class ApplicationStatus(
    private val _tag: String,
    private val _color: String,
    private val _colorNight: String? = null,
    val importanceValue: Int
) {

    val color
        get() = Color.parseColor(_color)

    val colorNight
        get() = _colorNight?.let { Color.parseColor(it) } ?: color

    val tag
        get() = _tag.split(" ")
            .joinToString(" ") { word -> word.replaceFirstChar { ch -> ch.uppercaseChar() } }

    companion object {
        const val WAITING_FOR_REFERRAL = 0
        const val APPLIED = 1
        const val APPLIED_WITH_REFERRAL = 2
        const val SELECTED = 3
        const val REJECTED = 4
    }

}