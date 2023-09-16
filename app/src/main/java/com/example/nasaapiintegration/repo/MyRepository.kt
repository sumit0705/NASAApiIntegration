package com.example.nasaapiintegration.repo

import com.example.nasaapiintegration.database.SharedPreferencesHelper

/**
 * A helper class responsible for updating [sharedPreferencesHelper] values.
 */
class MyRepository(private val sharedPreferencesHelper: SharedPreferencesHelper) {
    fun saveData(key: String, value: String) {
        sharedPreferencesHelper.saveData(key, value)
    }

    fun getData(key: String): String? {
        return sharedPreferencesHelper.getData(key)
    }

    fun getBooleanData(key: String): Boolean {
        return sharedPreferencesHelper.getBooleanData(key)
    }

    fun saveBooleanData(key: String, value: Boolean) {
        sharedPreferencesHelper.saveBooleanData(key, value)
    }

    fun clearCache() {
        sharedPreferencesHelper.clearCache()
    }
}