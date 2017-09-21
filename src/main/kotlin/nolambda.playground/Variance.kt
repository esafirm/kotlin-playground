package nolambda.playground

fun variance() {
    var test = listOf<Any>()
    val testString = listOf<String>()
    test = testString
}

fun invariance() {
    var test = mutableListOf<Any>()
    val testString = mutableListOf<String>()
//    can't do this because sendEvent can't consume String
//    sendEvent = testString
}

interface Service<T> {
    fun getData(): T
    fun saveData(data: T)
}

fun main(args: Array<String>) {
    invariance()
    variance()
}