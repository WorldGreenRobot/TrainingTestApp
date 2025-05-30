package ru.ivan.eremin.treningtest.presenter.exteption

import android.content.Context
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining

fun TypeTraining.getString(context: Context): String {
    return when (this) {
        TypeTraining.TRAINING -> context.getString(R.string.item_training_type)
        TypeTraining.BROADCAST -> context.getString(R.string.item_broadcast_type)
        TypeTraining.COMPLEX -> context.getString(R.string.item_complex_type)
        else -> context.getString(R.string.item_unknown_type)
    }
}