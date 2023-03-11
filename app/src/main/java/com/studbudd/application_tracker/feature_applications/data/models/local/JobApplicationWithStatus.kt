package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.Embedded
import androidx.room.Relation

data class JobApplicationWithStatus(
    @Embedded
    val application: JobApplicationEntity_Old,
    @Relation(
        parentColumn = "status",
        entityColumn = "id"
    )
    val status: ApplicationStatusEntity
)
