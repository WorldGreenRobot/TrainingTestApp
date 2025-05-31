package ru.ivan.eremin.treningtest.presenter.exteption

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
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

@Composable
@ReadOnlyComposable
fun TypeTraining.getString(): String {
    return when (this) {
        TypeTraining.TRAINING -> stringResource(R.string.item_training_type)
        TypeTraining.BROADCAST -> stringResource(R.string.item_broadcast_type)
        TypeTraining.COMPLEX -> stringResource(R.string.item_complex_type)
        else -> stringResource(R.string.item_unknown_type)
    }
}
