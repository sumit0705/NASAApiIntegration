package com.example.nasaapiintegration.database

import android.content.SharedPreferences

/**
 * A helper class responsible for managing/updating [sharedPreferences] within the MainActivity.
 */
class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {
    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBooleanData(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveBooleanData(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }
}