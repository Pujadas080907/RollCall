package com.example.rollcall.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Degree::class], version = 2, exportSchema = false)
 abstract class DegreeDatabase: RoomDatabase() {

 abstract fun degreeDao(): DegreeDao

 companion object {
  @Volatile
  private var INSTANCE: DegreeDatabase? = null

  fun getDatabase(context: Context): DegreeDatabase {
   return INSTANCE ?: synchronized(this) {
    val instance = Room.databaseBuilder(
     context.applicationContext,
     DegreeDatabase::class.java,
     "degree_database"
    )
     .fallbackToDestructiveMigration()
     .build()
    INSTANCE = instance
    instance
   }
  }
 }
}