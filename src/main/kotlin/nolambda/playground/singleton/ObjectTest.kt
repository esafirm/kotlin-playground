package nolambda.playground.singleton

open class ObjectTest(val message: String) {
    fun test() {
        println(message)
    }
}

object ObjectOf : ObjectTest("test")

fun main() {
    ObjectOf.test()
}