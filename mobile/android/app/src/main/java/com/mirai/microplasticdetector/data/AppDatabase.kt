package com.mirai.microplasticdetector.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalysisReportEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analysisReportDao(): AnalysisReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aquascan_database"
                )
                    .fallbackToDestructiveMigration() // Wipes old test DB schema safely on version bump
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}