package ru.ivan.eremin.treningtest.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    operator fun invoke(fromCache: Boolean): Flow<ResultFlow<List<Workout>>> = repository.getWorkouts(fromCache)
}