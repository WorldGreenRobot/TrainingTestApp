package ru.ivan.eremin.treningtest.presenter.ui.entity

import ru.ivan.eremin.treningtest.domain.entity.Workout

sealed interface Training {
    data class Success(val data: Workout) : Training
    data object Skeleton : Training
}