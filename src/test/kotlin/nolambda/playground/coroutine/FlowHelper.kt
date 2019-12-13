package nolambda.playground.coroutine

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

@InternalCoroutinesApi
@FlowPreview
fun <T> Flow<T>.blockingCollect(action: suspend (T) -> Unit) {
    runBlocking {
        collect(object : FlowCollector<T> {
            override suspend fun emit(value: T) = action(value)
        })
    }
}

@FlowPreview
fun <T> Flow<T>.blockingToList(): List<T> = runBlocking {
    toList()
}

