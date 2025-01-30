package com.example.rollcall.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DegreeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDegree(degree: Degree)

    @Query("SELECT * FROM degree_table ORDER BY id DESC")
    fun getAllDegrees(): Flow<List<Degree>>

    @Query("DELETE FROM degree_table WHERE id = :degreeId")
    suspend fun deleteDegree(degreeId: Int)

    @Query("UPDATE degree_table SET degreeName = :newName WHERE id = :degreeId")
    suspend fun updateDegree(degreeId: Int, newName: String)

}