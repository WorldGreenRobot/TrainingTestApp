package ru.ivan.eremin.treningtest.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: Int,
    val title: String,
    val description: String,
    val type: TypeTraining,
    val duration: Int
): java.io.Serializable
