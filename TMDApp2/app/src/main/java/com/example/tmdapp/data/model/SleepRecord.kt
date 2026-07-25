package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("date") val date: String,
    @SerialName("sleep_hours") val sleepHours: Float,
    @SerialName("sleep_quality") val sleepQuality: String,
    @SerialName("jaw_clenching") val jawClenching: Boolean,
    @SerialName("morning_stiffness") val morningStiffness: String,
    @SerialName("wakeup_feeling") val wakeupFeeling: String,
    @SerialName("notes") val notes: String = "",
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
