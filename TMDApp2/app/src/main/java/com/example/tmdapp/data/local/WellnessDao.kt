package com.example.tmdapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdapp.data.model.WellnessRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWellnessRecord(record: WellnessRecord)

    @Query("SELECT * FROM wellness_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getWellnessRecordsForUser(userId: String): Flow<List<WellnessRecord>>
}
