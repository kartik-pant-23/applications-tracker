package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsRemoteDao
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.repo.JobApplicationsRepository_Impl
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideJobApplicationsRepository(
        localDao: JobApplicationsDao,
        remoteDao: JobApplicationsRemoteDao,
        userDao: UserDao
    ): JobApplicationsRepository {
        return JobApplicationsRepository_Impl(localDao, remoteDao, HandleApiCall(userDao))
    }

}