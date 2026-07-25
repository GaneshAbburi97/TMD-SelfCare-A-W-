package com.example.tmdapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
@Entity(tableName = "assessment_records")
data class AssessmentRecord(
    @PrimaryKey
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerialName("date") val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(timestamp)),
    
    // 12 Habit Questions (Boolean)
    @SerialName("q1_teeth_grinding") val q1TeethGrinding: Boolean,
    @SerialName("q2_jaw_clenching") val q2JawClenching: Boolean,
    @SerialName("q3_chew_gum") val q3ChewGum: Boolean,
    @SerialName("q4_bite_nails") val q4BiteNails: Boolean,
    @SerialName("q5_jaw_clicking") val q5JawClicking: Boolean,
    @SerialName("q6_difficulty_chewing") val q6DifficultyChewing: Boolean,
    @SerialName("q7_morning_stiffness") val q7MorningStiffness: Boolean,
    @SerialName("q8_frequent_headaches") val q8FrequentHeadaches: Boolean,
    @SerialName("q9_sleep_less_than_6_hours") val q9SleepLessThan6Hours: Boolean,
    @SerialName("q10_high_stress") val q10HighStress: Boolean,
    @SerialName("q11_poor_posture") val q11PoorPosture: Boolean,
    @SerialName("q12_one_side_chewing") val q12OneSideChewing: Boolean,
    
    // Additional Inputs
    @SerialName("sleep_duration") val sleepDuration: Float,
    @SerialName("water_intake") val waterIntake: Float,
    @SerialName("stress_frequency") val stressFrequency: String,
    @SerialName("jaw_pain_frequency") val jawPainFrequency: String,
    @SerialName("exercise_consistency") val exerciseConsistency: String,
    
    // Generated Analysis
    @SerialName("smart_analysis") val smartAnalysis: String
)
