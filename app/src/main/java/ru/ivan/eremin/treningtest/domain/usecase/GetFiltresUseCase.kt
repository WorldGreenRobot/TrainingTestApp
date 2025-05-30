package ru.ivan.eremin.treningtest.domain.usecase

import ru.ivan.eremin.treningtest.domain.entity.Filter
import ru.ivan.eremin.treningtest.domain.repository.TrainingRepository
import ru.ivan.eremin.treningtest.utils.getFirstValue
import javax.inject.Inject

class GetFiltresUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(): List<Filter> {
        return repository.getWorkouts(true).getFirstValue()
            .map { result ->
                Filter(
                    result.type
                )
            }.distinct()

    }
}