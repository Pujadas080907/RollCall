package com.example.rollcall.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rollcall.studentlists.Student
import com.example.rollcall.studentlists.StudentDao


@Database(entities = [Degree::class, Student::class], version = 4, exportSchema = false)
 abstract class DegreeDatabase: RoomDatabase() {

 abstract fun degreeDao(): DegreeDao
 abstract fun studentDao(): StudentDao

 companion object {
  @Volatile
  private var INSTANCE: DegreeDatabase? = null

  fun getDatabase(context: Context): DegreeDatabase {
   return INSTANCE ?: synchronized(this) {
    val instance = Room.databaseBuilder(
     context.applicationContext,
     DegreeDatabase::class.java,
     "degree_database"
    ).fallbackToDestructiveMigration()
     .build()
    INSTANCE = instance
    instance
   }
  }
 }
}