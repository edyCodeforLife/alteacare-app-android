package id.altea.care.core.ext

import android.content.SharedPreferences

fun SharedPreferences.update(vararg values: Pair<Any, String>) {
    val editor = this.edit()
    values.forEach {
        when (it.first) {
            is Int -> editor.putInt(it.second, it.first as Int)
            is Boolean -> editor.putBoolean(it.second, it.first as Boolean)
            is Float -> editor.putFloat(it.second, it.first as Float)
            is Long -> editor.putLong(it.second, it.first as Long)
            is String -> editor.putString(it.second, it.first as String)
        }
    }
    editor.apply()
}
