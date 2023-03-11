package com.studbudd.application_tracker.feature_applications.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationsDao {

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN createdAt END DESC, " +
            "CASE WHEN :latest_first = 0 THEN createdAt END ASC "
    )
    suspend fun getApplicationsByCreatedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity_Old>

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN modifiedAt END DESC, " +
            "CASE WHEN :latest_first = 0 THEN modifiedAt END ASC "
    )
    suspend fun getApplicationsByModifiedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity_Old>

    @Query("SELECT * FROM applications WHERE id=:id")
    fun getApplication(id: Long): Flow<JobApplicationEntity_Old>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplicationEntity_Old): Long?

    @Update
    suspend fun update(jobApplication: JobApplicationEntity_Old)

    @Query("UPDATE applications SET remoteId=(:remoteId) WHERE id=(:id)")
    suspend fun updateRemoteId(id: Long, remoteId: String)

    @Delete
    suspend fun delete(jobApplication: JobApplicationEntity_Old)

}