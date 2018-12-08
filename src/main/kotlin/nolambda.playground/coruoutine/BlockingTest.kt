package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun main() = logMeasure(name = { "Main" }) {
    test2()
}

private fun test1() {
    val firstTask = StartupManager.add(StartupItem(
            name = "1st task",
            block = { delayAndPrint(it, 300) }
    ))

    val secondTask = StartupManager.add(
            createTask("2nd task - After 1st task", delay = 500, dependencies = listOf(firstTask)))

    StartupManager.add(createTask("After 2nd task", delay = 100, dependencies = listOf(secondTask)))
    StartupManager.add(createTask("After 1st task", delay = 100, dependencies = listOf(firstTask)))

    val firstToFinish = StartupManager.add(createTask("1st to finish"))
    StartupManager.add(createTask("Long task", delay = 400, dependencies = listOf(firstToFinish)))
    StartupManager.add(createTask("Long task #2", delay = 250, dependencies = listOf(firstToFinish)))
    StartupManager.add(createTask("Long task #3", delay = 300, dependencies = listOf(firstToFinish)))

    StartupManager.add(createTask("Small task #1", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #2", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #3", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #4", dependencies = listOf(secondTask)))

    StartupManager.execute()
}

private fun test2() {
    val configurator = StartupManager.add(createTask("#1", delay = 50))
    StartupManager.add(createTask("Neo", delay = 120, dependencies = listOf(configurator)))
    StartupManager.add(createTask("ST#1", delay = 50, dependencies = listOf(configurator)))
    StartupManager.add(createTask("ST#2", delay = 60, dependencies = listOf(configurator)))
    StartupManager.add(createTask("ST#3", delay = 40, dependencies = listOf(configurator)))

    StartupManager.add(StartupItem("Hydro", block = { Thread.sleep(400) }, needToWait = false))

    StartupManager.execute()
}


private fun createTask(name: String, delay: Long = 100, dependencies: List<Result>? = null) =
        StartupItem(
                name = name,
                block = { delayAndPrint(it, delay) },
                dependencies = dependencies
        )

suspend fun delayAndPrint(name: String, delay: Long) {
    delay(delay)
    println(name)
}


data class StartupItem(
        val name: String,
        val block: suspend (String) -> Unit,
        val dependencies: List<Result>? = null,
        val needToWait: Boolean = true
)

typealias Result = Deferred<*>

object StartupManager {

    private val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    private val startupMap = mutableMapOf<StartupItem, Result>()

    fun add(item: StartupItem): Result {
        val task = GlobalScope.async {
            item.dependencies?.forEach { it.awaitIfNotComplete() }
            runStartupItem(item)
        }
        startupMap[item] = task
        return task
    }

    fun execute() = logMeasure(
            name = { "Startup.execute" },
            func = {
                runBlocking {
                    startupMap.forEach {
                        val item = it.key
                        val task = it.value

                        if (item.needToWait) {
                            task.awaitIfNotComplete()
                        }
                    }
                }
            }
    )


    private suspend inline fun runStartupItem(item: StartupItem) = withContext(dispatcher) {
        logMeasure(
                name = { item.name },
                func = { item.block.invoke(item.name) }
        )
    }

    private suspend fun Deferred<*>.awaitIfNotComplete() {
        if (!isCompleted) {
            await()
        }
    }

    private suspend fun Job.joinIfNotCompleted() {
        if (!isCompleted) {
            join()
        }
    }

}


inline fun <T> logMeasure(name: () -> String, func: () -> T): T {
    var t: T? = null
    val time = measureTimeMillis { t = func() }
    log { "${name()} took $time ms" }
    return t!!
}

inline fun log(message: () -> String) {
    println(message())
}
