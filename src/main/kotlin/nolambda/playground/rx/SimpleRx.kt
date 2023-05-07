package nolambda.playground.rx

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

fun main() {
    Single.just(1)
        .subscribeOn(Schedulers.computation())
        .map {
            p("doing map")
            it
        }
        .observeOn(Schedulers.newThread())
        .map {
            p("doing map again")
            it
        }
        .doOnSubscribe { p("Do on subscribe:") }
        .subscribe { elements, err ->
            p("$elements")
            p("$err")
        }

    Thread.sleep(1000)
}

private fun p(msg: String) {
    println("$msg | ${Thread.currentThread().name}")
}