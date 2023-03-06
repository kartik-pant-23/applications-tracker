package com.studbudd.application_tracker.di

import com.studbudd.application_tracker.common.data.AppDatabase
import com.studbudd.application_tracker.common.domain.HandleApiCall
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserApi
import com.studbudd.application_tracker.feature_user.data.dao.UserApi
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.repo.UserRepository_Impl
import com.studbudd.application_tracker.feature_user.domain.use_cases.CreateLocalUserUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.CreateRemoteUserUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.GetUserDataUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userLocalDao()
    }

    @Provides
    @Singleton
    fun provideUserApi(@RetrofitModule.RetrofitObject retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserAuthApi(@RetrofitModule.AuthRetrofitObject retrofit: Retrofit): AuthUserApi {
        return retrofit.create(AuthUserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        dao: UserDao,
        api: UserApi,
        authApi: AuthUserApi,
        prefs: SharedPreferencesManager,
        handleApiCall: HandleApiCall
    ): UserRepository {
        return UserRepository_Impl(
            dao = dao,
            api = api,
            authApi = authApi,
            preferencesManager = prefs,
            handleApiCall = handleApiCall
        )
    }

    @Provides
    @Singleton
    fun provideUserUseCase(repository: UserRepository): UserUseCases {
        return UserUseCases(
            create = CreateLocalUserUseCase(repository),
            login = CreateRemoteUserUseCase(repository),
            get = GetUserDataUseCase(repository)
        )
    }

}