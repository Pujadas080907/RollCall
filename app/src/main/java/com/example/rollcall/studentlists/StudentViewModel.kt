package com.example.rollcall.studentlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rollcall.Database.DegreeDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application)  {

    private val studentDao = DegreeDatabase.getDatabase(application).studentDao()

    val students: Flow<List<Student>> = studentDao.getAllStudents()

    fun addStudent(fullName: String, enrollmentNo: String) {
        val student = Student(fullName = fullName, enrollmentNo = enrollmentNo)
        viewModelScope.launch {
            studentDao.insertStudent(student)
        }
    }

}