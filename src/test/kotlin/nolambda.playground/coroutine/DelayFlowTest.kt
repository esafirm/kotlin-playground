package nolambda.playground.coroutine

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
                delay(10)
                emit(it)
            }
        }.sample(20)

        var flowSize: Int? = null
        GlobalScope.launch {
            flowSize = flow.toList().size
        }

        runBlocking {
            delay(1000)
            // Because delay is not exact
            flowSize shouldBe 4
        }
    }
})

@Suppress("EXPERIMENTAL_API_USAGE")
private suspend fun FlowCollector<Int>.emitFlow() {
    (0..10).forEach {
        this.emit(it)
        delay(50)
    }
}