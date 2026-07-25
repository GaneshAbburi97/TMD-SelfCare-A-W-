package com.example.tmdapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class GroqRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<GroqMessage>
)

data class GroqMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class GroqResponse(
    @SerializedName("id") val id: String,
    @SerializedName("choices") val choices: List<GroqChoice>
)

data class GroqChoice(
    @SerializedName("message") val message: GroqMessage
)

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun createChatCompletion(
        @Body request: GroqRequest
    ): GroqResponse
}
