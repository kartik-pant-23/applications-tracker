package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

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
    val id: Long = 0,
    val remoteId: String? = null,
    val company: String,
    val companyLogo: String? = null,
    val role: String,
    val jobLink: String,
    val notes: String? = null,
    val status: Long,
    val applicationDeadline: String? = null,
    val createdAt: String = TimestampHelper.getCurrentTimestamp(),
    val modifiedAt: String = TimestampHelper.getCurrentTimestamp()
)