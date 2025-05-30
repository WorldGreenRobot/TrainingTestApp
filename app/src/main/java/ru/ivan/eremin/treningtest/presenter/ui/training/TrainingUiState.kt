package ru.ivan.eremin.treningtest.presenter.ui.training

import ru.ivan.eremin.treningtest.domain.entity.VideoWorkout
import ru.ivan.eremin.treningtest.presenter.ui.base.InitializationData
import ru.ivan.eremin.treningtest.presenter.ui.base.UiState

data class TrainingUiState(
    val showSkeleton: Boolean = true,
    val showRefresh: Boolean = false,
    val data: VideoWorkout? = null,
    val trainingId: Int = 0,
    val error: String? = null
) : UiState

data class TrainingDetailInitData(
    val trainingId: Int
) : InitializationData