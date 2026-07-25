package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("date") val date: String,
    @SerialName("exercise_name") val exerciseName: String,
    @SerialName("duration_sec") val durationSec: Int,
    @SerialName("category") val category: String,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
