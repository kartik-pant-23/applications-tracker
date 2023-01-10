package com.studbudd.application_tracker.feature_applications_management.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "applications")
data class JobApplication(
    val company_name: String,
    val role: String,
    var notes: String?,
    var jobLink: String,
    val created_at: Calendar = Calendar.getInstance(),
    var modified_at: Calendar = Calendar.getInstance(),
    var status: Int = 0
) {

    @PrimaryKey(autoGenerate = true)
    var application_id: Int = 0

    val title
        get() = company_name.plus(" - ").plus(role)

    override fun toString(): String {
        return """
            { "_id": ${application_id} "title": ${title}, "jobLink": ${jobLink}, "status": ${status}
        """.trimIndent()
    }

}