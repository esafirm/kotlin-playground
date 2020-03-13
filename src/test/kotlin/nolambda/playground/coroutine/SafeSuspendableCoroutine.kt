package nolambda.playground.coroutine

import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class OnceContinuation<T>(private val continuation: Continuation<T>) {
    private val isResumed = AtomicBoolean(false)

    fun resumeWith(result: Result<T>) {
        if (isResumed.compareAndSet(false, true)) {
            continuation.resumeWith(result)
        }else{
            println("Already resumed: $continuation with $result")
        }
    }

}

suspend inline fun <T> onceSuspendableCoroutine(crossinline block: (OnceContinuation<T>) -> Unit): T {
    return suspendCoroutine {
        block.invoke(OnceContinuation(it))
    }
}

fun main() = runBlocking {
    println(getSomeString())
}


suspend fun getSomeString(): String {
    return onceSuspendableCoroutine {
        it.resumeWith(Result.success("A"))
        it.resumeWith(Result.success("B"))
    }
}