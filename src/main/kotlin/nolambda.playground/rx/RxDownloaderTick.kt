package nolambda.playground.rx

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    // True is completion
    tickObservable().map { it.toInt() >= 10 }
            .mergeWith(completeOnceObservable().map { true })
            .takeUntil { isComplete -> isComplete == true } // For clarity
            .forEach {
                println("Download Compelte: $it")
            }

    Thread.sleep(11_000)
}

fun completeOnceObservable() = Observable.timer(5, TimeUnit.SECONDS)

fun tickObservable() = Observable.interval(1, TimeUnit.SECONDS).doOnNext { println("tick $it") }