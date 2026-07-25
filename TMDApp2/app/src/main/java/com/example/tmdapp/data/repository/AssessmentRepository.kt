package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssessmentRepository {
    private val supabase = SupabaseClient.client

    fun getAssessmentRecordsForUser(userId: String): Flow<List<AssessmentRecord>> = flow {
        val records = supabase.postgrest["assessment_records"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<AssessmentRecord>()
        emit(records)
    }

    suspend fun saveAssessmentRecord(record: AssessmentRecord) {
        supabase.postgrest["assessment_records"].insert(record)
    }
}
