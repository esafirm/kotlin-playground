package nolambda.playground.coroutine

import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.test.TestCoroutineDispatcher
import nolambda.playground.coruoutine.SingleExecutorWithDelay

class SingleExecutorWithDelaySpec : StringSpec({

    val testDispatcher = TestCoroutineDispatcher()
    val testSender = mockk<(List<String>) -> Unit>(relaxed = true)
    val executorWithDelay = SingleExecutorWithDelay(testDispatcher, testSender)

    "It should emit value" {
        executorWithDelay.log("A")
        testDispatcher.advanceTimeBy(10_000)

        verify(exactly = 1) { testSender.invoke(any()) }
    }

    "it should emit value when different type of log came in" {
        executorWithDelay.log("A")
        executorWithDelay.log("A")
        executorWithDelay.log("B")

        testDispatcher.advanceUntilIdle()

        verifySequence {
            testSender.invoke(listOf("A", "A"))
            testSender.invoke(listOf("B"))
        }
    }

    "it should emit value when reset is called" {
        executorWithDelay.log("A")
        executorWithDelay.reset()
        executorWithDelay.log("B")

        testDispatcher.advanceUntilIdle()

        verifySequence {
            testSender.invoke(listOf("A"))
            testSender.invoke(listOf("B"))
        }
    }

})