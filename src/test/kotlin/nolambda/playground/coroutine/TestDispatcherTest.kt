package nolambda.playground.coroutine

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout


class Suspendable(
    private val action: () -> Unit,
    private val timeout: Long = 1,
) {
    suspend fun run(): String {

        return withTimeout(timeout) {
            action.invoke()
            println("Run something")
            "A"
        }
    }
}

class TestDispatcherTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    "Suspend function should follow it scope" {

        val mockAction = mockk<() -> Unit>()
        val suspendable = Suspendable(mockAction)

        coEvery { mockAction.invoke() } answers {
            Unit
        }

        runTest(testDispatcher) {
            val result = suspendable.run()
            result shouldBe "A"
        }
    }
})
