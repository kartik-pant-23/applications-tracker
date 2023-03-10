package com.studbudd.application_tracker.feature_user.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.domain.models.User

@JsonClass(generateAdapter = true)
data class UserDto(
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

    fun toUserEntity(): UserEntity {
        return UserEntity(
            remoteId = id,
            name = name,
            email = email,
            photoUrl = photoUrl,
            placeholderKeys = placeholderKeys ?: emptyList(),
            placeholderValues = placeholderValues ?: emptyList(),
            createdAt = createdAt
        )
    }

}