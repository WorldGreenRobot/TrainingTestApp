package ru.ivan.eremin.treningtest.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.domain.entity.Workout

interface TrainingRepository {

    fun getWorkouts(fromCache: Boolean): Flow<ResultFlow<List<Workout>>>

    fun getVideoWorkout(id: Int, fromCache: Boolean): Flow<ResultFlow<VideoWorkout>>
}