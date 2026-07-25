package com.example.tmdapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.tmdapp.data.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    fun generateDoctorReport(
        context: Context,
        user: User?,
        painRecords: List<PainRecord>,
        sleepRecords: List<SleepRecord>,
        assessmentRecords: List<AssessmentRecord>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var currentY = 50f
        val startX = 50f

        // Title
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText("Clinical TMD Health Report", startX, currentY, paint)
        currentY += 40f

        // Patient Info
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        canvas.drawText("Generated: ${dateFormat.format(Date())}", startX, currentY, paint)
        currentY += 20f
        canvas.drawText("Patient Name: ${user?.name ?: "Unknown"}", startX, currentY, paint)
        currentY += 20f
        canvas.drawText("Email: ${user?.email ?: "Unknown"}", startX, currentY, paint)
        currentY += 20f
        if (user?.heightCm != null && user.weightKg != null) {
            canvas.drawText("Height: ${user.heightCm} cm | Weight: ${user.weightKg} kg", startX, currentY, paint)
            currentY += 20f
        }
        currentY += 20f

        // Separator
        paint.strokeWidth = 2f
        canvas.drawLine(startX, currentY, 545f, currentY, paint)
        currentY += 30f

        // Pain Summary
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Pain & Symptom Summary (Last 30 Days)", startX, currentY, paint)
        currentY += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        if (painRecords.isEmpty()) {
            canvas.drawText("No pain records available.", startX, currentY, paint)
            currentY += 20f
        } else {
            val avgPain = painRecords.map { it.painLevel }.average()
            val avgStress = painRecords.map { it.stressLevel }.average()
            val mostCommonLocation = painRecords.groupBy { it.location }
                .maxByOrNull { it.value.size }?.key ?: "N/A"

            canvas.drawText(String.format("Average Pain Level: %.1f / 10", avgPain), startX, currentY, paint)
            currentY += 20f
            canvas.drawText(String.format("Average Stress Level: %.1f / 10", avgStress), startX, currentY, paint)
            currentY += 20f
            canvas.drawText("Most Common Pain Location: $mostCommonLocation", startX, currentY, paint)
            currentY += 20f
            canvas.drawText("Total Recorded Incidents: ${painRecords.size}", startX, currentY, paint)
            currentY += 30f
        }

        // Sleep Summary
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Sleep Quality", startX, currentY, paint)
        currentY += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        if (sleepRecords.isEmpty()) {
            canvas.drawText("No sleep records available.", startX, currentY, paint)
            currentY += 20f
        } else {
            val avgSleep = sleepRecords.map { it.sleepHours }.average()
            val clenchingIncidents = sleepRecords.count { it.jawClenching }
            
            canvas.drawText(String.format("Average Sleep Duration: %.1f hrs", avgSleep), startX, currentY, paint)
            currentY += 20f
            canvas.drawText("Reported Jaw Clenching During Sleep: $clenchingIncidents nights", startX, currentY, paint)
            currentY += 30f
        }
        
        // Latest Assessment
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Latest Clinical Assessment", startX, currentY, paint)
        currentY += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        val latestAssessment = assessmentRecords.maxByOrNull { it.date }
        if (latestAssessment == null) {
            canvas.drawText("No assessments completed.", startX, currentY, paint)
        } else {
            canvas.drawText("Date: ${latestAssessment.date}", startX, currentY, paint)
            currentY += 20f
            canvas.drawText("Reported Habits:", startX, currentY, paint)
            currentY += 20f
            if (latestAssessment.q1TeethGrinding) { canvas.drawText("- Teeth Grinding", startX + 20f, currentY, paint); currentY += 20f }
            if (latestAssessment.q2JawClenching) { canvas.drawText("- Jaw Clenching", startX + 20f, currentY, paint); currentY += 20f }
            if (latestAssessment.q5JawClicking) { canvas.drawText("- Jaw Clicking/Popping", startX + 20f, currentY, paint); currentY += 20f }
            if (latestAssessment.q7MorningStiffness) { canvas.drawText("- Morning Jaw Stiffness", startX + 20f, currentY, paint); currentY += 20f }
            
            currentY += 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            
            // Draw analysis text wrapped
            val analysisLines = wrapText(latestAssessment.smartAnalysis, paint, 495f)
            for (line in analysisLines) {
                canvas.drawText(line, startX, currentY, paint)
                currentY += 20f
            }
        }

        pdfDocument.finishPage(page)

        // Save PDF to Cache
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        
        val file = File(reportsDir, "TMD_Clinical_Report_${System.currentTimeMillis()}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
    
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) > maxWidth) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }
}
