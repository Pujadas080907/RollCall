package com.example.rollcall.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "degree_table")
data class Degree(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var degreeName: String,
    var year: Int,
    var section: String
)
