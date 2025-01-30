package com.example.rollcall.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Degree(

    @PrimaryKey(autoGenerate = true)
    var showdegree: String,
)
