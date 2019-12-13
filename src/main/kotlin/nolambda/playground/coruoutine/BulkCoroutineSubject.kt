package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
fun main() = runBlocking {
    val channel = BroadcastChannel<String>(100)

    launch {
        println("Collecting…")

        val list = mutableListOf<String>()
        channel.asFlow()
            .onEach {
                println("each: $it")
            }
            .transform<String, List<String>> {
                emit(list.apply { add(it) }.toList())
            }
            .debounce(100)
            .onEach { list.clear() }
            .collect {
                println("collected: $it")
            }
    }


    GlobalScope.launch {
        println("Send…")

        channel.offer("A")
        channel.offer("B")
        channel.offer("C")
        channel.offer("D")
        channel.offer("E")

        delay(1000)
        channel.offer("A")
        channel.offer("B")
        channel.offer("C")
        channel.offer("D")
        channel.offer("E")
        channel.offer("F")
    }

    Unit
}

