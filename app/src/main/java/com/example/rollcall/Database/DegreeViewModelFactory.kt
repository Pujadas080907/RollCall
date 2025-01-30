package com.example.rollcall.Database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DegreeViewModelFactory(private val application: Application) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DegreeViewModel::class.java)) {
            return DegreeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}