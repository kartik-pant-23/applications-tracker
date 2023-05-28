package com.studbudd.application_tracker.core.di

import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.core.data.dao.PlaceholderDao
import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository
import com.studbudd.application_tracker.core.domain.repo.PlaceholderRepository_Impl
import com.studbudd.application_tracker.core.domain.usecases.HandleExceptionUseCase
import com.studbudd.application_tracker.core.domain.usecases.placeholder.GetPlaceholderDataUseCase
import com.studbudd.application_tracker.core.domain.usecases.placeholder.PlaceholderUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun providesPlaceholderRepository(
        dao: PlaceholderDao,
        handleException: HandleExceptionUseCase
    ): PlaceholderRepository {
        return PlaceholderRepository_Impl(
            dao = dao,
            handleException = handleException
        )
    }

    @Provides
    fun providesPlaceholderUseCases(
        repo: PlaceholderRepository,
    ): PlaceholderUseCases {
        return PlaceholderUseCases(
            get = GetPlaceholderDataUseCase(repo)
        )
    }

}