package ru.ivan.eremin.treningtest.data.cache

import kotlinx.coroutines.flow.MutableStateFlow
import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.domain.entity.Workout

class AppCache: BaseCache {
    val workoutCache = MutableStateFlow<List<Workout>?>(null)
    val videoWorkout = MutableStateFlow<VideoWorkout?>(null)
}