package com.example.tmdapp.util

import kotlin.math.roundToInt

object UnitConverter {

    /**
     * Converts raw cm into a display string based on the unit system.
     * Metric: "170 cm"
     * Imperial: "5'7\" ft"
     */
    fun formatHeight(cm: Float?, isMetric: Boolean): String {
        if (cm == null) return "--"
        return if (isMetric) {
            "${cm.roundToInt()} cm"
        } else {
            val totalInches = cm / 2.54f
            val feet = (totalInches / 12).toInt()
            val inches = (totalInches % 12).roundToInt()
            "$feet'$inches\" ft"
        }
    }

    /**
     * Converts raw kg into a display string based on the unit system.
     * Metric: "70 kg"
     * Imperial: "154 lbs"
     */
    fun formatWeight(kg: Float?, isMetric: Boolean): String {
        if (kg == null) return "--"
        return if (isMetric) {
            "${kg.roundToInt()} kg"
        } else {
            val lbs = kg * 2.20462f
            "${lbs.roundToInt()} lbs"
        }
    }

    /**
     * Formats water intake (stored internally as "glasses", assuming 1 glass = 250ml)
     * Metric: "X L" or "X ml"
     * Imperial: "X oz"
     */
    fun formatWater(glasses: Int, isMetric: Boolean): String {
        val ml = glasses * 250
        return formatWaterFromMl(ml, isMetric)
    }

    fun formatWaterFromLiters(liters: Float, isMetric: Boolean): String {
        val ml = (liters * 1000).toInt()
        return formatWaterFromMl(ml, isMetric)
    }

    private fun formatWaterFromMl(ml: Int, isMetric: Boolean): String {
        return if (isMetric) {
            if (ml >= 1000) {
                String.format("%.1f L", ml / 1000f)
            } else {
                "$ml ml"
            }
        } else {
            val oz = ml / 29.5735f
            "${oz.roundToInt()} oz"
        }
    }

    fun formatTemperature(celsius: Float, isMetric: Boolean): String {
        return if (isMetric) {
            "${celsius.roundToInt()}°C"
        } else {
            val f = (celsius * 9 / 5) + 32
            "${f.roundToInt()}°F"
        }
    }
}
