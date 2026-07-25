package com.example.tmdapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdapp.data.model.SleepRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecord(record: SleepRecord)

    @Query("SELECT * FROM sleep_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSleepRecordsForUser(userId: String): Flow<List<SleepRecord>>
}
