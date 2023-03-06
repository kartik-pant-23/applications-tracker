package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.common.data.AppDatabase
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsRemoteDao
import com.studbudd.application_tracker.feature_applications_management.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications_management.domain.repo.JobApplicationsRepository_Impl
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
        remoteDao: JobApplicationsRemoteDao
    ): JobApplicationsRepository {
        return JobApplicationsRepository_Impl(localDao, remoteDao)
    }

}