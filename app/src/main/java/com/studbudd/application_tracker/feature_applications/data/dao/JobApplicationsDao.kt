package com.studbudd.application_tracker.feature_applications.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationWithStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationsDao {

    @Query("SELECT * FROM applications_old WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN created_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN created_at END ASC "
    )
    suspend fun getApplicationsByCreatedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity_Old>

    @Query("SELECT * FROM applications_old WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN modified_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN modified_at END ASC "
    )
    suspend fun getApplicationsByModifiedDate(latest_first: Boolean, status: List<Int>): List<JobApplicationEntity_Old>

    @Transaction
    @Query("SELECT * FROM applications ORDER BY createdAt DESC LIMIT (:limit) OFFSET (:offset)")
    fun getApplications(limit: Int, offset: Int): Flow<List<JobApplicationWithStatus>>

    @Transaction
    @Query("SELECT * FROM applications WHERE id=:id")
    fun getApplication(id: Long): Flow<JobApplicationWithStatus>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplicationEntity): Long?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplicationEntity_Old): Long?

    @Update
    suspend fun update(jobApplication: JobApplicationEntity_Old)

    @Query("UPDATE applications SET remoteId=(:remoteId) WHERE id=(:id)")
    suspend fun updateRemoteId(id: Long, remoteId: String)

    @Delete
    suspend fun delete(jobApplication: JobApplicationEntity_Old)

    @Query("DELETE FROM applications")
    suspend fun deleteAllApplications()

}