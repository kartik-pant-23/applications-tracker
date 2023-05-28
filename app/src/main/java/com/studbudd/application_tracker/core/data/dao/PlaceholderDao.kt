package com.studbudd.application_tracker.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceholderDao {

    @Query("SELECT placeholderMap FROM users where id=1")
    fun getPlaceholderData(): Flow<String>

}