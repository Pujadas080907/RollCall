package com.example.rollcall.studentlists

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
    @Query("""
    SELECT * FROM students 
    WHERE degreeId = :degreeId AND year = :year AND section = :section 
    ORDER BY CAST(SUBSTR(enrollmentNo, LENGTH(enrollmentNo) - 2, 3) AS INTEGER) ASC
""")
    fun getStudentsByDegreeYearSection(degreeId: Int, year: Int, section: String): Flow<List<Student>>

}