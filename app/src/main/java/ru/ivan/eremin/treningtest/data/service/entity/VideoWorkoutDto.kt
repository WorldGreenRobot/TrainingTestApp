package ru.ivan.eremin.treningtest.data.service.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoWorkoutDto (
    @SerialName("id")
    val id: Int?=null,
  /*  @SerialName("duration")
    val duration: String? = null,*/
    @SerialName("link")
    val link: String? = null
)