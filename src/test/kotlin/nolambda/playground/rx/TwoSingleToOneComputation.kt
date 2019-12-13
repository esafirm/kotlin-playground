package nolambda.playground.rx

import io.kotlintest.specs.StringSpec
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TwoSingleToOneComputation : StringSpec({

    RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }

    val obs = Single.timer(2, TimeUnit.SECONDS)
            .flatMap { Single.just("Text 1") }

    val ob2 = Single.timer(5, TimeUnit.SECONDS)
            .flatMap { Single.just("Text 2") }

    val concated = Single.concat(obs, ob2)
            .map { "This is $it" }
            .doOnNext { println("Transformed: $it") }

    "Will print the right result" {
        concated.toList().map { "${it[0]}\n${it[1]}" }
                .doOnEvent { res, _ -> println("Result $res") }
                .test()
                .run {
                    assertNoErrors()
                    assertValueCount(1)
                    assertComplete()
                }
    }
})

