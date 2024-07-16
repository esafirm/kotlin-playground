package nolambda.playground.coruoutine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.runBlocking

fun main() {
    val stateFlow = MutableStateFlow<String?>(null)

    runBlocking {
        println("Start of run blocking.")

        println("#1 Variant")
        val result1 = stateFlow
            .onSubscription { println("onSubscription") }
            .onStart { println("onStart") }
            .firstOrNull()

        println("#2 Variant")
        val result2 = stateFlow
            .onStart { println("onStart") }
            .firstOrNull()

        println(
            """
            Result 1: $result1
            Result 2: $result2
            """.trimIndent()
        )

        println("End of run blocking.")
    }
}
