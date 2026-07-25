package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.WellnessRecord
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class WellnessRepository {
    private val supabase = SupabaseClient.client

    suspend fun saveWellnessRecord(
        userId: String,
        sleepQuality: String,
        jawStiffness: String,
        teethGrinding: Boolean,
        mood: String,
        waterIntake: Int,
        energyLevel: Int,
        notes: String
    ): WellnessRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val record = WellnessRecord(
            userId = userId,
            date = date,
            sleepQuality = sleepQuality,
            jawStiffness = jawStiffness,
            teethGrinding = teethGrinding,
            mood = mood,
            waterIntake = waterIntake,
            energyLevel = energyLevel,
            notes = notes
        )
        supabase.postgrest["wellness_records"].insert(record)
        return record
    }

    fun getWellnessRecordsForUser(userId: String): Flow<List<WellnessRecord>> = flow {
        val records = supabase.postgrest["wellness_records"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<WellnessRecord>()
        emit(records)
    }
}
