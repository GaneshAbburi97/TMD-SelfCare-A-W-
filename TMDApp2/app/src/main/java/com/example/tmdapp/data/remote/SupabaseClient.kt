package com.example.tmdapp.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.functions.Functions

object SupabaseClient {
    private const val SUPABASE_URL = "https://iaxrtaqaavckkbftwgjr.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlheHJ0YXFhYXZja2tiZnR3Z2pyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAxMTYyODUsImV4cCI6MjA5NTY5MjI4NX0.a2pVmgCqqlFgfULO-0Euiv2Zdj0L2iiZPoF8eoGk03g"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Functions)
    }
}
