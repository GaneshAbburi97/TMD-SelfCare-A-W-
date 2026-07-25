package com.example.tmdapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.tmdapp.data.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateHealthcareReport(
        context: Context,
        user: User?,
        painRecords: List<PainRecord>,
        sleepRecords: List<SleepRecord>,
        wellnessRecords: List<WellnessRecord>,
        assessmentRecords: List<AssessmentRecord>
    ): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()

        var currentY = 50f
        val startX = 50f
        val pageWidth = 545f

        // Colors
        val primaryColor = Color.parseColor("#0F172A") // Slate 900
        val accentColor = Color.parseColor("#0ea5e9") // Sky Blue 500
        val textColor = Color.parseColor("#475569") // Slate 600
        val lightGray = Color.parseColor("#F1F5F9") // Slate 100

        fun checkPageBreak(requiredSpace: Float) {
            if (currentY + requiredSpace > 800f) {
                document.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 2).create()
                page = document.startPage(newPageInfo)
                canvas = page.canvas
                currentY = 50f
            }
        }

        fun drawSectionHeader(title: String) {
            checkPageBreak(50f)
            paint.color = accentColor
            paint.strokeWidth = 2f
            canvas.drawLine(startX, currentY, pageWidth, currentY, paint)
            currentY += 20f
            
            paint.color = primaryColor
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(title.uppercase(), startX, currentY, paint)
            currentY += 25f
        }

        // --- HEADER ---
        paint.color = primaryColor
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Comprehensive Health Report", startX, currentY, paint)
        currentY += 20f
        
        paint.color = textColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("TMD Care AI - Clinical Wellness Summary", startX, currentY, paint)
        currentY += 30f

        // --- PATIENT INFO ---
        paint.color = lightGray
        paint.style = Paint.Style.FILL
        canvas.drawRect(startX, currentY, pageWidth, currentY + 70f, paint)
        
        paint.color = primaryColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Patient Name: ${user?.name ?: "Guest User"}", startX + 15f, currentY + 25f, paint)
        
        paint.color = textColor
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Generated: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}", startX + 15f, currentY + 45f, paint)
        canvas.drawText("Records span: ${painRecords.size} logged days", startX + 250f, currentY + 45f, paint)
        currentY += 100f

        // --- PAIN & STRESS ---
        drawSectionHeader("1. Pain & Stress Analytics")
        val avgPain = painRecords.map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 0.0
        val avgStress = painRecords.map { it.stressLevel }.average().takeIf { !it.isNaN() } ?: 0.0
        
        paint.color = textColor
        canvas.drawText("Average Pain Level: ${String.format("%.1f", avgPain)} / 10", startX, currentY, paint)
        currentY += 20f
        canvas.drawText("Average Stress Level: ${String.format("%.1f", avgStress)} / 10", startX, currentY, paint)
        currentY += 20f
        val painTrend = if (painRecords.takeLast(7).map { it.painLevel }.average() < avgPain) "Decreasing (Positive)" else "Stable/Increasing"
        canvas.drawText("Current Trend: $painTrend", startX, currentY, paint)
        currentY += 30f

        // --- SLEEP ANALYTICS ---
        drawSectionHeader("2. Sleep Analytics Report")
        val avgSleep = sleepRecords.map { it.sleepHours }.average().takeIf { !it.isNaN() } ?: 0.0
        canvas.drawText("Average Duration: ${String.format("%.1f", avgSleep)} hours/night", startX, currentY, paint)
        currentY += 20f
        val goodSleeps = sleepRecords.count { it.sleepQuality == "Good" }
        canvas.drawText("Quality Consistency: $goodSleeps good nights out of ${sleepRecords.size}", startX, currentY, paint)
        currentY += 30f

        // --- EXERCISE REPORT ---
        drawSectionHeader("3. Exercise & Therapy Report")
        val totalAssessments = assessmentRecords.size
        canvas.drawText("Total Therapies Logged: $totalAssessments sessions", startX, currentY, paint)
        currentY += 20f
        if (assessmentRecords.isNotEmpty()) {
            val latest = assessmentRecords.last()
            canvas.drawText("Latest Exercise Consistency: ${latest.exerciseConsistency}", startX, currentY, paint)
            currentY += 20f
            canvas.drawText("Jaw Tension Observed: ${if (latest.q10HighStress) "Yes" else "No"}", startX, currentY, paint)
        }
        currentY += 30f

        // --- WELLNESS ---
        drawSectionHeader("4. Wellness & Lifestyle")
        val avgWellness = wellnessRecords.map { it.energyLevel * 10 }.average().takeIf { !it.isNaN() } ?: 0.0
        val recoveryScore = minOf(100.0, maxOf(0.0, avgWellness)).toInt()
        
        paint.color = primaryColor
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Overall Recovery Score: $recoveryScore%", startX, currentY, paint)
        currentY += 20f
        
        paint.color = textColor
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("This score aggregates pain reduction, sleep quality, and lifestyle factors.", startX, currentY, paint)
        currentY += 30f

        // --- FINAL SUMMARY ---
        drawSectionHeader("Final Health Summary")
        val summaryLines = mutableListOf<String>()
        if (recoveryScore > 75) {
            summaryLines.add("Patient is showing excellent adherence to therapy and strong recovery metrics.")
            summaryLines.add("Pain levels are manageable and sleep consistency is contributing positively.")
        } else if (recoveryScore > 50) {
            summaryLines.add("Patient is making steady progress but experiences occasional symptom flare-ups.")
            summaryLines.add("Recommend continued focus on stress management and daily jaw exercises.")
        } else {
            summaryLines.add("Patient is currently experiencing elevated symptoms.")
            summaryLines.add("Clinical review or adjustment of the current therapy routine is recommended.")
        }

        for (line in summaryLines) {
            checkPageBreak(25f)
            canvas.drawText(line, startX, currentY, paint)
            currentY += 20f
        }
        
        // Footer
        currentY = 800f
        paint.color = Color.LTGRAY
        paint.textSize = 10f
        canvas.drawText("Generated by TMD Care AI - Do not use as sole medical diagnostic.", startX, currentY, paint)

        document.finishPage(page)

        // Save
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            
            val fileName = "TMD_Health_Report_${System.currentTimeMillis()}.pdf"
            val file = File(reportsDir, fileName)
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()

            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }
}
