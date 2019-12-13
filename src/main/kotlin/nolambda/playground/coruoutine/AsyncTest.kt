package nolambda.playground.coruoutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

val listOfThing by lazy {
    listOf(
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) },
            GlobalScope.async { delay(100) }
    )
}

fun main() = println("Took ${measureTimeMillis {
    runBlocking {
        repeat(100) {
            GlobalScope.async { delay(100) }.await()
        }
        Unit
    }
}} ms")