package com.studbudd.application_tracker.feature_applications.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationWithStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationsDao {

    @Transaction
    @Query("SELECT * FROM applications ORDER BY createdAt DESC LIMIT (:limit) OFFSET (:offset)")
    fun getApplications(limit: Int, offset: Int): Flow<List<JobApplicationWithStatus>>

    @Transaction
    @Query("SELECT * FROM applications WHERE id=:id")
    fun getApplication(id: Long): Flow<JobApplicationWithStatus?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jobApplication: JobApplicationEntity): Long?

    @Update
    suspend fun update(jobApplication: JobApplicationEntity_Old)

    @Query("UPDATE applications SET remoteId=(:remoteId) WHERE id=(:id)")
    suspend fun updateRemoteId(id: Long, remoteId: String)

    @Delete
    suspend fun delete(jobApplication: JobApplicationEntity_Old)

    @Query("DELETE FROM applications")
    suspend fun deleteAllApplications()

}