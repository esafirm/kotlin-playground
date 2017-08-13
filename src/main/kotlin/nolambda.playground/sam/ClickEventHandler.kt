package com.esafirm.kotlin.playground.sam

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun main(args: Array<String>) {
    ClickHandler().apply {

        setOnClickListener { println(it) }
        setOnClickListener { num: Long -> print(num) }

    }.run {
        sendEvent()
    }

    /*
    Can't do this!
    setClickListener(OnClickKotlin {
        println(it)
    })*/


    val composite = CompositeDisposable()
    composite += Observable.just(1)
            .subscribeOn(Schedulers.single())
            .subscribe({ println(it) }, { println(it) })
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun setClickListener(listener: OnClickListenerJava) {}
fun ClickHandler.setOnClickListener(block: (Long) -> Unit) = setOnClickListener(object : OnClickKotlin {
    override fun onClick(viewId: Long) {
        block(viewId)
    }
})

