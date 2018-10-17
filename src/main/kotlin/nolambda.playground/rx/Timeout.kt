package nolambda.playground.rx

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Observable.interval(1, TimeUnit.SECONDS)
            .timeout(6, TimeUnit.SECONDS)
            .map { true }
            .firstOrError()
            .onErrorReturn { false }
            .toObservable()
            .subscribe {
                println("Is true: $it")
            }
    Thread.sleep(20_000)
}