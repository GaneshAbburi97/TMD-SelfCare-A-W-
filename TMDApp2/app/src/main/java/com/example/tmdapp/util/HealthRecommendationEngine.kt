package com.example.tmdapp.util

import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.ExerciseRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord
import com.example.tmdapp.ui.screens.Difficulty
import com.example.tmdapp.ui.screens.Exercise

object HealthRecommendationEngine {

    /**
     * Calculates Daily Progress (0-100%) dynamically based on completed health actions today.
     * Completing all 4 core actions (Pain, Sleep, Wellness, Exercises) = 100%.
     */
    fun calculateDailyProgress(
        hasPainLog: Boolean,
        hasSleepLog: Boolean,
        hasWellnessLog: Boolean,
        completedExercisesCount: Int
    ): Int {
        var progress = 0
        if (hasPainLog) progress += 25
        if (hasSleepLog) progress += 25
        if (hasWellnessLog) progress += 25
        
        // Exercise completions contribute up to 25% (cap at 3 exercises for full score)
        val exerciseScore = minOf(25, completedExercisesCount * 9) // 1=9%, 2=18%, 3=25% (capped)
        progress += exerciseScore
        
        return minOf(100, progress)
    }

    /**
     * Computes a dynamic Recovery Score (0-100) based on the last 7 days of logs.
     * Lower pain/stress, better sleep, higher energy, and consistent exercises improve the score.
     */
    fun calculateRecoveryScore(
        painRecords: List<PainRecord>,
        sleepRecords: List<SleepRecord>,
        wellnessRecords: List<WellnessRecord>,
        exerciseRecords: List<ExerciseRecord>
    ): Int {
        if (painRecords.isEmpty() && sleepRecords.isEmpty() && wellnessRecords.isEmpty() && exerciseRecords.isEmpty()) {
            return 0 // No data yet
        }

        // 1. Pain & Stress (0 to 40 points) -> Lower is better
        val avgPain = painRecords.map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 5.0
        val avgStress = painRecords.map { it.stressLevel }.average().takeIf { !it.isNaN() } ?: 5.0
        val painStressScore = maxOf(0.0, 40.0 - (avgPain * 2.0) - (avgStress * 2.0))

        // 2. Sleep (0 to 20 points)
        val avgSleep = sleepRecords.map { it.sleepHours.toDouble() }.average().takeIf { !it.isNaN() } ?: 6.0
        val sleepScore = minOf(20.0, (avgSleep / 8.0) * 20.0) // 8 hours = 20 pts

        // 3. Wellness (0 to 20 points)
        val avgEnergy = wellnessRecords.map { it.energyLevel.toDouble() }.average().takeIf { !it.isNaN() } ?: 5.0
        val wellnessScore = minOf(20.0, (avgEnergy / 10.0) * 20.0)

        // 4. Exercise Consistency (0 to 20 points)
        // Count distinct days with at least one exercise
        val activeExerciseDays = exerciseRecords.map { it.date }.distinct().count()
        val exerciseScore = minOf(20.0, (activeExerciseDays / 5.0) * 20.0) // 5 active days = max 20 pts

        return (painStressScore + sleepScore + wellnessScore + exerciseScore).toInt().coerceIn(0, 100)
    }

    /**
     * Dynamically filters and reorders the full exercise list based on the user's latest logged symptoms.
     */
    fun getRecommendedExercises(
        allExercises: List<Exercise>,
        latestPain: Int,
        latestStress: Int,
        latestSleepQuality: String,
        latestJawStiffness: String,
        teethGrinding: Boolean,
        poorPosture: Boolean
    ): List<Exercise> {
        // 1. Base Pain Filtering
        var filtered = when (latestPain) {
            in 7..10 -> allExercises.filter {
                it.category in listOf("Relaxation", "Stress Relief") ||
                (it.category == "Mobility" && it.difficulty == Difficulty.MILD)
            }
            in 4..6 -> allExercises.filter { it.category != "Strengthening" }
            else -> allExercises
        }

        // 2. Adjust for specific symptoms
        if (teethGrinding || latestJawStiffness == "Severe") {
            // Remove heavy jaw strengthening if grinding or severely stiff
            filtered = filtered.filter { it.category != "Strengthening" }
        }

        // 3. Prioritize exercises based on conditions (Move to top)
        val priorityExercises = mutableListOf<Exercise>()
        val regularExercises = mutableListOf<Exercise>()

        for (ex in filtered) {
            val isPriority = when {
                latestStress >= 7 && ex.category in listOf("Stress Relief", "Relaxation") -> true
                latestSleepQuality == "Poor" && ex.name in listOf("Guided Jaw Relaxation", "Diaphragmatic Breathing") -> true
                poorPosture && ex.name in listOf("Chin Tucks", "Shoulder Rolls") -> true
                latestJawStiffness == "Severe" && ex.name == "Warm Compress" -> true
                else -> false
            }
            if (isPriority) priorityExercises.add(ex) else regularExercises.add(ex)
        }

        return priorityExercises + regularExercises
    }

    /**
     * Generates a dynamic, symptom-responsive recommendation text snippet.
     */
    fun generatePersonalizedRecommendation(
        latestPain: PainRecord?,
        latestSleep: SleepRecord?,
        latestWellness: WellnessRecord?,
        assessment: AssessmentRecord?
    ): String {
        val pain = latestPain?.painLevel ?: 5
        val stress = latestPain?.stressLevel ?: 5
        val sleepQuality = latestWellness?.sleepQuality ?: latestSleep?.sleepQuality ?: "Average"
        
        if (pain >= 7) {
            return "⚠️ Severe pain detected. Apply a warm compress and limit jaw movement. Stick to mild relaxation exercises."
        }
        
        if (stress >= 7) {
            return "🧘 High stress can trigger jaw clenching. Box breathing and relaxation are highly recommended today."
        }
        
        if (sleepQuality == "Poor") {
            return "💤 Poor sleep observed. Focus on guided jaw relaxation and avoid heavy chewing to prevent tension."
        }
        
        if (assessment?.q11PoorPosture == true) {
            return "👤 Posture check: Keep your head aligned with your shoulders. Try some Chin Tucks to relieve neck strain."
        }
        
        if (assessment?.q1TeethGrinding == true || latestWellness?.teethGrinding == true) {
            return "🦷 Teeth grinding alert: Practice gentle jaw mobility exercises to release muscle tightness."
        }
        
        if (pain in 4..6) {
            return "Moderate discomfort. Gentle mobility and stretching will help ease the tension."
        }
        
        return "You're doing great! Keep up with your strengthening and mobility exercises to maintain jaw health."
    }
}
