package com.esafirm.kotlin.playground

/* --------------------------------------------------- */
/* > Delegate by Class */
/* --------------------------------------------------- */

interface SomeService {
    fun getString(): String
}

class EmptyService : SomeService {
    override fun getString(): String = "Empty"
}

class HelloService : SomeService by EmptyService()

/* --------------------------------------------------- */
/* > Another One */
/* --------------------------------------------------- */

class JsonService : SomeService by EmptyService() {
    override fun getString(): String = "JSON"
}

class ServicePrinter(service: SomeService) : SomeService by service

fun main(args: Array<String>) {
    println(HelloService().getString())
    println(ServicePrinter(JsonService()).getString())
}
