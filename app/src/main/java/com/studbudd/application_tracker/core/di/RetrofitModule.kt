package com.studbudd.application_tracker.core.di

import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.core.data.dao.RefreshTokenApi
import com.studbudd.application_tracker.core.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @RetrofitObject
    @Provides
    @Singleton
    fun provideRetrofitObject(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("x-device-type", "android").build()
                chain.proceed(request)
            }.build())
            .build()
    }

    @RefreshAuthRetrofitObject
    @Provides
    fun providesRefreshAuthRetrofitObject(preferencesManager: SharedPreferencesManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .headers(
                        Headers.of(
                            mapOf(
                                "x-device-type" to "android",
                                "token" to "${preferencesManager.refreshToken}"
                            )
                        )
                    )
                    .build()
                chain.proceed(request)
            }.build())
            .build()
    }

    @AuthRetrofitObject
    @Provides
    fun provideAuthRetrofitObject(
        @RefreshAuthRetrofitObject retrofit: Retrofit,
        preferencesManager: SharedPreferencesManager
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().authenticator { _, response ->
                if (response.code() == 401) {
                    val data = runBlocking(Dispatchers.IO) {
                        val res = retrofit.create(RefreshTokenApi::class.java).refreshAuthTokens()
                        res.body()?.data
                    }
                    return@authenticator if (data != null) {
                        preferencesManager.accessToken = data.accessToken
                        preferencesManager.refreshToken = data.refreshToken
                        response.request().newBuilder()
                            .headers(
                                Headers.of(
                                    mapOf(
                                        "x-device-type" to "android",
                                        "token" to "Bearer ${preferencesManager.accessToken}"
                                    )
                                )
                            ).build()
                    } else {
                        null
                    }
                }
                null
            }
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .headers(
                            Headers.of(
                                mapOf(
                                    "x-device-type" to "android",
                                    "token" to "Bearer ${preferencesManager.accessToken}"
                                )
                            )
                        )
                        .build()
                    chain.proceed(request)
                }.build())
            .build()
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofitObject

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RefreshAuthRetrofitObject

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitObject

}