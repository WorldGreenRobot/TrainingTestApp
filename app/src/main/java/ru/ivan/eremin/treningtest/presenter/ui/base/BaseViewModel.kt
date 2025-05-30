package ru.ivan.eremin.treningtest.presenter.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : UiState> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    private val uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state = uiState.asStateFlow()

    protected fun updateState(newState: State.() -> State) {
        uiState.update {
            newState.invoke(it)
        }
    }
}

abstract class BaseViewModelData<State : UiState,
        Data : InitializationData> : BaseViewModel<State>() {

    private val initDataState: MutableSharedFlow<Data> = MutableSharedFlow()

    fun setInitData(data: Data) {
        viewModelScope.launch { initDataState.emit(data) }
    }

    protected val initData = initDataState.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            initDataState.distinctUntilChanged()
                .collectLatest { onFirstViewAttach(it) }
        }
    }

    protected abstract fun onFirstViewAttach(data: Data)
}