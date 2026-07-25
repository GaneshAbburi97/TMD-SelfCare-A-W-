package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class SleepRepository {
    private val supabase = SupabaseClient.client

    suspend fun saveSleepRecord(
        userId: String,
        sleepHours: Float,
        sleepQuality: String,
        jawClenching: Boolean,
        morningStiffness: String,
        wakeupFeeling: String,
        notes: String
    ): SleepRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val record = SleepRecord(
            userId = userId,
            date = date,
            sleepHours = sleepHours,
            sleepQuality = sleepQuality,
            jawClenching = jawClenching,
            morningStiffness = morningStiffness,
            wakeupFeeling = wakeupFeeling,
            notes = notes
        )
        supabase.postgrest["sleep_records"].insert(record)
        return record
    }

    fun getSleepRecordsForUser(userId: String): Flow<List<SleepRecord>> = flow {
        val records = supabase.postgrest["sleep_records"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<SleepRecord>()
        emit(records)
    }
}
