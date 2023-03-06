package com.studbudd.application_tracker.feature_user.data.dao

import androidx.room.*
import com.studbudd.application_tracker.feature_user.data.models.local.RemoteIdTuple
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id=1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT remoteId FROM users WHERE id=1")
    suspend fun getRemoteId(): RemoteIdTuple?

    @Update
    suspend fun updateUser(newUser: UserEntity)

    @Query("DELETE FROM users WHERE id=1")
    fun deleteLocalUser()

}