package com.example.tmdapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdapp.data.model.AssessmentRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AssessmentRecord)

    @Query("SELECT * FROM assessment_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAssessmentRecordsForUser(userId: String): Flow<List<AssessmentRecord>>
    
    @Query("SELECT * FROM assessment_records WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestAssessmentRecordForUser(userId: String): AssessmentRecord?
}
