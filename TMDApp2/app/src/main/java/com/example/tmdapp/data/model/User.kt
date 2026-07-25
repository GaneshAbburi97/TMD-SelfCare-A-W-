package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerialName("id") val id: String, // Maps to Supabase auth.users UUID
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("auth_provider") val authProvider: String,
    @SerialName("profile_image_path") val profileImagePath: String? = null,
    @SerialName("height_cm") val heightCm: Float? = null,
    @SerialName("weight_kg") val weightKg: Float? = null,
    @SerialName("created_at") val createdAt: String? = null
)
