package com.example.rollcall.studentlists

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val enrollmentNo: String,

)
