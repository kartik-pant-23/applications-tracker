package com.studbudd.application_tracker.feature_user.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_user.domain.models.User
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class UserRemote(
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "_id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "photoUrl")
    val photoUrl: String,
    @Json(name = "placeholderKeys")
    val placeholderKeys: List<String>?,
    @Json(name = "placeholderValues")
    val placeholderValues: List<String>?
) {

    val user
        get() = User(
            remoteId = id,
            name = name,
            email = email,
            photoUrl = photoUrl,
            placeholderKeys = placeholderKeys ?: listOf("resume", "experience-years"),
            placeholderValues = placeholderValues ?: listOf("resume_link", "x_years"),
            createdAt = createdAt
        )

    val localUser: UserLocal
        get() = UserLocal(
            remoteId = id,
            name = name,
            email = email,
            photoUrl = photoUrl,
            placeholderKeys = placeholderKeys ?: listOf("resume", "experience-years"),
            placeholderValues = placeholderValues ?: listOf("resume_link", "x_years"),
            createdAt = createdAt
        )

}