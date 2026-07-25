package com.example.tmdapp.data.model

data class Appointment(
    val id: String,
    val doctorName: String,
    val date: String,
    val time: String,
    val status: String = "Confirmed"
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
