package com.studbudd.application_tracker.feature_user.data.models.local

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.studbudd.application_tracker.core.domain.usecases.MapConverterUseCase
import com.studbudd.application_tracker.feature_user.domain.models.User

@Entity(tableName = "users")
data class UserEntity(
    val remoteId: String? = null,
    val name: String,
    val email: String? = null,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val placeholderMap: Map<String,String> = mapOf()
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toUser(): User {
        return User(
            remoteId = remoteId,
            name = name,
            email = email ?: "-",
            photoUrl = photoUrl,
            placeholderMap = placeholderMap,
            createdAt = createdAt
        )
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is UserEntity -> {
                remoteId == other.remoteId &&
                        name == other.name &&
                        email == other.email &&
                        placeholderMap.toString() == other.placeholderMap.toString() &&
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
        result = 31 * result + placeholderMap.toString().hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + id
        return result
    }

}