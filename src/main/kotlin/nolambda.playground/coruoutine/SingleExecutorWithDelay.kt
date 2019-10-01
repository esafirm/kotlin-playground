@file:Suppress("EXPERIMENTAL_API_USAGE")

package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import nolambda.playground.concurrent.delay
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

fun main() {
    println("Start working!")
    runBlocking {
        val errorHandler = CoroutineExceptionHandler { _, e ->
            println("Got error $e")
        }
        val context = Executors.newSingleThreadExecutor().asCoroutineDispatcher() + errorHandler
        val sender: (List<String>) -> Unit = { }
        val executor = SingleExecutorWithDelay(context, sender)
        executor.log("A")
        executor.log("A")
        executor.log("A")
        executor.log("A")

        executor.log("B")
        executor.log("B")
        executor.log("B")
        executor.log("C")

        delay(1000)
        executor.reset()

        delay(1000)
        executor.log("Last")

        delay(15_000)
    }
    println("Finish working!")
}

class SingleExecutorWithDelay(
    override val coroutineContext: CoroutineContext,
    private val sender: (List<String>) -> Unit
) : CoroutineScope {

    private val subject = Channel<Unit>()
    private val queueOfLog = mutableListOf<String>()

    private var lastLog: String? = null

    init {
        launch {
            subject.asFlow()
                .debounce(2_000)
                .collect {
                    println("Collecting!")
                    sendAll()
                }
        }
    }

    fun reset() = launch {
        sendAll()
        queueOfLog.clear()
    }

    fun log(log: String) = launch {
        println("Log: $log")

        val isFirstState = lastLog == null
        if (lastLog != log && !isFirstState) {
            sendAll()
        }

        lastLog = log
        queueOfLog.add(log)
        subject.send(Unit)
    }

    fun sendAll() {
        if (queueOfLog.isNotEmpty()) {
            send(queueOfLog.toList())
            queueOfLog.clear()
        }
    }

    private fun send(logs: List<String>) {
        println("Send: $logs")
        sender.invoke(logs)
    }
}