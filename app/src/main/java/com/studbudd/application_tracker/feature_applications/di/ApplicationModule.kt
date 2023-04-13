package com.studbudd.application_tracker.feature_applications.di

import android.app.Application
import androidx.work.WorkManager
import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.core.di.RetrofitModule
import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.core.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications.data.dao.ApplicationStatusDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsApi
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.repo.JobApplicationsRepository_Impl
import com.studbudd.application_tracker.feature_applications.domain.use_cases.*
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
    fun providesApplicationStatusDao(db: AppDatabase): ApplicationStatusDao {
        return db.applicationStatusDao()
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
        userLocalDao: UserDao,
        localDao: JobApplicationsDao,
        applicationStatusDao: ApplicationStatusDao,
        remoteDao: JobApplicationsApi,
        handleApiCall: HandleApiCall
    ): JobApplicationsRepository {
        return JobApplicationsRepository_Impl(
            dao = localDao,
            applicationStatusDao = applicationStatusDao,
            api = remoteDao,
            handleApiCall = handleApiCall,
            userDao = userLocalDao
        )
    }

    @Provides
    @Singleton
    fun provideJobApplicationsUseCase(
        repo: JobApplicationsRepository,
        application: Application
    ): ApplicationsUseCase {
        val handleNotification =
            HandlePeriodicNotification(WorkManager.getInstance(application.applicationContext))
        return ApplicationsUseCase(
            create = CreateJobApplicationUseCase(
                repo,
                handleNotification
            ),
            get = GetJobApplicationsUseCase(repo),
            getApplicationStatus = GetApplicationStatusUseCase(repo),
            getDetails = GetJobApplicationDetailsUseCase(repo),
            update = UpdateJobApplicationUseCase(repo, handleNotification),
            delete = DeleteJobApplicationUseCase(repo, handleNotification)
        )
    }

}