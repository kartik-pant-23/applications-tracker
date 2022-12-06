package com.studbudd.application_tracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications_management.data.AppDatabase
import com.studbudd.application_tracker.feature_applications_management.data.ApplicationsRepository
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.repo.UserRepository_Impl
import com.studbudd.application_tracker.feature_user.domain.use_cases.AnonymousSignInUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.GetUserDataUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.RemoteSignInUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.UserUseCases
import com.studbudd.application_tracker.utilities.DATABASE_NAME
import com.studbudd.application_tracker.utilities.SHARED_PREFERENCES_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            DATABASE_NAME,
        ).addMigrations(AppDatabase.Migration_2_3).build()
    }

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return application.applicationContext.getSharedPreferences(
            SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providesSharedPrefManager(sharedPref: SharedPreferences): SharedPreferencesManager {
        return SharedPreferencesManager(sharedPref)
    }

    @AuthRetrofitObject
    @Provides
    fun providesAuthRetrofitObject(prefManager: SharedPreferencesManager): Retrofit {
        val token = prefManager.accessToken ?: ""
        val authClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request =
                chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            chain.proceed(request)
        }.build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(authClient)
            .build()
    }

    @RetrofitObject
    @Provides
    fun providesRetrofitObject(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun providesUserRemoteDao(@RetrofitObject retrofit: Retrofit): UserRemoteDao {
        return retrofit.create(UserRemoteDao::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRepository(
        database: AppDatabase,
        userRemoteDao: UserRemoteDao
    ): UserRepository {
        return UserRepository_Impl(database.userLocalDao(), userRemoteDao)
    }

    @Provides
    fun providesApplicationRepository(database: AppDatabase): ApplicationsRepository {
        return ApplicationsRepository(database.applicationsDao())
    }

    @Provides
    fun providesUserUseCases(
        prefManager: SharedPreferencesManager,
        userRepository: UserRepository
    ): UserUseCases {
        return UserUseCases(
            GetUserDataUseCase(prefManager, userRepository, Dispatchers.IO),
            AnonymousSignInUseCase(prefManager, userRepository, Dispatchers.IO),
            RemoteSignInUseCase(prefManager, userRepository, Dispatchers.IO)
        )
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofitObject

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitObject

}