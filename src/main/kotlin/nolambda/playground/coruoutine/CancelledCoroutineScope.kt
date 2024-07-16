package nolambda.playground.coruoutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)

    scope.launch { println("Launch #1") }
    scope.launch {
        println("Preparing -> Launch #2")
        delay(50)
        println("Launch #2")
    }
    scope.coroutineContext.cancelChildren()
    val job = scope.launch { println("Launch #3") }

    println("Job is active: ${job.isActive}")

    // Sleep for 2 seconds to wait for the job to finish
    Thread.sleep(2_000)
}
