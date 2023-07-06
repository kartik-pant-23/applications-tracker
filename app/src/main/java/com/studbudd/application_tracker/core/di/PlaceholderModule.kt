package com.studbudd.application_tracker.core.di

import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.core.data.dao.PlaceholderApi
import com.studbudd.application_tracker.core.data.dao.PlaceholderDao
import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository
import com.studbudd.application_tracker.core.domain.repo.PlaceholderRepository_Impl
import com.studbudd.application_tracker.core.domain.usecases.HandleExceptionUseCase
import com.studbudd.application_tracker.core.domain.usecases.placeholder.GetPlaceholderDataUseCase
import com.studbudd.application_tracker.core.domain.usecases.placeholder.PlaceholderUseCases
import com.studbudd.application_tracker.core.domain.usecases.placeholder.UpdatePlaceholderDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlaceholderModule {

    @Provides
    @Singleton
    fun providesPlaceholderDao(db: AppDatabase): PlaceholderDao {
        return db.placeholderDao()
    }

    @Provides
    @Singleton
    fun providesPlaceholderApi(@RetrofitModule.AuthRetrofitObject retrofit: Retrofit): PlaceholderApi {
        return retrofit.create(PlaceholderApi::class.java)
    }

    @Provides
    @Singleton
    fun providesPlaceholderRepository(
        dao: PlaceholderDao,
        api: PlaceholderApi,
        handleException: HandleExceptionUseCase
    ): PlaceholderRepository {
        return PlaceholderRepository_Impl(
            dao = dao,
            api = api,
            handleException = handleException
        )
    }

    @Provides
    fun providesPlaceholderUseCases(
        repo: PlaceholderRepository,
    ): PlaceholderUseCases {
        return PlaceholderUseCases(
            get = GetPlaceholderDataUseCase(repo),
            update = UpdatePlaceholderDataUseCase(repo)
        )
    }

}