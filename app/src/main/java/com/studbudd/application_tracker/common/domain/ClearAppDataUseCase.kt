package com.studbudd.application_tracker.common.domain

import com.studbudd.application_tracker.feature_applications_management.data.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearAppDataUseCase @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val database: AppDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        database.clearAllTables()
        sharedPreferencesManager.clearAllData()
    }
}