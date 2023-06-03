package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
fun main() = runBlocking {
    val channel = MutableSharedFlow<String>(100)

    launch {
        println("Collecting…")

        val list = mutableListOf<String>()
        channel.onEach {
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

        channel.emit("A")
        channel.emit("B")
        channel.emit("C")
        channel.emit("D")
        channel.emit("E")

        delay(1000)
        channel.emit("A")
        channel.emit("B")
        channel.emit("C")
        channel.emit("D")
        channel.emit("E")
        channel.emit("F")
    }

    Unit
}

