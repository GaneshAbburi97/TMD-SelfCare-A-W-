package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "pain_records")
data class PainRecord(
    @PrimaryKey
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("date") val date: String,
    @SerialName("pain_level") val painLevel: Int,
    @SerialName("stress_level") val stressLevel: Int,
    @SerialName("location") val location: String,
    @SerialName("type") val type: String = "Dull",
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
