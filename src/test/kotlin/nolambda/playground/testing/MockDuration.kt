package nolambda.playground.testing

import io.kotlintest.mock.mock
import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import kotlin.system.measureTimeMillis

class MockDuration : StringSpec({
    "mockito duration" {
        measureTimeMillis {
            mock<Runnable>()
        }.also {
            println("Mockito took $it ms")
        }
    }

    "mockk duration" {
        measureTimeMillis {
            mockk<Runnable>()
        }.also {
            println("Mockk took $it ms")
        }
    }
})