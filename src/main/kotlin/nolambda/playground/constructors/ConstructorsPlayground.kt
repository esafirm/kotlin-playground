
fun main(args: Array<String>) {
    Bird()
    println("======")
    Bird("Pigeon")
    println("======")
    Dog()
    Dog("Husky")

    println(House("Alabama").location)
    println(HousePrimaryConstructor("Bandung").location)
}

class HousePrimaryConstructor(val location: String)

class House {

    var location: String

    constructor(location: String) {
        this.location = location
    }

}

abstract class Animal(name: String) {

    constructor() : this("Animal") {
        println("init in Animal secondary contructor")
    }

    init {
        println("init in Animal")
        onInitCalled()
    }

    abstract fun onInitCalled()
}

class Bird(name: String) : Animal(name) {

    val b = println("print in Bird")

    constructor() : this("Bird") {
        println("init in Bird secondary constructor")
    }

    init {
        println("init in Bird")
    }

    override fun onInitCalled() {
        println("on init called")
    }
}

class Dog() : Animal() {
    override fun onInitCalled() {}

    constructor(breed: String) : this() {
        println("print the $breed")
    }

    init {
        println("init in Dog")
    }
}