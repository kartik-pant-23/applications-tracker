package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.Embedded
import androidx.room.Relation
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.Job
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

data class JobApplicationWithStatus(
    @Embedded
    val application: JobApplicationEntity,
    @Relation(
        parentColumn = "status",
        entityColumn = "id"
    )
    val status: ApplicationStatusEntity
) {

    fun toJobApplication(): JobApplication {
        return JobApplication(
            id = application.id,
            job = Job(
                company = application.company,
                companyLogo = application.companyLogo,
                role = application.role,
                url = application.jobLink,
                _deadline = application.applicationDeadline
            ),
            status = ApplicationStatus(
                id = status.id,
                _tag = status.tag,
                _color = status.color,
                _colorNight = status.colorNight,
                importanceValue = status.importanceValue
            ),
            notes = application.notes,
            createdAt = application.createdAt,
            modifiedAt = application.modifiedAt
        )
    }

}
