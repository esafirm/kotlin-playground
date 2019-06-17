package nolambda.playground.coroutine

import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

@Suppress("EXPERIMENTAL_API_USAGE")
class SelectSpec : StringSpec({
    "Sample function with select" {
        runBlocking {
            val producer = produce {
                repeat(10) {
                    delay(50)
                    send(it)
                }
            }

            val fixedPeriodProducer = produce {
                while (true) {
                    delay(100)
                    send(Unit)
                }
            }

            var isDone = false
            var lastValue: Int? = null

            while (!isDone) {
                select<Unit> {
                    producer.onReceiveOrNull {
                        if (it == null) {
                            println("End")
                            isDone = true
                        } else {
                            lastValue = it
                        }
                    }

                    fixedPeriodProducer.onReceive {
                        lastValue?.let { println(it) }
                    }
                }
            }

            fixedPeriodProducer.cancel()
        }
    }
})