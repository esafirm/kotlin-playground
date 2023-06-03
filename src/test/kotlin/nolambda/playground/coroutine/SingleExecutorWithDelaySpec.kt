package nolambda.playground.coroutine

import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import nolambda.playground.coruoutine.SingleExecutorWithDelay

class SingleExecutorWithDelaySpec : StringSpec({

    val testDispatcher = Dispatchers.Unconfined
    val testSender = mockk<(List<String>) -> Unit>(relaxed = true)
    val executorWithDelay = SingleExecutorWithDelay(testDispatcher, testSender)

    "It should emit value" {
        executorWithDelay.log("A")

        verify(exactly = 1) { testSender.invoke(any()) }
    }

    "it should emit value when different type of log came in" {
        executorWithDelay.log("A")
        executorWithDelay.log("A")
        executorWithDelay.log("B")

        verifySequence {
            testSender.invoke(listOf("A", "A"))
            testSender.invoke(listOf("B"))
        }
    }

    "it should emit value when reset is called" {
        executorWithDelay.log("A")
        executorWithDelay.reset()
        executorWithDelay.log("B")

        verifySequence {
            testSender.invoke(listOf("A"))
            testSender.invoke(listOf("B"))
        }
    }

})
