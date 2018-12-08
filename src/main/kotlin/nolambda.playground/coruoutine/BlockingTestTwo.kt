package nolambda.playground.coruoutine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() = logMeasure(name = { "Main" }) {

    val firstTask = createTaskTwo(
            name = "1st task", delay = 300
    )

    val secondTask = createTaskTwo("2nd task - After 1st task", delay = 500, dependencies = listOf(firstTask))

    StartupManagerTwo.add(createTaskTwo("After 2nd task", delay = 100, dependencies = listOf(secondTask)))
    StartupManagerTwo.add(createTaskTwo("After 1st task", delay = 100, dependencies = listOf(firstTask)))

    val firstToFinish = createTaskTwo("1st to finish")
    StartupManagerTwo.add(createTaskTwo("Long task", delay = 400, dependencies = listOf(firstToFinish)))
    StartupManagerTwo.add(createTaskTwo("Long task #2", delay = 250, dependencies = listOf(firstToFinish)))
    StartupManagerTwo.add(createTaskTwo("Long task #3", delay = 300, dependencies = listOf(firstToFinish)))

    StartupManagerTwo.add(createTaskTwo("Small task #1", dependencies = listOf(secondTask)))
    StartupManagerTwo.add(createTaskTwo("Small task #2", dependencies = listOf(secondTask)))
    StartupManagerTwo.add(createTaskTwo("Small task #3", dependencies = listOf(secondTask)))
    StartupManagerTwo.add(createTaskTwo("Small task #4", dependencies = listOf(secondTask)))

    StartupManagerTwo.execute()
}

private fun createTaskTwo(name: String, delay: Long = 100, dependencies: List<StartupItemTwo>? = null) =
        StartupItemTwo(
                name = name,
                block = { delayAndPrint(it, delay) },
                dependencies = dependencies
        )


data class StartupItemTwo(
        val name: String,
        val block: suspend (String) -> Unit,
        val dependencies: List<StartupItemTwo>? = null,
        val needToWait: Boolean = true
)

typealias DependencyMap = Map<StartupItemTwo, Deferred<*>>
typealias MutableDependencyMap = MutableMap<StartupItemTwo, Deferred<*>>

object StartupManagerTwo {

    fun creation() {}

    private val items = mutableSetOf<StartupItemTwo>()

    fun add(item: StartupItemTwo) {
        items.add(item)
        addDependency(item)
    }

    private fun addDependency(item: StartupItemTwo) {
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

    private suspend fun run(dependencyMap: DependencyMap, startupItems: List<StartupItemTwo>): DependencyMap {
        val dependencyAddition: MutableDependencyMap = mutableMapOf()
        val unresolvedItem = mutableListOf<StartupItemTwo>()
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

        return if (unresolvedItem.isNotEmpty()) {
            dependencyAddition + run(dependencyMap + dependencyAddition, unresolvedItem)
        } else {
            dependencyAddition
        }
    }

    private suspend fun run(items: List<StartupItemTwo>): DependencyMap {
        return items.map { it to runStartupItem(it) }.toMap()
    }

    private suspend fun runStartupItem(item: StartupItemTwo) = GlobalScope.async {
        logMeasure(
                name = { item.name },
                func = { item.block.invoke(item.name) }
        )
    }
}