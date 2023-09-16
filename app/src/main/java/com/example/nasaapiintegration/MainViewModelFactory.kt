package com.example.nasaapiintegration

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaapiintegration.repo.MyRepository

/** Factory which is responsible to instantiate [MainViewModel]. */
class MainViewModelFactory(private val application: Application, private val repo: MyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}