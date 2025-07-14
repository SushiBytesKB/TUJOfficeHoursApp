// AppDatabase.kt
package com.example.tujofficehoursapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// MODIFICATION: New Room Database class definition
@Database(entities = [UserSettings::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tuj_office_hours_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}