package nolambda.playground.coruoutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    println("Hello world")

    val firstTask = StartupItem(
            name = "1st task",
            block = { delayAndPrint(it, 300) }
    )

    val secondTask = createTask("2nd task - After 1st task", delay = 500, dependencies = listOf(firstTask))

    StartupManager.add(createTask("After 2nd task", delay = 100, dependencies = listOf(secondTask)))
    StartupManager.add(createTask("After 1st task", delay = 100, dependencies = listOf(firstTask)))

    val firstToFinish = createTask("1st to finish")
    StartupManager.add(createTask("Long task", delay = 400, dependencies = listOf(firstToFinish)))
    StartupManager.add(createTask("Long task #2", delay = 250, dependencies = listOf(firstToFinish)))
    StartupManager.add(createTask("Long task #3", delay = 300, dependencies = listOf(firstToFinish)))

    StartupManager.add(createTask("Small task #1", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #2", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #3", dependencies = listOf(secondTask)))
    StartupManager.add(createTask("Small task #4", dependencies = listOf(secondTask)))

    StartupManager.execute()

    println("All Done")
}

private fun createTask(name: String, delay: Long = 100, dependencies: List<StartupItem>? = null) =
        StartupItem(
                name = name,
                block = { delayAndPrint(it, delay) },
                dependencies = dependencies
        )

private suspend fun delayAndPrint(name: String, delay: Long) {
    delay(delay)
    println(name)
}

data class StartupItem(
        val name: String,
        val block: suspend (String) -> Unit,
        val dependencies: List<StartupItem>? = null,
        val needToWait: Boolean = true
)
typealias DependencyMap = Map<StartupItem, Deferred<*>>
typealias MutableDependencyMap = MutableMap<StartupItem, Deferred<*>>

object StartupManager {

    private val items = mutableSetOf<StartupItem>()

    fun add(item: StartupItem) {
        items.add(item)
        addDependency(item)
    }

    private fun addDependency(item: StartupItem) {
        if (item.dependencies?.isNotEmpty() == true) {
            items.addAll(item.dependencies)
            item.dependencies.forEach {
                addDependency(it)
            }
        }
    }

    fun execute() = logMeasure(
            name = { "Startup.execute" },
            func = {
                runBlocking {
                    val startupItems = items.toList()
                    val standaloneItems = startupItems.filter { it.dependencies == null }
                    val dependentItems = startupItems - standaloneItems

                    val dependencyMap = logMeasure(
                            name = { "Run standalone" },
                            func = { run(standaloneItems) }
                    )
                    val othersDependencyMap = logMeasure(
                            name = { "Run others dependency" },
                            func = { run(dependencyMap, dependentItems) }
                    )
                    val allDependency = othersDependencyMap + dependencyMap

                    logMeasure(
                            name = { "Waiting for dependencies" },
                            func = {
                                allDependency
                                        .filter { it.key.needToWait }
                                        .forEach {
                                            val task = it.value
                                            if (task.isCompleted.not()) {
                                                log { "Waiting for ${it.key.name}" }
                                                task.await()
                                            }
                                        }
                            }
                    )
                }
            }
    )

    private suspend fun run(dependencyMap: DependencyMap, startupItems: List<StartupItem>): DependencyMap {
        val dependencyAddition: MutableDependencyMap = mutableMapOf()
        val unresolvedItem = mutableListOf<StartupItem>()
        startupItems.forEach { sItem ->
            val dependencies = sItem.dependencies!!.mapNotNull { dependencyMap[it] }
            if (dependencies.size != sItem.dependencies.size) {
                unresolvedItem.add(sItem)
                return@forEach
            }

            dependencies.forEach {
                if (!it.isCompleted) {
                    it.await()
                }
            }
            dependencyAddition[sItem] = runStartupItem(sItem)
        }

        unresolvedItem.forEach {
            log { "Unresolved items: ${it.name}" }
        }

        return if (unresolvedItem.isNotEmpty()) {
            dependencyAddition + run(dependencyMap + dependencyAddition, unresolvedItem.toList())
        } else {
            dependencyAddition
        }
    }

    private suspend fun run(items: List<StartupItem>): DependencyMap {
        return items.map { it to runStartupItem(it) }.toMap()
    }

    private suspend fun runStartupItem(item: StartupItem) = GlobalScope.async {
        log { "Running > ${item.name}" }
        item.block.invoke(item.name)
    }

    private inline fun log(message: () -> String) {
        println(message())
    }

    private inline fun <T> logMeasure(name: () -> String, func: () -> T): T {
        var t: T? = null
        val time = measureTimeMillis { t = func() }
        log { "${name()} took $time ms" }
        return t!!
    }

}
