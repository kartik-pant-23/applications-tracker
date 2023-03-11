package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "applications",
    foreignKeys = [
        ForeignKey(
            entity = ApplicationStatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val remoteId: String?,
    val company: String,
    val companyLogo: String?,
    val role: String,
    val jobLink: String,
    val notes: String?,
    val status: Long,
    val applicationDeadline: String?,
    val createdAt: String,
    val modifiedAt: String
)
