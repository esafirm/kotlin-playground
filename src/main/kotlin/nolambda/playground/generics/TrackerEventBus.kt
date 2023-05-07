package nolambda.playground.generics

import kotlin.reflect.KClass

typealias TrackerListener<T> = Pair<KClass<T>, (T) -> Unit>

object TrackerEventBus {

    const val EVENT_ADD_TO_CART = "AddToCart"

    private val listeners: MutableList<TrackerListener<Any>> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> subscribe(clazz: KClass<T>, block: (T) -> Unit) {
        val pair = clazz to block
        listeners.add(pair as TrackerListener<Any>)
    }

    fun publish(event: Any) {
        listeners.filter { it.first == event::class }.forEach {
            it.second.invoke(event)
        }
    }
}

data class AddToCartEvent(val productName: String)

fun main() {
    TrackerEventBus.subscribe(String::class) {
        println("Testing")
    }
    TrackerEventBus.publish("a")
}