package nolambda.playground.coroutine

import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runBlockingTest
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class BlockingVsBlockingTest : StringSpec({
    "runBlocking delay" {
        val time = measureTimeMillis {
            runBlocking {
                delay(1000)
            }
        }
        println("took: $time ms")
    }

    "runBlockingTest delay" {
        val time = measureTimeMillis {
            runBlockingTest {
                delay(1000)
            }
        }
        println("took: $time ms")
    }

    "runBlockingTest Stuck" {
        runBlockingTest {
            suspendCancellableCoroutine<Unit> { cont ->
                thread {
                    Thread.sleep(1000)
                    cont.resumeWith(Result.success(Unit))
                }
            }
        }
    }
})