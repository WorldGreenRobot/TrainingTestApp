@file:OptIn(ExperimentalMaterial3Api::class)

package ru.ivan.eremin.treningtest.presenter.ui.trainings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.presenter.exteption.getString
import ru.ivan.eremin.treningtest.presenter.ui.base.BaseFragment
import ru.ivan.eremin.treningtest.presenter.ui.base.Screen
import ru.ivan.eremin.treningtest.presenter.ui.component.SearchableTopAppBar
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training

class TrainingsComposeFragment : BaseFragment() {
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
        state = state.value,
        onAction = remember {
            {
                handlerAction(it, viewModel)
            }
        }
    )
}

@Composable
private fun TrainingsScreenState(
    state: TrainingsUiState,
    onAction: (TrainingAction) -> Unit = {}
) {

    val isShowSearch = remember { mutableStateOf(false) }

    Screen(
        topBar = {
            SearchableTopAppBar(
                title = {
                    Text(text = stringResource(R.string.training))
                },
                currentSearchQuery = "",
                onSearchQueryChange = {},
                onSearchExecute = {},
                isSearchActive = isShowSearch.value,
                onSearchActiveChange = {
                    isShowSearch.value = it
                },
                onCloseSearch = {
                    isShowSearch.value = false
                },
                actions = {
                    IconButton(onClick = { onAction(TrainingAction.OnRefresh) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.filter_list_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        isRefreshing = state.showRefresh,
        onRefresh = {
            onAction(TrainingAction.OnRefresh)
        },
        uiError = state.error,
    ) {
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            if (item.description.isNotBlank())
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.type.getString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp
                )

                Text(
                    text = pluralStringResource(
                        R.plurals.minutes,
                        item.duration,
                        item.duration
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun TrainingItemSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .background(
                colorResource(R.color.white),
                RoundedCornerShape(8.dp)
            )
    ) {
        Box(
            modifier = modifier
                .background(colorResource(R.color.skeletonColor))
                .height(200.dp)
        )
    }
}

sealed interface TrainingAction {
    data object OnRefresh : TrainingAction
}

private fun handlerAction(action: TrainingAction, viewModel: TrainingsViewModel) {
    when (action) {
        TrainingAction.OnRefresh -> viewModel.refresh()
    }
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
                showRefresh = false,
                data = listOf(
                    Training.Skeleton,
                    Training.Skeleton,
                    Training.Skeleton,
                )
            )
        )
    }
}