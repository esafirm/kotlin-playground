package nolambda.playground.rxdelay

import io.kotlintest.specs.StringSpec
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RxDelaySpec : StringSpec({

    "FlatMap to Observable timer" {
        Observable.just(1)
                .flatMap {
                    Observable.timer(5, TimeUnit.SECONDS, Schedulers.trampoline())
                            .flatMap { Observable.just(2) }
                }
                .test()
                .run {
                    assertNoErrors()
                    assertValueCount(1)
                    assertValue(2)
                }
    }

    "Delay should be just fine too" {
        Observable.just(1)
                .flatMap {
                    Observable.just(2)
                            .delay(5, TimeUnit.SECONDS, Schedulers.trampoline())
                }
                .test()
                .run {
                    assertNoErrors()
                    assertValueCount(1)
                    assertValue(2)
                }
    }

})