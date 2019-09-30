package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

fun main() {
    println("Start working!")
    runBlocking {
        val errorHandler = CoroutineExceptionHandler { _, e ->
            println("Got error $e")
        }
        val executor = SingleExecutorWithDelay(Executors.newSingleThreadExecutor().asCoroutineDispatcher() + errorHandler)
        executor.log("A")
        executor.reset()
        executor.log("B")
        executor.reset()
        delay(5_000)
    }
    println("Finish working!")
}

class SingleExecutorWithDelay(
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    private val queueOfLogs = mutableListOf<String>()

    private var sendJob: Job? = null

    fun log(log: String) = launch {
        println("Log")
        queueOfLogs.add(log)
        delaySend()
    }

    fun reset() = launch {
        println("Reset")
        sendJob?.join()

        println("Clear")
        queueOfLogs.clear()
    }

    private fun delaySend() {
        sendJob?.cancel()
        sendJob = launch {
            delay(2_000)
            send()
        }
    }

    private fun send() {
        println("Send")
        if (queueOfLogs.isEmpty()) {
            throw IllegalStateException("Logs cannot be empty")
        }
        println("Send log with size: ${queueOfLogs.size}")
    }
}