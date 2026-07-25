package com.example.tmdapp.util

import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object DemoDataGenerator {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private const val DEMO_DAYS = 30

    data class MergedHistory(
        val painHistory: List<PainRecord>,
        val sleepHistory: List<SleepRecord>,
        val wellnessHistory: List<WellnessRecord>,
        val assessmentHistory: List<AssessmentRecord>
    )

    fun mergeWithRealData(
        realPain: List<PainRecord>,
        realSleep: List<SleepRecord>,
        realWellness: List<WellnessRecord>,
        realAssessment: List<AssessmentRecord>
    ): MergedHistory {
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -DEMO_DAYS)
        val startDate = calendar.time
        
        val mergedPain = mutableListOf<PainRecord>()
        val mergedSleep = mutableListOf<SleepRecord>()
        val mergedWellness = mutableListOf<WellnessRecord>()
        val mergedAssessment = mutableListOf<AssessmentRecord>()

        // Generate smooth curves
        var currentPain = 7.5
        var currentStress = 8.0
        var currentSleep = 5.5

        for (i in 0..DEMO_DAYS) {
            val dateStr = dateFormat.format(calendar.time)
            
            // --- PAIN ---
            val realP = realPain.find { it.date == dateStr }
            if (realP != null) {
                mergedPain.add(realP)
            } else {
                // Demo logic: gradual decrease with slight noise
                currentPain = maxOf(2.0, currentPain - Random.nextDouble(0.0, 0.3) + Random.nextDouble(0.0, 0.1))
                currentStress = maxOf(3.0, currentStress - Random.nextDouble(0.0, 0.2))
                mergedPain.add(
                    PainRecord(
                        userId = "0",
                        date = dateStr,
                        painLevel = currentPain.toInt(),
                        stressLevel = currentStress.toInt(),
                        location = "Jaw",
                        type = "Dull"
                    )
                )
            }

            // --- SLEEP ---
            val realS = realSleep.find { it.date == dateStr }
            if (realS != null) {
                mergedSleep.add(realS)
            } else {
                currentSleep = minOf(8.0, currentSleep + Random.nextDouble(0.0, 0.15) - Random.nextDouble(0.0, 0.05))
                mergedSleep.add(
                    SleepRecord(
                        userId = "0",
                        date = dateStr,
                        sleepHours = currentSleep.toFloat(),
                        sleepQuality = if (currentSleep > 7.0) "Good" else if (currentSleep > 6.0) "Fair" else "Poor",
                        jawClenching = false,
                        morningStiffness = "Low",
                        wakeupFeeling = "Rested",
                        notes = "Demo generated"
                    )
                )
            }

            // --- WELLNESS ---
            val realW = realWellness.find { it.date == dateStr }
            if (realW != null) {
                mergedWellness.add(realW)
            } else {
                // Wellness is inverse of pain + stress mostly
                val score = 100 - (currentPain * 5) - (currentStress * 3) + (currentSleep * 2)
                mergedWellness.add(
                    WellnessRecord(
                        userId = "0",
                        date = dateStr,
                        sleepQuality = if (currentSleep > 7.0) "Good" else "Poor",
                        jawStiffness = "Medium",
                        teethGrinding = false,
                        mood = if (currentStress > 6.0) "Stressed" else "Calm",
                        waterIntake = 4,
                        energyLevel = minOf(10, maxOf(1, (10 - currentStress + currentSleep - currentPain).toInt())),
                        notes = "Demo generated"
                    )
                )
            }

            // --- ASSESSMENT (Exercise) ---
            val realA = realAssessment.find { it.date == dateStr }
            if (realA != null) {
                mergedAssessment.add(realA)
            } else {
                // 5 out of 7 days exercise consistency (skip e.g. weekends)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY) {
                    mergedAssessment.add(
                        AssessmentRecord(
                            userId = "0",
                            date = dateStr,
                            q1TeethGrinding = false,
                            q2JawClenching = false,
                            q3ChewGum = false,
                            q4BiteNails = false,
                            q5JawClicking = false,
                            q6DifficultyChewing = false,
                            q7MorningStiffness = false,
                            q8FrequentHeadaches = false,
                            q9SleepLessThan6Hours = false,
                            q10HighStress = currentStress > 6.0,
                            q11PoorPosture = false,
                            q12OneSideChewing = false,
                            sleepDuration = currentSleep.toFloat(),
                            waterIntake = 2.0f,
                            stressFrequency = "Sometimes",
                            jawPainFrequency = "Rarely",
                            exerciseConsistency = "Often",
                            smartAnalysis = "Demo generated analysis."
                        )
                    )
                }
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return MergedHistory(
            painHistory = mergedPain,
            sleepHistory = mergedSleep,
            wellnessHistory = mergedWellness,
            assessmentHistory = mergedAssessment
        )
    }
}
