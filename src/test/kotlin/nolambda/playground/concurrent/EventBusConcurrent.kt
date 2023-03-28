package nolambda.playground.concurrent

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.Executors

class EventBusSafeRegisterSpec : StringSpec({

    val eventBus = EventBus.getDefault()
    val dispatchers = Executors.newFixedThreadPool(24).asCoroutineDispatcher()

    "Event Bus register should not be safe" {
        var lastError: Throwable? = null
        val errorHandler = CoroutineExceptionHandler { _, err ->
            lastError = err
        }

        val scope = CoroutineScope(dispatchers + errorHandler)
        eventBus.executeTests(scope = scope, useSynchronized = false)

        (lastError != null) shouldBe true
    }

    "Event Bus register should be safe because synchronized" {
        var lastError: Throwable? = null
        val errorHandler = CoroutineExceptionHandler { _, err ->
            lastError = err
        }

        val scope = CoroutineScope(dispatchers + errorHandler)
        eventBus.executeTests(scope = scope, useSynchronized = true)

        lastError shouldBe null
    }

})

private fun EventBus.executeTests(
    repeatCount: Int = 1000,
    scope: CoroutineScope,
    useSynchronized: Boolean
) {
    val subscriber = EventBusSubscriber()
    val jobs = mutableListOf<Job>()

    repeat(repeatCount) {
        jobs += scope.launch {
            if (useSynchronized) {
                safeRegisterSynchronized(subscriber)
            } else {
                safeRegister(subscriber)
            }
        }
        jobs += scope.launch {
            logUnregister(subscriber)
        }
    }

    runBlocking {
        jobs.joinAll()
        log("Done!")
    }
}

private fun log(message: String) {
    println("-> $message -- ${Thread.currentThread().name}")
}

private fun EventBus.safeRegister(subscriber: Any) {
    if (!isRegistered(subscriber)) {
        log("Register!")
        register(subscriber)
    }
}

@Synchronized
private fun EventBus.safeRegisterSynchronized(subscriber: Any) {
    if (isRegistered(subscriber)) {
        log("Unregister!")
        register(subscriber)
    }
}

private fun EventBus.logUnregister(subscriber: Any) {
    log("Unregister!")
    unregister(subscriber)
}

private class EventBusSubscriber {
    @Suppress("unused")
    @Subscribe
    fun testing(event: TestEvent) {
        println("Testing")
    }
}

private class TestEvent
