@file:Suppress("EXPERIMENTAL_API_USAGE")

package nolambda.playground.coruoutine

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("EXPERIMENTAL_API_USAGE")
class CoroutineSubject<T>(
    private val debounce: Long = 0L,
    capacity: Int = 1
) {
    private val channel = Channel<T>(capacity)

    suspend fun onNext(data: T) {
        println("onNext")
        channel.send(data)
    }

    suspend fun observe(onCollect: suspend (T) -> Unit) {
        flow {
            channel.consumeEach {
                emit(it)
            }
        }.apply {
            if (debounce > 0) {
                debounce(debounce)
            }
        }.collect(onCollect)
    }
}

fun main() = runBlocking {
    val subject = CoroutineSubject<String>()

    launch {
        println("Subject send")

        subject.onNext("A")
        subject.onNext("B")
        subject.onNext("C")
    }

    launch {
        println("Subject observe")
        
        subject.observe {
            println("Observe: $it")
        }
    }

    Unit
}
