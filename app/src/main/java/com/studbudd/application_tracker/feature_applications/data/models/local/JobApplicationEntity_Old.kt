package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.Job
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import java.util.*

//foreignKeys = [
//ForeignKey(
//entity = ApplicationStatusEntity::class,
//parentColumns = arrayOf("id"),
//childColumns = arrayOf("status"),
//onDelete = ForeignKey.SET_NULL,
//onUpdate = ForeignKey.CASCADE
//)
//]
@Entity(
    tableName = "applications_old",
)
data class JobApplicationEntity_Old(
    @ColumnInfo(name = "company_name")
    val companyName: String,
    val role: String,
    val notes: String?,
    val jobLink: String,
    @ColumnInfo(name = "created_at")
    val createdAtCalendar: Calendar = Calendar.getInstance(),
    val status: Long = 0,
    @ColumnInfo(name = "remote_id")
    val remoteId: String? = null,
    @ColumnInfo(name = "company_logo")
    val companyLogo: String? = null,
    @ColumnInfo(name = "application_deadline")
    val applicationDeadline: String? = null,
    @ColumnInfo(name = "modified_at")
    val modifiedAtCalendar: Calendar = Calendar.getInstance()

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "application_id")
    var id: Long = 0

    val createdAt
        get() = TimestampHelper.getDateString(createdAtCalendar.time)

    val modifiedAt
        get() = TimestampHelper.getDateString(modifiedAtCalendar.time)

    val title
        get() = companyName.plus(" - ").plus(role)

    fun toJobApplication(): JobApplication {
        return JobApplication(
            id = id,
            job = Job(
                company = companyName,
                role = role,
                url = jobLink,
                companyLogo = companyLogo,
                _deadline = applicationDeadline
            ),
            status = ApplicationStatus(
                id = status,
                _tag = "waiting for referral",
                _color = "#000",
                _colorNight = "#FFF",
                importanceValue = 0
            ),
            notes = notes,
            createdAt = createdAt ?: "-",
            modifiedAt = modifiedAt ?: "-"
        )
    }

    override fun toString(): String {
        return """
            { "_id": $id "title": ${title}, "jobLink": ${jobLink}, "status": $status
        """.trimIndent()
    }

}