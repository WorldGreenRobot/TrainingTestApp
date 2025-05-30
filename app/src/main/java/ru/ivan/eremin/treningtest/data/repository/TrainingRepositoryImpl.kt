package ru.ivan.eremin.treningtest.data.repository

import kotlinx.coroutines.flow.Flow
import ru.ivan.eremin.treningtest.data.cache.AppCache
import ru.ivan.eremin.treningtest.data.mapper.TrainingMapper.mapToDomain
import ru.ivan.eremin.treningtest.data.service.extension.RequestHelper
import ru.ivan.eremin.treningtest.data.service.network.TrainingService
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val service: TrainingService,
    private val requestHelper: RequestHelper,
    cache: AppCache
): TrainingRepository{

    private val trainigCache = cache.workoutCache

    private val videoWorkout = cache.videoWorkout

    override fun getWorkouts(fromCache: Boolean): Flow<ResultFlow<List<Workout>>>{
        return requestHelper.getCacheOrNetwork(trainigCache, fromCache) {
            service.getWorkouts().mapToDomain()
        }
    }

    override fun getVideoWorkout(id: Int, fromCache: Boolean): Flow<ResultFlow<VideoWorkout>> {
        return requestHelper.getCacheOrNetwork (videoWorkout, fromCache) {
            service.getVideo(id).mapToDomain()
        }
    }
}