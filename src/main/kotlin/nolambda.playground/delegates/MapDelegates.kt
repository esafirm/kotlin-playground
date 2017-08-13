package com.esafirm.kotlin.playground.delegates

class Animal(map: Map<String, String>) {
    val name by map
    val weight by map

    override fun toString(): String = "Animal: $name $weight"
}

fun main(args: Array<String>) {
    print(Animal(mapOf("name" to "ayam", "weight" to "2 Ton")))
}