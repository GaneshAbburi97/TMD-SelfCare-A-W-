package com.example.tmdapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdapp.data.model.PainRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPainRecord(record: PainRecord)

    @Query("SELECT * FROM pain_records WHERE userId = :userId ORDER BY timestamp ASC")
    fun getPainRecordsForUser(userId: String): Flow<List<PainRecord>>

    @Query("DELETE FROM pain_records WHERE userId = :userId")
    suspend fun deleteAllRecordsForUser(userId: String)
}
