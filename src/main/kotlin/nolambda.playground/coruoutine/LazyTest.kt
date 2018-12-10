package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

private val lazyCoroutines = listOf(
        lazyLaunch { delayAndPrint("#1", 400) },
        lazyLaunch { delayAndPrint("#2", 100) },
        lazyLaunch { delayAndPrint("#3", 100) },
        lazyLaunch { delayAndPrint("#4", 100) },
        lazyLaunch { delayAndPrint("#5", 100) },
        lazyLaunch { delayAndPrint("#6", 400) }
)

private fun lazyLaunch(block: suspend CoroutineScope.() -> Unit) =
        GlobalScope.launch(start = CoroutineStart.DEFAULT, block = block)

fun main() = measureTimeMillis {
    runBlocking {
//        lazyCoroutines.forEach { it.start() }
        lazyCoroutines.joinAll()
    }
}.let {
    println("This took $it ms")
}

