package com.example.tmdapp.data.model

data class ChatMessage(
    val id: String,
    val role: String,      // "user" | "assistant"
    val content: String,
    val timestamp: Long
)
