package com.studbudd.application_tracker.feature_applications.data.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus

@Entity(
    tableName = "application_status"
)
data class ApplicationStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val tag: String,
    val color: String,
    val colorNight: String,
    val importanceValue: Int
) {

    fun toApplicationStatus(): ApplicationStatus {
        return ApplicationStatus(
            id = id,
            _tag = tag,
            _color = color,
            _colorNight = colorNight,
            importanceValue = importanceValue
        )
    }

}
