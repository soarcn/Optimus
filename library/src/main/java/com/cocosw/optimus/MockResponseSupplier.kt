package com.cocosw.optimus

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

interface MockResponseSupplier {
    fun get(cls: KClass<out MockResponse>, method: Method): Response<*>
    fun set(method: KFunction<Any>, value: KProperty1<*, *>)
}

