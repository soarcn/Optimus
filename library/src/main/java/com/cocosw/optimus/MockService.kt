package com.cocosw.optimus

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

abstract class MockService internal constructor(
    val clazz: Class<*>,
    val name: String
) {

    internal val definitions = arrayListOf<MockDefinition>()

    infix fun KFunction<*>.with(kClass: KClass<out MockResponse>): MockDefinition {
        return MockDefinition(this, kClass).apply { definitions.add(this) }
    }

    infix fun MockDefinition.named(name: String) {
        this.name = name
    }
}

fun <T> alter(
    clazz: Class<T>,
    name: String = clazz.simpleName,
    block: MockService.() -> Unit
): MockService {
    return object : MockService(clazz, name) {}.apply(block)
}

class DefinitionParameters internal constructor(vararg val values: Any?) {

    private fun <T> elementAt(i: Int): T = values[i] as T

    operator fun <T> component1(): T = elementAt(0)
    operator fun <T> component2(): T = elementAt(1)
    operator fun <T> component3(): T = elementAt(2)
    operator fun <T> component4(): T = elementAt(3)
    operator fun <T> component5(): T = elementAt(4)

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = values[i] as T

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get() = values.first { it is T }
}

class MockDefinition(
    internal val kFunction: KFunction<*>,
    internal val kClass: KClass<out MockResponse>,
    internal var name: String = kFunction.name
)
