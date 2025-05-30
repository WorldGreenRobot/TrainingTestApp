package ru.ivan.eremin.treningtest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.ErrorResult
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.Loading
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.Success
import java.io.IOException

suspend fun <T> Flow<ResultFlow<T>>.getFirstValue(ignoreException: Boolean = false) =
    with(
        filter { it !is Loading }
            .filter { it !is ErrorResult || !ignoreException }
            .first()
    ) {
        when (this) {
            is Success -> data
            is ErrorResult -> throw exception
            is Loading -> throw IOException()
        }
    }