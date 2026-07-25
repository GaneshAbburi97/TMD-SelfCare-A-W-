package com.example.tmdapp.data.repository

import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.addJsonObject

@Serializable
data class GroqEdgeResponse(
    val choices: List<GroqEdgeChoice>? = null,
    val error: String? = null
)

@Serializable
data class GroqEdgeChoice(
    val message: GroqEdgeMessage
)

@Serializable
data class GroqEdgeMessage(
    val role: String,
    val content: String
)

class ChatRepository {
    private val supabase = SupabaseClient.client
    private val json = Json { ignoreUnknownKeys = true }

    private val systemPrompt = "You are an AI Health Assistant specifically focused on Temporomandibular Joint (TMJ) disorders and general jaw/facial pain wellness. Provide helpful, empathetic, and concise guidance. Always include a disclaimer that you are not a doctor and users should consult a professional for medical advice."

    private val chatHistory = mutableListOf<Pair<String, String>>() // role to content

    suspend fun sendMessage(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                chatHistory.add("user" to userMessage)

                val messagesArray = buildJsonArray {
                    addJsonObject {
                        put("role", "system")
                        put("content", systemPrompt)
                    }
                    for ((role, content) in chatHistory) {
                        addJsonObject {
                            put("role", role)
                            put("content", content)
                        }
                    }
                }

                val requestBody = buildJsonObject {
                    put("messages", messagesArray)
                    put("model", "llama-3.3-70b-versatile")
                }

                val response = supabase.functions.invoke("groq-chat", body = requestBody)
                val responseText: String = response.body()
                val groqResponse = json.decodeFromString<GroqEdgeResponse>(responseText)

                val assistantMessage = groqResponse.choices?.firstOrNull()?.message?.content
                    ?: groqResponse.error
                    ?: "I'm sorry, I couldn't generate a response."

                chatHistory.add("assistant" to assistantMessage)
                assistantMessage
            } catch (e: Exception) {
                chatHistory.removeLastOrNull()
                "AI assistant temporarily unavailable. Error: ${e.message}"
            }
        }
    }
}
