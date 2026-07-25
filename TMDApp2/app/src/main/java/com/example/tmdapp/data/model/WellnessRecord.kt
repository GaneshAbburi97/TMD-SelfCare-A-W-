package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "wellness_records")
data class WellnessRecord(
    @PrimaryKey
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("date") val date: String,
    @SerialName("sleep_quality") val sleepQuality: String,
    @SerialName("jaw_stiffness") val jawStiffness: String,
    @SerialName("teeth_grinding") val teethGrinding: Boolean,
    @SerialName("mood") val mood: String,
    @SerialName("water_intake") val waterIntake: Int,
    @SerialName("energy_level") val energyLevel: Int,
    @SerialName("notes") val notes: String = "",
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
