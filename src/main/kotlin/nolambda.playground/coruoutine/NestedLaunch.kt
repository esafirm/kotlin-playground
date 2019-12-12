package nolambda.playground.coruoutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    GlobalScope.launch {
        launch {
            delay(1000)
            println("First")
        }
        println("Second")
    }

    Thread.sleep(5_000)

    a(::BasicConstructor)
}

fun a(block: (String) -> BasicConstructor) {}
class BasicConstructor(a: String)