package com.studbudd.application_tracker.feature_applications.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationsDao {

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN created_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN created_at END ASC "
    )
    suspend fun getApplicationsByCreatedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity>

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN modified_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN modified_at END ASC "
    )
    suspend fun getApplicationsByModifiedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity>

    @Query("SELECT * FROM applications WHERE application_id=:id")
    fun getApplication(id: Int): Flow<JobApplicationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplicationEntity): Long?

    @Update
    suspend fun update(jobApplication: JobApplicationEntity)

    @Query("UPDATE applications SET remote_id=(:remoteId) WHERE application_id=(:id)")
    suspend fun updateRemoteId(id: Long, remoteId: String)

    @Delete
    suspend fun delete(jobApplication: JobApplicationEntity)

}