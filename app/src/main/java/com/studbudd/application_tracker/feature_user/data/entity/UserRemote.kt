package com.studbudd.application_tracker.feature_user.data.entity


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_user.domain.models.User

@JsonClass(generateAdapter = true)
data class UserRemote(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "photoUrl")
    val photoUrl: String?,
    @Json(name = "placeholderKeys")
    val placeholderKeys: List<String>?,
    @Json(name = "placeholderValues")
    val placeholderValues: List<String>?,
    @Json(name = "createdAt")
    val createdAt: String,
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