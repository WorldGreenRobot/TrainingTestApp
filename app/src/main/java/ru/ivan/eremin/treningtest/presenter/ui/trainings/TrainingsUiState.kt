package ru.ivan.eremin.treningtest.presenter.ui.trainings

import ru.ivan.eremin.treningtest.domain.entity.Filter
import ru.ivan.eremin.treningtest.presenter.ui.base.UiState
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training

data class TrainingsUiState(
    val showSkeleton: Boolean = true,
    val showRefresh: Boolean = false,
    val data: List<Training>? = null,
    val filters: List<Filter>? = null,
    val error: String? = null
) : UiState