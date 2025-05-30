package ru.ivan.eremin.treningtest.data.service.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.ErrorResult
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.Loading
import ru.ivan.eremin.treningtest.domain.entity.ResultFlow.Success
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class RequestHelper @Inject constructor(
    private val scope: CoroutineScope
) {
    private val mutexes = ConcurrentHashMap<String, Mutex>()

    fun <T> getCacheOrNetwork(
        cache: MutableStateFlow<T?>,
        fromCache: Boolean,
        key: String = DEFAULT_KEY,
        network: suspend () -> T
    ): Flow<ResultFlow<T>> {
        return channelFlow {
            if (!fromCache) {
                cache.emit(null)
            }
            cache.collect {
                it?.let { send(Success(it)) }
                if (it == null) {
                    send(Loading)
                    scope.launch {
                        try {
                            mutexes.getOrPut(key) { Mutex() }.withLock {
                                cache.value ?: withContext(Dispatchers.Default) {
                                    val response = network.invoke()
                                    cache.emit(response)
                                }
                            }
                        } catch (e: Exception) {
                            if (!isClosedForSend) send(ErrorResult(e))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_KEY = "KEY"
    }
}