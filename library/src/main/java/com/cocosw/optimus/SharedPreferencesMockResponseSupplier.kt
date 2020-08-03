package com.cocosw.optimus

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

internal class SharedPreferencesMockResponseSupplier(private val preferences: SharedPreferences) :
    MockResponseSupplier() {

    @SuppressLint("ApplySharedPref")
    override fun save(key:String,value:String) {
        preferences.edit()
            .putString(key, value)
            .commit()
    }

    override fun load(key:String): String? {
        return preferences.getString(key, null)
    }
}

