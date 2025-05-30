package ru.ivan.eremin.treningtest.presenter.ui.trainings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.ivan.eremin.treningtest.domain.entity.Filter
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.domain.usecase.GetFiltresUseCase
import ru.ivan.eremin.treningtest.domain.usecase.GetWorkoutsUseCase
import ru.ivan.eremin.treningtest.presenter.ui.base.BaseViewModel
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training
import javax.inject.Inject


@HiltViewModel
class TrainingsViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val getFiltersUseCase: GetFiltresUseCase
) : BaseViewModel<TrainingsUiState>() {
    override fun createInitialState() = TrainingsUiState()

    private var job: Job? = null

    private val workoutsFlow = MutableStateFlow<ResultFlow<List<Workout>>?>(null)
    private val searchQueryFlow = MutableStateFlow<String?>(null)
    private val filterQueryFlow = MutableStateFlow<TypeTraining?>(null)

    init {
        subscribeWorkoutFlow()
        loadData()
    }

    private fun subscribeWorkoutFlow() {
        viewModelScope.launch {
            combine(
                workoutsFlow,
                searchQueryFlow,
                filterQueryFlow
            ) { workouts, searchQuery, filterQuery ->
                when (workouts) {
                    is ResultFlow.Success -> {
                        val searchedWorkouts = workouts.data.filter { workout ->
                            if (searchQuery == null) {
                                true
                            } else {
                                workout.title.startsWith(searchQuery, true)
                            }
                        }

                        val filtedWorkouts =
                            searchedWorkouts.filter { workout ->
                                if (filterQuery == null) {
                                    true
                                } else {
                                    workout.type == filterQuery
                                }
                            }

                        ResultFlow.Success(filtedWorkouts)
                    }

                    else -> workouts
                } to filterQuery
            }.collectLatest { result ->
                result.first?.let {
                    when (it) {
                        is ResultFlow.Loading -> updateState {
                            copy(
                                data = data ?: List(3) {
                                    Training.Skeleton
                                }
                            )
                        }

                        is ResultFlow.Success -> {

                            val filters = state.value.filters ?: getFilters()
                            updateState {
                                copy(
                                    showRefresh = false,
                                    data = it.data.map {
                                        Training.Success(it)
                                    },
                                    filters = filters?.map {
                                        it.copy(
                                            isSelected = it.typeTraining == result.second
                                        )
                                    }
                                )
                            }
                        }

                        is ResultFlow.ErrorResult -> updateState {
                            copy(
                                showRefresh = false,
                                error = it.exception.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getFilters(): List<Filter>? {
        return try {
            getFiltersUseCase()
        } catch (_: Exception) {
            null
        }
    }

    private fun loadData(fromCache: Boolean = true) {
        job?.cancel()
        job = viewModelScope.launch {
            getWorkoutsUseCase(fromCache)
                .collectLatest {
                    workoutsFlow.value = it
                }

        }
    }

    fun search(query: String) {
        searchQueryFlow.value = query
    }

    fun filter(query: TypeTraining?) {
        filterQueryFlow.value = query
    }

    fun refresh() {
        updateState {
            copy(
                showRefresh = true,
                error = null
            )
        }

        loadData(false)
    }
}