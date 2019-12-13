package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.util.concurrent.Executors

fun main() {
    runBlocking {
        val sender = Sender()
        var counter: Int = 0

        sender.init()
        sender.send(counter++)
        sender.send(counter++)
        sender.send(counter++)
        sender.send(counter++)
        sender.send(counter++)
        sender.send(counter)
        delay(1000)
    }
    println("Heyo")
}

class Sender {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun init() = GlobalScope.launch(dispatcher) {
        safeSleep(200)
        println("Init")
        throw IllegalStateException()
    }

    fun send(counter: Int) = GlobalScope.launch(dispatcher) {
        safeSleep((Math.random() * 500).toLong())
        println("Send $counter")
    }

    private fun safeSleep(milis: Long) {
        try {
            Thread.sleep(milis)
        } catch (e: Exception) {
        }
    }
}