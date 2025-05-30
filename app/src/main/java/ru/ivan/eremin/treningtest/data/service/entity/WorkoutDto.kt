package ru.ivan.eremin.treningtest.data.service.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class WorkoutDto(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("type")
    val type: Int? = null,
   /* @SerialName("duration")
    val duration: Int? = null*/
)