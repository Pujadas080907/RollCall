package com.example.rollcall.Database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DegreeViewModel(application: Application) : AndroidViewModel(application) {

    private val degreeDao = DegreeDatabase.getDatabase(application).degreeDao()

    private val _degrees = MutableStateFlow<List<Degree>>(emptyList())
    val degreeList: StateFlow<List<Degree>> = _degrees

    init {
        fetchDegrees()
    }

    private fun fetchDegrees() {
        viewModelScope.launch {
            degreeDao.getAllDegrees().collect { degreeList ->
                _degrees.value = degreeList
            }
        }
    }

    fun addDegree(degree: Degree) {
        viewModelScope.launch {
            degreeDao.insertDegree(degree)
        }
    }

    fun deleteDegree(degree: Degree) {
        viewModelScope.launch {
            degreeDao.deleteDegree(degree.id)
        }
    }

    fun updateDegree(degree: Degree) {
        viewModelScope.launch {
            degreeDao.updateDegree(degree.id, degree.degreeName)
        }
    }
}