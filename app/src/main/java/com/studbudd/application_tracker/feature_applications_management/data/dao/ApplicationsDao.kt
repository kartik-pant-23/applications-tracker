package com.studbudd.application_tracker.feature_applications_management.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationsDao {

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN created_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN created_at END ASC "
    )
    suspend fun getApplicationsByCreatedDate(latest_first: Boolean, status: List<Int>): List<JobApplication>

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN modified_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN modified_at END ASC "
    )
    suspend fun getApplicationsByModifiedDate(latest_first: Boolean, status: List<Int>): List<JobApplication>

    @Query("SELECT * FROM applications WHERE application_id=:id")
    fun getApplication(id: Int): Flow<JobApplication>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplication): Long

    @Update
    suspend fun update(jobApplication: JobApplication)

    @Delete
    suspend fun delete(jobApplication: JobApplication)

}