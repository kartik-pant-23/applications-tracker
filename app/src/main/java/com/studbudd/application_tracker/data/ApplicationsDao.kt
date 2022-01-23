package com.studbudd.application_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationsDao {

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN created_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN created_at END ASC "
    )
    suspend fun getApplicationsByCreatedDate(latest_first: Boolean, status: List<Int>): List<Application>

    @Query("SELECT * FROM applications WHERE status in (:status) ORDER BY " +
            "CASE WHEN :latest_first = 1 THEN modified_at END DESC, " +
            "CASE WHEN :latest_first = 0 THEN modified_at END ASC "
    )
    suspend fun getApplicationsByModifiedDate(latest_first: Boolean, status: List<Int>): List<Application>

    @Query("SELECT * FROM applications WHERE application_id=:id")
    fun getApplication(id: Int): Flow<Application>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(application: Application): Long

    @Update
    suspend fun update(application: Application)

    @Delete
    suspend fun delete(application: Application)

}