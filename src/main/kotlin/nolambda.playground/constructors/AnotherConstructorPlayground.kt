package nolambda.playground.constructors

class Test(
        private val a: String,
        private val b: String
) : BaseTest() {
    override fun getData(): String = "Test".also {
        print(a)
        print(b)
    }
}

abstract class BaseTest {

    private val a = "String".let { getData() }

    abstract fun getData(): String
}

fun main(args: Array<String>) {
    Test("1", "2")
}