package com.cocosw.optimus

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

internal class SharedPreferencesMockResponseSupplier(private val preferences: SharedPreferences) : MockResponseSupplier {

    override fun get(cls: KClass<out MockResponse>, method: Method): Response<*> {
        val value = preferences.getString(method.toString(), null)
        val field = cls.java.fields.firstOrNull{it.name == value}?:cls.java.fields[0]
        return field.get(cls.objectInstance) as Response<*>
    }

    @SuppressLint("ApplySharedPref")
    override fun set(method: KFunction<Any>, value: KProperty1<*, *>) {
        preferences.edit()
            .putString(method.toString(), value.name)
            .commit()
    }
}

fun MockResponseSupplier.create(preferences: SharedPreferences): MockResponseSupplier {
    return SharedPreferencesMockResponseSupplier(preferences)
}
