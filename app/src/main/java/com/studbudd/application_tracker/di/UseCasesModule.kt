package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.core.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.use_cases.ApplicationsUseCase
import com.studbudd.application_tracker.feature_applications.domain.use_cases.CreateJobApplicationUseCase
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
    fun providesApplicationsUseCase(
        repo: JobApplicationsRepository,
        prefs: SharedPreferencesManager
    ): ApplicationsUseCase {
        return ApplicationsUseCase(
            create = CreateJobApplicationUseCase(
                repo,
                prefs.accessToken != null && prefs.refreshToken != null
            )
        )
    }

}