package com.example.tmdapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdapp.data.model.ExerciseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseRecord(record: ExerciseRecord)

    @Query("SELECT * FROM exercise_records WHERE userId = :userId ORDER BY timestamp ASC")
    fun getExerciseRecordsForUser(userId: String): Flow<List<ExerciseRecord>>

    @Query("DELETE FROM exercise_records WHERE userId = :userId")
    suspend fun deleteAllRecordsForUser(userId: String)
}
