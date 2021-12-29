package com.studbudd.application_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationsDao {

    @Query("SELECT * FROM applications ORDER BY application_id DESC")
    fun getAllApplications(): Flow<List<Application>>

    @Query("SELECT * FROM applications WHERE application_id=:id")
    fun getApplication(id: Int): Flow<Application>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(application: Application)

    @Delete
    suspend fun delete(application: Application)

}