package com.cocosw.optimus

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod

class InMemoryMockResponseSupplier : MockResponseSupplier {
    val map: HashMap<String, String> = HashMap()

    override fun get(cls: KClass<out MockResponse>, method: Method): Response<*> {
        val value = map[method.toString()]
        val field = cls.declaredMemberProperties.firstOrNull { it.name == value }
            ?: findDefaultField(cls)
            ?: cls.declaredMemberProperties.first()
        return field.call(cls.java.newInstance()) as Response<*>
    }

    private fun findDefaultField(cls: KClass<out MockResponse>): KProperty1<out MockResponse, *>? {
        return cls.declaredMemberProperties.firstOrNull {
            it.findAnnotation<Default>() != null
        }
    }

    override fun set(method: KFunction<Any>, value: KProperty1<*, *>) {
        map[method.javaMethod.toString()] = value.name
    }

    fun reset() {
        map.clear()
    }
}
