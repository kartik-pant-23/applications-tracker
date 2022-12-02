package com.studbudd.application_tracker.feature_user.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studbudd.application_tracker.feature_user.data.models.UserLocal

@Dao
interface UserLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewUser(user: UserLocal): Long

    @Query("SELECT * FROM users WHERE id=1")
    suspend fun getUser(): UserLocal?

    @Update
    suspend fun updateUser(newUser: UserLocal)

}