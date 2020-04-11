package nolambda.playground.proxy

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


interface StringGeneratorInterface {
    fun generateString(key: String? = ""): String
}

class ProxySpec : StringSpec({

    val expectedString = "Some String"

    val clazz = StringGeneratorInterface::class.java
    val proxyClass = Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz),
        object : InvocationHandler {
            @Throws(Throwable::class)
            override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
                println("Method: $method")
                println("Args: ${args?.joinToString(",")}")

                // If the method is a method from Object then defer to normal invocation.
                if (method.declaringClass === Any::class.java) {
                    return method.invoke(this, args)
                }

                return expectedString
            }
        }) as StringGeneratorInterface

    "Proxy class will generate string" {
        proxyClass.generateString("asdasdasd") shouldBe expectedString
    }
})
