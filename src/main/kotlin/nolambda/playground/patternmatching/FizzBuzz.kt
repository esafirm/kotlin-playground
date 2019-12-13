package nolambda.playground.patternmatching

fun main(args: Array<String>) {

    val fizz: Pattern = FizzPattern()
    val buzz: Pattern = BuzzPattern()
    val fizzBuzz: Pattern = FizzBuzzPattern()

    (1..100).forEach { number ->
        val result: Any = ModResult(number % 3, number % 5)
        when (result) {
            fizzBuzz -> println("FizzBuzz")
            fizz -> println("Fizz")
            buzz -> println("Buzz")
            else -> println(number)
        }
    }
}

class FizzPattern : Pattern {
    override fun match(modResult: ModResult): Boolean = modResult.a == 0
}

class BuzzPattern : Pattern {
    override fun match(modResult: ModResult): Boolean = modResult.b == 0
}

class FizzBuzzPattern : Pattern {
    override fun match(modResult: ModResult): Boolean = modResult.a == 0 && modResult.b == 0
}

interface Pattern {
    fun match(modResult: ModResult): Boolean
}

class ModResult(val a: Int, val b: Int) {

    override fun equals(other: Any?): Boolean {
        if (other is Pattern) {
            return other.match(this)
        }
        return other == this
    }
}


