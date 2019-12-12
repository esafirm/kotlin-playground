package nolambda.playground.testing

import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import io.mockk.verify

internal class ClassToTestTest : StringSpec({

    "It should cover all the way through" {
        val sut = ClassToTest()
        val mockRunnable = mockk<Runnable>(relaxed = true)
        sut.testNullable(mockRunnable)
        sut.testNullable(null)

        verify(exactly = 1) {
            mockRunnable.run()
        }
    }
})