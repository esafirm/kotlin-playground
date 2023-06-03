package nolambda.playground.coroutine

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

private class CoroutineDemo(
    private val firstJobDurationInMs: Long,
    private val secondJobDurationInMs: Long,
) {

    fun runParallel(): Long {
        return runBlocking(Executors.newFixedThreadPool(2).asCoroutineDispatcher()) {
            runTask()
        }
    }

    fun runConcurrently(): Long {
        return runBlocking {
            runTask()
        }
    }

    private suspend fun runTask(): Long {
        return coroutineScope {
            val result = measureTimeMillis {
                val firstJob = async { Thread.sleep(firstJobDurationInMs) }
                val secondJob = async { Thread.sleep(secondJobDurationInMs) }

                listOf(firstJob, secondJob).awaitAll()
            }

            println("It took $result ms to run")
            result
        }
    }
}

class ConcurrencyVsParallelismTest : StringSpec({

    "Simple concurrent coroutine will accumulate the time" {
        val demo = CoroutineDemo(1000, 2000)
        val timeTaken = demo.runConcurrently()

        (timeTaken > 3000L) shouldBe true
    }

    "Simple parallel coroutine will took the longest task duration" {
        val demo = CoroutineDemo(1000, 2000)
        val timeTaken = demo.runParallel()

        (timeTaken > 2000L) shouldBe true
        (timeTaken < 3000L) shouldBe true
    }

})
