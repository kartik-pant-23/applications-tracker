package com.studbudd.application_tracker.core.domain.usecases

import com.studbudd.application_tracker.core.utils.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearAppDataUseCase @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val usersDao: UserDao,
    private val applicationsDao: JobApplicationsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        usersDao.delete()
        applicationsDao.deleteAllApplications()
        sharedPreferencesManager.clearAllData()
    }
}