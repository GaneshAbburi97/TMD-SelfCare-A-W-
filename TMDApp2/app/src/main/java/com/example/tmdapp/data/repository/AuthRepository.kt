package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.User
import com.example.tmdapp.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {
    private val supabase = SupabaseClient.client

    suspend fun signUp(name: String, email: String, passwordRaw: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = passwordRaw
            data = buildJsonObject {
                put("full_name", name)
            }
        }
    }

    suspend fun login(email: String, passwordRaw: String): Boolean {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = passwordRaw
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun verifyEmailOtp(email: String, token: String): Boolean {
        return try {
            supabase.auth.verifyEmailOtp(
                type = io.github.jan.supabase.auth.OtpType.Email.SIGNUP,
                email = email,
                token = token
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        return try {
            supabase.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.IDToken) {
                this.idToken = idToken
                this.provider = Google
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCurrentUser(): User? {
        val sessionUser = supabase.auth.currentUserOrNull() ?: return null
        return try {
            supabase.postgrest["users"]
                .select { filter { eq("id", sessionUser.id) } }
                .decodeSingleOrNull<User>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, email: String, imagePath: String?, heightCm: Float? = null, weightKg: Float? = null): Boolean {
        return try {
            supabase.postgrest["users"].update(
                buildJsonObject {
                    put("name", name)
                    put("email", email)
                    put("profile_image_path", imagePath)
                    if (heightCm != null) put("height_cm", heightCm)
                    if (weightKg != null) put("weight_kg", weightKg)
                }
            ) {
                filter { eq("id", userId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAccount(userId: String): Boolean {
        return try {
            supabase.postgrest["users"].delete {
                filter { eq("id", userId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun logout() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
