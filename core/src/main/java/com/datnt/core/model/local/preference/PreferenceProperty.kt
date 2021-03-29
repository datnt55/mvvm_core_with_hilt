package com.datnt.core.model.local.preference

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.Int(
    key: String ,
    defaultValue: Int = 0
): ReadWriteProperty<Any, Int> = object : ReadWriteProperty<Any, Int> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return getInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        edit { putInt(key, value) }
    }
}

fun SharedPreferences.String(
    key: String ,
    defaultValue: String = ""
): ReadWriteProperty<Any, String?> = object : ReadWriteProperty<Any, String?> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return getString(key, defaultValue).toString()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        if (value == null){
            edit {
                remove(key)
            }
        }else
            edit { putString(key, value) }
    }
}

fun SharedPreferences.Boolean(
    key: String ,
    defaultValue: Boolean = true
): ReadWriteProperty<Any, Boolean> = object : ReadWriteProperty<Any, Boolean> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        edit { putBoolean(key, value) }
    }
}

inline fun <reified T> SharedPreferences.Object(
    key: String ,
    defaultValue: T
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val value = getString(key, null)
        if (value.isNullOrBlank())
            return defaultValue
        return Gson().fromJson(value,T::class.java)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val pref = Gson().toJson(value)
        edit { putString(key, pref) }
    }
}

inline fun <reified T> SharedPreferences.Arrays(
    key: String ,
    defaultValue: T
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val value = getString(key, null)
        if (value.isNullOrBlank())
            return defaultValue
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(value, type)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val pref = Gson().toJson(value)
        edit { putString(key, pref) }
    }
}