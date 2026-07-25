package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PainRepository {
    private val supabase = SupabaseClient.client

    suspend fun saveRecord(userId: String, pain: Int, stress: Int, location: String, type: String): PainRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val newRecord = PainRecord(
            userId = userId,
            date = date,
            painLevel = pain,
            stressLevel = stress,
            location = location,
            type = type
        )
        supabase.postgrest["pain_records"].insert(newRecord)
        return newRecord
    }

    fun getRecordsForUser(userId: String): Flow<List<PainRecord>> = flow {
        val records = supabase.postgrest["pain_records"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<PainRecord>()
        emit(records)
    }
}
