package com.example.tmdapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.User
import com.example.tmdapp.data.model.WellnessRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.ExerciseRecord

@Database(entities = [User::class, PainRecord::class, WellnessRecord::class, SleepRecord::class, AssessmentRecord::class, ExerciseRecord::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun painDao(): PainDao
    abstract fun wellnessDao(): WellnessDao
    abstract fun sleepDao(): SleepDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tmd_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
