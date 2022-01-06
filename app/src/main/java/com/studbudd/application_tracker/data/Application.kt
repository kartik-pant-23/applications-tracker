package com.studbudd.application_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.util.Calendar.DAY_OF_YEAR

@Entity(tableName = "applications")
data class Application(
    val company_name: String,
    val role: String,
    var notes: String?,
    var jobLink: String,
    val created_at: Calendar = Calendar.getInstance(),
    var modified_at: Calendar = Calendar.getInstance(),
    val notify_after: Int = 7,
    var status: Int = 0
) {

    @PrimaryKey(autoGenerate = true)
    var application_id: Int = 0

    fun shouldNotify(since: Calendar): Boolean {
        return since > modified_at.apply { add(DAY_OF_YEAR, notify_after) }
    }

    val title
        get() = company_name.plus(" - ").plus(role)

    override fun toString(): String {
        return """
            { "title": ${title}, "jobLink": ${jobLink}, "status": ${status}
        """.trimIndent()
    }

}