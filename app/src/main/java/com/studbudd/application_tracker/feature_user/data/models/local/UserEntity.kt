package com.studbudd.application_tracker.feature_user.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.feature_user.domain.models.User

@Entity(tableName = "users")
data class UserEntity(
    val remoteId: String? = null,
    val name: String,
    val email: String? = null,
    val photoUrl: String? = null,
    val placeholderKeys: List<String> = emptyList(),
    val placeholderValues: List<String> = emptyList(),
    val createdAt: String? = null,
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toUser(): User {
        return User(
            remoteId,
            name,
            email ?: "-",
            photoUrl,
            placeholderKeys,
            placeholderValues,
            createdAt
        )
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is UserEntity -> {
                remoteId == other.remoteId &&
                        name == other.name &&
                        email == other.email &&
                        photoUrl == other.photoUrl &&
                        placeholderKeys.toString() == other.placeholderKeys.toString() &&
                        placeholderValues.toString() == other.placeholderValues.toString() &&
                        createdAt == other.createdAt
            }
            else -> {
                false
            }
        }
    }

    override fun hashCode(): Int {
        var result = remoteId?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (photoUrl?.hashCode() ?: 0)
        result = 31 * result + placeholderKeys.toString().hashCode()
        result = 31 * result + placeholderValues.toString().hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + id
        return result
    }

}