package ru.ivan.eremin.treningtest.data.service.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.ivan.eremin.treningtest.data.service.entity.VideoWorkoutDto
import ru.ivan.eremin.treningtest.data.service.entity.WorkoutDto

interface TrainingService {

    @GET("get_workouts")
    suspend fun getWorkouts(): List<WorkoutDto>

    @GET("get_video")
    suspend fun getVideo(@Query("id") id: Int): VideoWorkoutDto
}