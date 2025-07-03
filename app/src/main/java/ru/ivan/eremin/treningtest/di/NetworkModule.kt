package ru.ivan.eremin.treningtest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.ivan.eremin.treningtest.data.cache.AppCache
import ru.ivan.eremin.treningtest.data.service.network.TrainingService
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun baseOkHttpBuilder(): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
    }

    @Singleton
    @Provides
    fun retrofit(
        okhttp: OkHttpClient,
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .baseUrl("")
            .client(okhttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    }

    @Provides
    fun scope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Singleton
    @Provides
    fun getCache(): AppCache {
        return AppCache()
    }
/*
    @Provides
    fun provideTrainingService(retrofit: Retrofit): TrainingService =
        retrofit.create(TrainingService::class.java)*/
}
