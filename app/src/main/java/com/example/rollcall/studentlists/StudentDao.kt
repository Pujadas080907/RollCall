package com.example.rollcall.studentlists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)


    @Query("""
    SELECT * FROM students 
    WHERE degreeId = :degreeId AND year = :year AND section = :section 
    ORDER BY CAST(SUBSTR(enrollmentNo, LENGTH(enrollmentNo) - 2, 3) AS INTEGER) ASC
""")
    fun getStudentsByDegreeYearSection(degreeId: Int, year: Int, section: String): Flow<List<Student>>

}