package com.studbudd.application_tracker.feature_applications.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.studbudd.application_tracker.feature_applications.data.models.local.ApplicationStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationStatusDao {

    @Query("SELECT * FROM application_status")
    fun getAllStatus(): Flow<List<ApplicationStatusEntity>>

}