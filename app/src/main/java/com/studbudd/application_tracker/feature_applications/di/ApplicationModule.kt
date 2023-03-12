package com.studbudd.application_tracker.feature_applications.di

import android.app.Application
import androidx.work.WorkManager
import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.core.di.RetrofitModule
import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.core.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsApi
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.repo.JobApplicationsRepository_Impl
import com.studbudd.application_tracker.feature_applications.domain.use_cases.ApplicationsUseCase
import com.studbudd.application_tracker.feature_applications.domain.use_cases.CreateJobApplicationUseCase
import com.studbudd.application_tracker.feature_applications.domain.use_cases.CreatePeriodicNotifications
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun providesJobApplicationsDao(db: AppDatabase): JobApplicationsDao {
        return db.applicationsDao()
    }

    @Provides
    @Singleton
    fun providesJobApplicationsApi(
        @RetrofitModule.AuthRetrofitObject retrofit: Retrofit
    ): JobApplicationsApi {
        return retrofit.create(JobApplicationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideJobApplicationsRepository(
        localDao: JobApplicationsDao,
        remoteDao: JobApplicationsApi,
        prefs: SharedPreferencesManager,
        handleApiCall: HandleApiCall
    ): JobApplicationsRepository {
        return JobApplicationsRepository_Impl(
            dao = localDao,
            api = remoteDao,
            handleApiCall = handleApiCall,
            isRemoteUser = prefs.accessToken != null && prefs.refreshToken != null
        )
    }

    @Provides
    @Singleton
    fun provideJobApplicationsUseCase(
        repo: JobApplicationsRepository,
        application: Application
    ): ApplicationsUseCase {
        return ApplicationsUseCase(
            create = CreateJobApplicationUseCase(
                repo,
                CreatePeriodicNotifications(WorkManager.getInstance(application.applicationContext))
            )
        )
    }

}