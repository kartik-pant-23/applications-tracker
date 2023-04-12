package com.studbudd.application_tracker.core.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.core.domain.ClearAppDataUseCase
import com.studbudd.application_tracker.core.domain.SharedPreferencesManager
import com.studbudd.application_tracker.core.data.AppDatabase
import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.core.domain.PrefillApplicationStatus
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import com.studbudd.application_tracker.core.utils.DATABASE_NAME
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        ).addMigrations(
            AppDatabase.Migration_2_3,
            AppDatabase.Migration_3_4,
            AppDatabase.Migration_4_5,
            AppDatabase.Migration_5_6
        )
            .addCallback(PrefillApplicationStatus())
            .build()
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
        userDao: UserDao,
        applicationsDao: JobApplicationsDao
    ): ClearAppDataUseCase {
        return ClearAppDataUseCase(prefManager, userDao, applicationsDao)
    }

    @Provides
    @Singleton
    fun provideHandleApiCallUseCase(userDao: UserDao): HandleApiCall {
        return HandleApiCall(userDao)
    }


}