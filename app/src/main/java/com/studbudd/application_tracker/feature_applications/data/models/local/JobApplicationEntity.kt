package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.core.utils.TimestampHelper
import java.util.*

@Entity(tableName = "applications")
data class JobApplicationEntity(
    @ColumnInfo(name = "company_name")
    val companyName: String,
    val role: String,
    val notes: String?,
    val jobLink: String,
    @ColumnInfo(name = "created_at")
    val createdAtCalendar: Calendar = Calendar.getInstance(),
    val status: Int = 0,
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
    var id: Int = 0

    val createdAt
        get() = TimestampHelper.getDateString(createdAtCalendar.time)

    val modifiedAt
        get() = TimestampHelper.getDateString(modifiedAtCalendar.time)

    val title
        get() = companyName.plus(" - ").plus(role)

    override fun toString(): String {
        return """
            { "_id": $id "title": ${title}, "jobLink": ${jobLink}, "status": $status
        """.trimIndent()
    }

}