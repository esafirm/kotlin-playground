package nolambda.playground.coroutine

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import nolambda.playground.concurrent.delay

@Suppress("EXPERIMENTAL_API_USAGE")
class DelayFlowSpec : StringSpec({
    "Flow with debounce" {
        val flow = flow<Int> {
            emitFlow()
        }.debounce(100)

        runBlocking {
            flow.toList() shouldBe listOf(10)
        }
    }

    "Flow with delay" {
        val flow = flow {
            repeat(10) {
                emit(it)
                delay(100)
            }
        }.sample(1)

        runBlocking {
            delay(100)
            println(flow.toList())
        }
    }
})

@Suppress("EXPERIMENTAL_API_USAGE")
private suspend fun FlowCollector<Int>.emitFlow() {
    (0..10).forEach {
        emit(it)
        delay(50)
    }
}