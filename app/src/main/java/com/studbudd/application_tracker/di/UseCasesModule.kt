package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.feature_applications_management.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications_management.domain.use_cases.AddJobApplicationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCasesModule {

    @Provides
    @Singleton
    fun providesAddJobApplicationUseCase(repository: JobApplicationsRepository): AddJobApplicationUseCase {
        return AddJobApplicationUseCase(repository)
    }

}