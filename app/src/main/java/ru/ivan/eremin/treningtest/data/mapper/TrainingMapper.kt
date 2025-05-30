package ru.ivan.eremin.treningtest.data.mapper

import ru.ivan.eremin.treningtest.data.service.entity.VideoWorkoutDto
import ru.ivan.eremin.treningtest.data.service.entity.WorkoutDto
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.domain.entity.Workout

object TrainingMapper {

    fun List<WorkoutDto>?.mapToDomain() =
        this.orEmpty().map {
            Workout(
                id = it.id ?: 0,
                title = it.title.orEmpty(),
                description = it.description.orEmpty(),
                type = TypeTraining.getTraining(it.type),
                duration = 30
            )
        }.filter { it.id > 0 }


    fun VideoWorkoutDto.mapToDomain(): VideoWorkout {
        return VideoWorkout(
            id = this.id ?: 0,
            duration = 30,
            link = "https://ref.test.kolsa.ru${this.link}"
        )
    }
}