package com.studbudd.application_tracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.common.domain.ClearAppDataUseCase
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_applications_management.data.AppDatabase
import com.studbudd.application_tracker.feature_applications_management.data.ApplicationsRepository
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserRemoteDao
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.repo.UserRepository_Impl
import com.studbudd.application_tracker.feature_user.domain.use_cases.CreateLocalUserUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.CreateRemoteUserUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.GetUserDataUseCase
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
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            BuildConfig.SHARED_PREF_FILE_NAME,
            masterKeyAlias,
            application.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun providesSharedPrefManager(sharedPref: SharedPreferences): SharedPreferencesManager {
        return SharedPreferencesManager(sharedPref)
    }

    @Provides
    @Singleton
    fun providesGoogleSignInClient(application: Application): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_CLIENT_ID)
            .build()
        return GoogleSignIn.getClient(application.applicationContext, gso)
    }

    @Provides
    @Singleton
    fun providesClearAppDataUseCase(
        prefManager: SharedPreferencesManager,
        database: AppDatabase
    ): ClearAppDataUseCase {
        return ClearAppDataUseCase(prefManager, database)
    }

    @AuthRetrofitObject
    @Provides
    fun providesAuthRetrofitObject(prefManager: SharedPreferencesManager): Retrofit {
        val token = prefManager.accessToken ?: ""
        val authClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request =
                chain.request().newBuilder()
                    .addHeader("token", "Bearer $token")
                    .addHeader("x-device-type", "android").build()
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
    @Singleton
    fun providesRetrofitObject(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-device-type", "android").build()
            chain.proceed(request)
        }.build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun providesUserRemoteDao(@RetrofitObject retrofit: Retrofit): UserRemoteDao {
        return retrofit.create(UserRemoteDao::class.java)
    }

    @Provides
    fun providesAuthUserRemoteDao(@AuthRetrofitObject retrofit: Retrofit): AuthUserRemoteDao {
        return retrofit.create(AuthUserRemoteDao::class.java)
    }

    @Provides
    fun providesUserRepository(
        database: AppDatabase,
        userRemoteDao: UserRemoteDao,
        authUserRemoteDao: AuthUserRemoteDao,
        prefManager: SharedPreferencesManager
    ): UserRepository {
        return UserRepository_Impl(
            database.userLocalDao(),
            userRemoteDao,
            authUserRemoteDao,
            prefManager
        )
    }

    @Provides
    fun providesApplicationRepository(database: AppDatabase): ApplicationsRepository {
        return ApplicationsRepository(database.applicationsDao())
    }

    @Provides
    fun providesUserUseCases(
        userRepository: UserRepository
    ): UserUseCases {
        return UserUseCases(
            GetUserDataUseCase(userRepository, Dispatchers.IO),
            CreateLocalUserUseCase(userRepository, Dispatchers.IO),
            CreateRemoteUserUseCase(userRepository, Dispatchers.IO)
        )
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofitObject

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitObject

}