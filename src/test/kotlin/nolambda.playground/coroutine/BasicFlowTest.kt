package nolambda.playground.coroutine

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

@Suppress("EXPERIMENTAL_API_USAGE")
class FlowSpec : StringSpec({

    "Simple Flow" {
        val flow = flow {
            emit(1)
        }

        val secondFlow = flowOf(1, 2, 3)

        runBlocking {
            flow.collect {
                it shouldBe 1
            }

            val result = mutableListOf<Int>()
            secondFlow.collect {
                result.add(it)
            }

            result.size shouldBe 3
        }
    }

    "Test producer" {
        runBlocking {
            val producer = produce {
                (0..10).forEach {
                    send(it)
                }
            }
            producer.consume { println("Consumed") }
            producer.isEmpty shouldBe false
        }
    }
})