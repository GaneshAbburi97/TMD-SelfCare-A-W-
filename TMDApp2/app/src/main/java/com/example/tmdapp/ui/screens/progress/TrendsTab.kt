package com.example.tmdapp.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrendsTab(
    painHistory: List<PainRecord>,
    sleepHistory: List<SleepRecord>,
    wellnessHistory: List<WellnessRecord>,
    assessmentHistory: List<AssessmentRecord>
) {
    val scrollState = rememberScrollState()

    // Aggregate Pain/Stress data per day
    val painData = remember(painHistory) {
        painHistory.groupBy { it.date }
            .map { it.value.first() }
            .sortedBy { record ->
                try {
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(record.date)
                } catch (e: Exception) {
                    Date(0)
                }
            }
    }

    val painEntries = painData.mapIndexed { index, record ->
        Entry(index.toFloat(), record.painLevel.toFloat())
    }
    val stressEntries = painData.mapIndexed { index, record ->
        Entry(index.toFloat(), record.stressLevel.toFloat())
    }
    val dateLabels = painData.map { it.date.substringBeforeLast("-") }

    // Mock or real sleep entries
    val sleepData = remember(sleepHistory) {
        if (sleepHistory.isNotEmpty()) {
            sleepHistory.takeLast(7).mapIndexed { index, record ->
                Entry(index.toFloat(), record.sleepHours)
            }
        } else {
            // Mock data for UI demonstration
            listOf(
                Entry(0f, 6.5f), Entry(1f, 7.0f), Entry(2f, 5.5f), 
                Entry(3f, 8.0f), Entry(4f, 7.5f), Entry(5f, 6.0f), Entry(6f, 7.2f)
            )
        }
    }
    val sleepLabels = if (sleepHistory.isNotEmpty()) {
        sleepHistory.takeLast(7).map { "Day" } // Should map to dates if date field exists in SleepRecord
    } else {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }

    // Mock wellness score trends (0-100)
    val wellnessEntries = listOf(
        Entry(0f, 60f), Entry(1f, 65f), Entry(2f, 62f), 
        Entry(3f, 70f), Entry(4f, 75f), Entry(5f, 78f), Entry(6f, 80f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SmoothLineChartSection(
            title = "Pain Level Over Time",
            entries = painEntries,
            labels = dateLabels,
            lineColor = 0xFFEF4444.toInt(), // Red for pain
            fillColor = 0xFFEF4444.toInt()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SmoothLineChartSection(
            title = "Stress Level Over Time",
            entries = stressEntries,
            labels = dateLabels,
            lineColor = 0xFF3B82F6.toInt(), // Blue for stress
            fillColor = 0xFF3B82F6.toInt()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SmoothLineChartSection(
            title = "Sleep Duration (Hours)",
            entries = sleepData,
            labels = sleepLabels,
            lineColor = 0xFF8B5CF6.toInt(), // Purple for sleep
            fillColor = 0xFF8B5CF6.toInt()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SmoothLineChartSection(
            title = "Overall Wellness Score",
            entries = wellnessEntries,
            labels = sleepLabels,
            lineColor = 0xFF10B981.toInt(), // Green for wellness
            fillColor = 0xFF10B981.toInt()
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
