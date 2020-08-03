package com.cocosw.optimus

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

abstract class MockResponseSupplier {

    internal fun get(cls: KClass<out MockResponse>, method: Method): Response<*> {
        val value = load(method.toString())
        val field = cls.declaredMemberProperties.firstOrNull { it.name == value }
            ?: findDefaultField(cls)
            ?: cls.declaredMemberProperties.first()
        return field.call(cls.java.newInstance()) as Response<*>
    }


    @SuppressLint("ApplySharedPref")
    internal fun set(method: KFunction<*>, value: KProperty1<*, Response<*>>) {
        save(method.toString(),value.name)
    }

    private fun findDefaultField(cls: KClass<out MockResponse>): KProperty1<out MockResponse, *>? {
        return cls.declaredMemberProperties.firstOrNull {
            it.findAnnotation<Default>() != null
        }
    }

    internal fun index(method: KFunction<*>,cls: KClass<out MockResponse>):Int {
        val value = load(method.toString())
        val field = cls.declaredMemberProperties.firstOrNull { it.name == value }
            ?: findDefaultField(cls)
        return if (field == null) 0
        else {
            cls.declaredMemberProperties.indexOfFirst { it == field }
        }
    }

    internal fun select(idx: Int,method: KFunction<*>,cls: KClass<out MockResponse>) {
        set(method , cls.declaredMemberProperties.toList()[idx] as KProperty1<*, Response<*>>)
    }

    abstract fun save(key:String,value:String)

    abstract fun load(key:String): String?


    companion object {
        fun create(preferences: SharedPreferences): MockResponseSupplier {
            return SharedPreferencesMockResponseSupplier(preferences)
        }
    }

}
