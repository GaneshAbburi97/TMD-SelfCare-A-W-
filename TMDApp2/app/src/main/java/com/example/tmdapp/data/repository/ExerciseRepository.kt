package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.ExerciseRecord
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseRepository {
    private val supabase = SupabaseClient.client

    suspend fun saveRecord(userId: String, exerciseName: String, durationSec: Int, category: String): ExerciseRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val newRecord = ExerciseRecord(
            userId = userId,
            date = date,
            exerciseName = exerciseName,
            durationSec = durationSec,
            category = category
        )
        supabase.postgrest["exercise_records"].insert(newRecord)
        return newRecord
    }

    fun getRecordsForUser(userId: String): Flow<List<ExerciseRecord>> = flow {
        val records = supabase.postgrest["exercise_records"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<ExerciseRecord>()
        emit(records)
    }
}
