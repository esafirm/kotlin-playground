package nolambda.playground.coruoutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

fun main() {
    println("Unconfined #1: ${getTime(1000)} ms")
    println("Unconfined #1: ${getTime(2000)} ms")
}

private fun getTime(delay: Int) = measureTimeMillis {
    delayUnconfined(delay)
}

private fun delayUnconfined(delay: Int) {
    GlobalScope.launch(Dispatchers.Unconfined) {
        delay(1000)
    }
}

