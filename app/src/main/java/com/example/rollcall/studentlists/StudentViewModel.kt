package com.example.rollcall.studentlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rollcall.Database.DegreeDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application)  {

    private val studentDao = DegreeDatabase.getDatabase(application).studentDao()

    fun getStudentsByDegreeYearSection(degreeId: Int, year: Int, section: String): Flow<List<Student>> {
        return studentDao.getStudentsByDegreeYearSection(degreeId, year, section)
            .map { students ->
                students.sortedWith(compareBy { extractNumericPart(it.enrollmentNo) })
            }
    }
    private fun extractNumericPart(enrollmentNo: String): Int {
        return enrollmentNo.filter { it.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE
    }

    fun addStudent(fullName: String, enrollmentNo: String, degreeId: Int, year: Int, section: String) {
        val student = Student(
            fullName = fullName,
            enrollmentNo = enrollmentNo,
            degreeId = degreeId,
            year = year,
            section = section
        )
        viewModelScope.launch {
            studentDao.insertStudent(student)
        }
    }

}