package ru.ivan.eremin.treningtest.presenter.ui.trainings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training

class TrainingsComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                TrainingsScreen()
            }
        }
    }
}

@Composable
private fun TrainingsScreen(
    viewModel: TrainingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    TrainingsScreenState(
        state = state.value
    )
}

@Composable
private fun TrainingsScreenState(
    state: TrainingsUiState
) {
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.data.orEmpty()) {
            if (it is Training.Success) {
                TrainingItem(
                    item = it.data,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TrainingItemSkeleton(
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@Composable
private fun TrainingItem(
    item: Workout,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .background(
                colorResource(R.color.white),
                RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun TrainingItemSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                colorResource(R.color.skeletonColor),
                RoundedCornerShape(8.dp)
            )
            .height(200.dp)
    )
}

@Composable
@Preview
private fun TrainingsScreen_Preview() {
    MaterialTheme {
        TrainingsScreenState(
            state = TrainingsUiState(
                data = listOf(
                    Training.Success(
                        Workout(
                            id = 1,
                            title = "Утренняя пробежка",
                            description = "Идеальная пробежка для старта дня",
                            type = TypeTraining.TRAINING,
                            duration = 30
                        )
                    ),
                    Training.Success(
                        Workout(
                            id = 2,
                            title = "Жиросжигающая тренировка",
                            description = "",
                            type = TypeTraining.TRAINING,
                            duration = 45
                        )
                    ),
                    Training.Success(
                        Workout(
                            id = 3,
                            title = "Прямой эфир с тренером",
                            description = "Живой эфир с разбором техники",
                            type = TypeTraining.BROADCAST,
                            duration = 60
                        )
                    ),
                    Training.Success(
                        Workout(
                            id = 4,
                            title = "Силовой комплекс",
                            description = "Упражнения с собственным весом",
                            type = TypeTraining.COMPLEX,
                            duration = 20
                        )
                    ),
                    Training.Success(
                        Workout(
                            id = 5,
                            title = "Йога для начинающих",
                            description = "Поза лотоса и базовые асаны",
                            type = TypeTraining.TRAINING,
                            duration = 40
                        )
                    )
                )
            )
        )
    }
}

@Composable
@Preview
private fun TrainingsScreenSkeleton_Preview() {
    MaterialTheme {
        TrainingsScreenState(
            state = TrainingsUiState(
                data = listOf(
                    Training.Skeleton,
                    Training.Skeleton,
                    Training.Skeleton,
                )
            )
        )
    }
}