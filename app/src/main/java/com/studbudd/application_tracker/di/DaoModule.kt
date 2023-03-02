package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsRemoteDao
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
    fun providesJobApplicationsRemoteDao(@AppModule.AuthRetrofitObject retrofit: Retrofit): JobApplicationsRemoteDao {
        return retrofit.create(JobApplicationsRemoteDao::class.java)
    }

}