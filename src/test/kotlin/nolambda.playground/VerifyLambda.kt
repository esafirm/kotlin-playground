package nolambda.playground

import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito

typealias Callback = (Boolean) -> Unit

class VerifyLambda : StringSpec({

    "Verify Lamdba" {
        val producer = mock<BooleanProducer>()
        val tested = SomeClass(producer)

        `when`(producer.getBoolean()).thenReturn(true)

        val success = mock<Callback>()
        val error = mock<Callback>()

        tested.checkService(success, error)

        Mockito.verify(success)(true)
    }

})

class BooleanProducer {
    fun getBoolean() = false
}

class SomeClass(val producer: BooleanProducer) {

    fun checkService(success: Callback, error: Callback) {
        if (producer.getBoolean()) {
            success(true)
        } else {
            error(false)
        }
    }

}
