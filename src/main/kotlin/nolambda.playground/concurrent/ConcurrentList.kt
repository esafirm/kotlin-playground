package nolambda.playground.concurrent

import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingDeque
import kotlin.concurrent.thread

val list = mutableListOf<Int>()

fun run(runnable: () -> Unit) {
    thread(start = true, block = runnable)
}

@Synchronized
fun addList() {
    list.add(list.size + 1)
    println("add")
}

@Synchronized
fun removeList() {
    list.remove(0)
    println("remove")
}

@Synchronized
fun iterate() {
    for (i in 1..100) {
        list.forEach {
            delay(10)
        }
        println("iterate")
    }
}

fun main(args: Array<String>) {
    println("Run the code!")

    Timer.start(3)

    run {
        println("on thread ${Thread.currentThread().name}")
        for (i in 1..100) {
            addList()
            delay(10)
        }
        Timer.stopAndPrint()
    }

    run {
        print("on thread ${Thread.currentThread().name}")
        for (i in 1..100) {
            removeList()
            delay(10)
        }
        Timer.stopAndPrint()
    }

    run {
        println("on thread ${Thread.currentThread().name}")
        iterate()
        Timer.stopAndPrint()
    }
}


fun delay(delay: Long) = try {
    Thread.sleep(delay)
} catch (e: Exception) {
    print("Have error at ${e.printStackTrace()}")
}

object Timer {

    private var start: Long = 0
    private var latch: CountDownLatch? = null

    fun start(count: Int) {
        latch = CountDownLatch(count)
        start = System.currentTimeMillis()
    }

    fun stopAndPrint() {
        latch?.countDown()
        if (latch?.count == 0L) {
            println("Total time: ${System.currentTimeMillis() - start}")
        }
    }
}