package nolambda.playground.rx

import io.reactivex.Observable

fun main(args: Array<String>) {
    Observable.just(1,2,3)
            .scan { numOne, numTwo ->
                println("Scan with: $numOne - $numTwo")
                numOne + numTwo
            }
            .subscribe {
                println("Called $it")
            }
}
