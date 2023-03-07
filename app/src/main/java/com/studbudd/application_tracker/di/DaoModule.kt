package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsRemoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    @Singleton
    fun providesJobApplicationsLocalDao(database: AppDatabase): JobApplicationsDao {
        return database.applicationsDao()
    }

    @Provides
    @Singleton
    fun providesJobApplicationsRemoteDao(@RetrofitModule.AuthRetrofitObject retrofit: Retrofit): JobApplicationsRemoteDao {
        return retrofit.create(JobApplicationsRemoteDao::class.java)
    }

}