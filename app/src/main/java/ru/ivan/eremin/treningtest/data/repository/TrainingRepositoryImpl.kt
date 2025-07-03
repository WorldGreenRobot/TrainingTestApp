package ru.ivan.eremin.treningtest.data.repository

import kotlinx.coroutines.flow.Flow
import ru.ivan.eremin.treningtest.data.cache.AppCache
import ru.ivan.eremin.treningtest.data.mapper.TrainingMapper.mapToDomain
import ru.ivan.eremin.treningtest.data.service.extension.RequestHelper
import ru.ivan.eremin.treningtest.data.service.network.TrainingService
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    //private val service: TrainingService,
    private val requestHelper: RequestHelper,
    cache: AppCache
): TrainingRepository{

    private val trainigCache = cache.workoutCache

    private val videoWorkout = cache.videoWorkout

    override fun getWorkouts(fromCache: Boolean): Flow<ResultFlow<List<Workout>>>{
        return requestHelper.getCacheOrNetwork(trainigCache, fromCache) {
            workOut
        }
    }

    override fun getVideoWorkout(id: Int, fromCache: Boolean): Flow<ResultFlow<VideoWorkout>> {
        return requestHelper.getCacheOrNetwork (videoWorkout, fromCache) {
            videoWorkoutData
        }
    }

    private val videoWorkoutData = VideoWorkout(
        id = 1,
        duration = 30,
        link = "https://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8"
    )

    private val workOut = listOf(
        Workout(
            id = 1,
            title = "Утренняя пробежка",
            description = "Идеальная пробежка для старта дня",
            type = TypeTraining.TRAINING,
            duration = 30
        ),
        Workout(
            id = 2,
            title = "Жиросжигающая тренировка",
            description = "",
            type = TypeTraining.TRAINING,
            duration = 45
        ),
        Workout(
            id = 3,
            title = "Прямой эфир с тренером",
            description = "Живой эфир с разбором техники",
            type = TypeTraining.BROADCAST,
            duration = 60
        ),
        Workout(
            id = 4,
            title = "Силовой комплекс",
            description = "Упражнения с собственным весом",
            type = TypeTraining.COMPLEX,
            duration = 20
        ),
        Workout(
            id = 5,
            title = "Йога для начинающих",
            description = "Поза лотоса и базовые асаны",
            type = TypeTraining.TRAINING,
            duration = 40
        )
    )
}