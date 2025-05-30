package ru.ivan.eremin.treningtest.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ivan.eremin.treningtest.data.repository.TrainingRepositoryImpl
import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindTrainingRepository(repository: TrainingRepositoryImpl): TrainingRepository
}