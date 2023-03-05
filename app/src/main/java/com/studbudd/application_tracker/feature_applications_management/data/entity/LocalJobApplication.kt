package com.studbudd.application_tracker.feature_applications_management.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "applications")
data class JobApplication(
    @ColumnInfo(name = "company_name") val companyName: String,
    val role: String,
    val notes: String?,
    val jobLink: String,
    @ColumnInfo(name = "created_at") val createdAt: Calendar = Calendar.getInstance(),
    val status: Int = 0,
    @ColumnInfo(name = "remote_id") val remoteId: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "application_id")
    var id: Int = 0

    @ColumnInfo(name = "modified_at")
    var modifiedAt: Calendar = Calendar.getInstance()

    val title
        get() = companyName.plus(" - ").plus(role)

    override fun toString(): String {
        return """
            { "_id": $id "title": ${title}, "jobLink": ${jobLink}, "status": $status
        """.trimIndent()
    }

}