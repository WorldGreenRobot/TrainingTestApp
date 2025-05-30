package ru.ivan.eremin.treningtest.domain.entity

enum class TypeTraining {
    TRAINING, BROADCAST, COMPLEX, UNKNOWN;

    companion object {
        fun getTraining(id: Int?) =
            entries.firstOrNull { it.ordinal == id } ?: UNKNOWN
    }
}