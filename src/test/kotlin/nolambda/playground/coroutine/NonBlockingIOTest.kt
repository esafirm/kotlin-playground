package nolambda.playground.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

/**
 * This demonstrates how to concurrently run two coroutines in a single thread
 * And on of the coroutine is doing non-blocking IO
 */
fun main() {
    runBlocking {
        val filePath = Paths.get("/tmp/test.txt")

        launch {
            val text = "Hello World!".repeat(1_000_000)
            val buffer = ByteBuffer.wrap(text.toByteArray(Charsets.UTF_8))

            try {
                val tookTime = measureTimeMillis {
                    AsynchronousFileChannel.open(
                        filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE
                    ).use { channel ->
                        println("Running in ${Thread.currentThread()}")

                        val writeResult = channel.write(buffer, 0)
                        writeResult.await()
                    }
                }

                val resultText = Files.readString(filePath, Charsets.UTF_8)
                println("The text for the file is: ${resultText.substring(0, 100)}")
                println("Took $tookTime ms")


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        launch {
            repeat(100) {
                println("This is second coroutine printing $it")
                delay(1)
                yield()
            }
        }

    }
}

private suspend fun <T> Future<T>.await(): T {
    while (!isDone) {
        delay(1)
    }
    return get()
}
