package com.studbudd.application_tracker.feature_user.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.feature_user.domain.models.User

@Entity(tableName = "users")
data class UserLocal(
    val name: String,
    val placeholderKeys: List<String> = listOf("resume", "experience-years"),
    val placeholderValues: List<String> = listOf("resume_link", "x_years")
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    val user
        get() = User(name, "-", null, placeholderKeys, placeholderValues, null)

}