package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus

@JsonClass(generateAdapter = true)
data class ApplicationStatusDto(
    @Json(name = "color")
    val color: String,
    @Json(name = "colorNight")
    val colorNight: String,
    @Json(name = "id")
    val id: Long,
    @Json(name = "tag")
    val tag: String,
    @Json(name = "importanceValue")
    val importanceValue: Int
) {

    fun toApplicationStatus(): ApplicationStatus {
        return ApplicationStatus(
            id = id,
            _color = color,
            _colorNight = colorNight,
            _tag = tag,
            importanceValue = importanceValue
        )
    }

}