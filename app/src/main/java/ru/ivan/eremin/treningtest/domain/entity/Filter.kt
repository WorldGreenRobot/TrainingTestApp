package ru.ivan.eremin.treningtest.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    val typeTraining: TypeTraining,
    val isSelected: Boolean = false
): java.io.Serializable