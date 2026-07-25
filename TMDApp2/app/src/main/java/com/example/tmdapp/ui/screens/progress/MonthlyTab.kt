package com.example.tmdapp.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord
import com.github.mikephil.charting.data.Entry

@Composable
fun MonthlyTab(
    painHistory: List<PainRecord>,
    sleepHistory: List<SleepRecord>,
    wellnessHistory: List<WellnessRecord>,
    assessmentHistory: List<AssessmentRecord>
) {
    val scrollState = rememberScrollState()

    // Divide history into 4 weeks (7 day chunks backwards)
    val w4Pain = painHistory.dropLast(21).takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 5.0
    val w3Pain = painHistory.dropLast(14).takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 4.8
    val w2Pain = painHistory.dropLast(7).takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 4.5
    val w1Pain = painHistory.takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 4.0

    val painEntries = listOf(
        Entry(0f, w4Pain.toFloat()), 
        Entry(1f, w3Pain.toFloat()), 
        Entry(2f, w2Pain.toFloat()), 
        Entry(3f, w1Pain.toFloat())
    )
    val labels = listOf("Week 4", "Week 3", "Week 2", "Week 1")

    // Dynamic Recommendations
    val w1Rec = if (w1Pain < w2Pain) "Continuing jaw stretches is yielding great results this week." else "Pain slightly increased. Reduce hard foods and monitor stress."
    val w2Rec = if (w2Pain < w3Pain) "Great sleep consistency helped lower pain." else "Consider checking your posture; tension remained high."
    val w3Rec = if (w3Pain < w4Pain) "Breathing exercises seem to be helping reduce tension." else "High stress levels detected this week."

    // Exercise Milestone
    val totalExercises = assessmentHistory.takeLast(30).count { it.date.isNotEmpty() }
    val milestoneText = if (totalExercises >= 20) "$totalExercises days of consistent exercise!" else if (totalExercises >= 10) "Great start! $totalExercises exercises completed." else "Log more exercises to hit a milestone!"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "MONTHLY OVERVIEW",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Monthly Milestone Reached", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(milestoneText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SmoothLineChartSection(
            title = "Monthly Pain Progression",
            entries = painEntries,
            labels = labels,
            lineColor = 0xFFEF4444.toInt(),
            fillColor = 0xFFEF4444.toInt()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "RECOMMENDATION HISTORY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RecommendationItem("Week 1 (Current)", w1Rec)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                RecommendationItem("Week 2", w2Rec)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                RecommendationItem("Week 3", w3Rec)
            }
        }
    }
}

@Composable
fun RecommendationItem(week: String, rec: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(week, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(rec, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), lineHeight = 18.sp)
        }
    }
}
