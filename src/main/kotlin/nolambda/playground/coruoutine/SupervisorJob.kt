package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Supervisor : CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job

    fun doA() {
        job.cancel()
        launch {
            println("A")
        }
    }
}

fun main() {
    val s = Supervisor()
    s.doA()

    runBlocking {
        delay(1000)
    }
}