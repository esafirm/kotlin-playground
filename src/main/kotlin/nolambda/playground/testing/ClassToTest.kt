package nolambda.playground.testing

class ClassToTest {
    fun testNullable(runnable: Runnable?) {
        runnable?.run() ?: println("No Runnable found")
    }
}

fun main() {
    ClassToTest().testNullable(runnable = Runnable {
        println("A")
    })
}