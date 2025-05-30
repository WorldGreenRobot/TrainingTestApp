package ru.ivan.eremin.treningtest.domain.usecase

import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository
import javax.inject.Inject

class GetVideoWorkoutUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    operator fun invoke(id: Int, fromCache: Boolean) = repository.getVideoWorkout(id, fromCache)
}