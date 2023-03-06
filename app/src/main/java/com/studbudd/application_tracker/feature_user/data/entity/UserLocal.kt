package com.studbudd.application_tracker.feature_user.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.feature_user.domain.models.User

@Entity(tableName = "users")
data class UserLocal(
    val remoteId: String? = null,
    val name: String,
    val email: String? = null,
    val photoUrl: String? = null,
    val placeholderKeys: List<String> = listOf("resume", "experience-years"),
    val placeholderValues: List<String> = listOf("resume_link", "x_years"),
    val createdAt: String? = null,
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    val user
        get() = User(
            remoteId,
            name,
            email ?: "-",
            photoUrl,
            placeholderKeys,
            placeholderValues,
            createdAt
        )

}