package ru.ivan.eremin.treningtest.domain.entity

sealed class ResultFlow<out T>  {
    data class Success<T>(val data: T) : ResultFlow<T>()
    data class ErrorResult(val exception: Throwable) : ResultFlow<Nothing>()
    object Loading : ResultFlow<Nothing>()
}