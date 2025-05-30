package ru.ivan.eremin.treningtest.presenter.ui.training

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.usecase.GetVideoWorkoutUseCase
import ru.ivan.eremin.treningtest.presenter.ui.base.BaseViewModelData
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getVideoWorkoutUseCase: GetVideoWorkoutUseCase
) : BaseViewModelData<TrainingUiState, TrainingDetailInitData>() {

    private var job: Job? = null
    private var trainingId: Int = 0
    override fun createInitialState() = TrainingUiState()

    override fun onFirstViewAttach(data: TrainingDetailInitData) {
        trainingId = data.trainingId
        loadData(trainingId)
    }

    private fun loadData(trainingId: Int, fromCache: Boolean = true) {
        job?.cancel()
        job = viewModelScope.launch {
            getVideoWorkoutUseCase(trainingId, fromCache)
                .collectLatest {
                    when (it) {
                        is ResultFlow.Loading -> updateState {
                            copy(
                                showRefresh = data != null,
                                showSkeleton = data == null
                            )
                        }

                        is ResultFlow.Success -> updateState {
                            copy(
                                showRefresh = false,
                                showSkeleton = false,
                                data = it.data
                            )
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

    fun refresh() {
        updateState {
            copy(
                showRefresh = true,
                error = null
            )
        }
        loadData(trainingId, false)
    }
}