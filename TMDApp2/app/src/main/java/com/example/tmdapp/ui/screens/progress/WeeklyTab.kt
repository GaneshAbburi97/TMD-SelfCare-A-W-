package com.example.tmdapp.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord

@Composable
fun WeeklyTab(
    painHistory: List<PainRecord>,
    sleepHistory: List<SleepRecord>,
    wellnessHistory: List<WellnessRecord>,
    assessmentHistory: List<AssessmentRecord>
) {
    val scrollState = rememberScrollState()

    // Calculate Weekly averages
    val last7Pain = painHistory.takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val prev7Pain = painHistory.dropLast(7).takeLast(7).map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val painDiff = last7Pain - prev7Pain

    val last7Stress = painHistory.takeLast(7).map { it.stressLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val prev7Stress = painHistory.dropLast(7).takeLast(7).map { it.stressLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val stressDiff = last7Stress - prev7Stress

    val last7Sleep = sleepHistory.takeLast(7).map { it.sleepHours.toDouble() }.average().takeIf { !it.isNaN() } ?: 0.0
    val sleepQuality = if (last7Sleep >= 7.0) "Good" else if (last7Sleep >= 6.0) "Fair" else "Poor"
    val sleepSubtitle = if (last7Sleep > 7.0) "Improving" else "Needs Focus"

    val exercisesCompleted = assessmentHistory.takeLast(7).count { it.date.isNotEmpty() }
    
    val avgWellness = wellnessHistory.takeLast(7).map { it.energyLevel * 10.0 }.average().takeIf { !it.isNaN() } ?: 0.0
    val prevWellness = wellnessHistory.dropLast(7).takeLast(7).map { it.energyLevel * 10.0 }.average().takeIf { !it.isNaN() } ?: 0.0
    val wellnessDiff = avgWellness - prevWellness
    
    val wellnessImprovement = if (wellnessDiff > 0) "+${String.format("%.1f", wellnessDiff)}%" else "${String.format("%.1f", wellnessDiff)}%"
    val recoveryScore = minOf(100, maxOf(0, avgWellness.toInt()))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "WEEKLY AVERAGES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Avg Pain",
                value = String.format("%.1f", last7Pain),
                modifier = Modifier.weight(1f),
                icon = if (painDiff <= 0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                iconTint = if (painDiff <= 0) Color(0xFF10B981) else Color(0xFFEF4444), // Green if lower/equal
                subtitle = "${if (painDiff > 0) "+" else ""}${String.format("%.1f", painDiff)} from last week"
            )
            MetricCard(
                title = "Avg Stress",
                value = String.format("%.1f", last7Stress),
                modifier = Modifier.weight(1f),
                icon = if (stressDiff <= 0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                iconTint = if (stressDiff <= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                subtitle = "${if (stressDiff > 0) "+" else ""}${String.format("%.1f", stressDiff)} from last week"
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Sleep Quality",
                value = sleepQuality,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowUpward,
                iconTint = Color(0xFF10B981),
                subtitle = "Improved"
            )
            MetricCard(
                title = "Exercises",
                value = "$exercisesCompleted / 7",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowUpward,
                iconTint = Color(0xFF10B981),
                subtitle = "Consistent"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WEEKLY SUMMARY REPORT",
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
                Text(
                    text = "Recovery Score: $recoveryScore%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val painText = if (painDiff < 0) "Your pain levels are steadily decreasing." else if (painDiff > 0) "Your pain levels have slightly increased this week." else "Your pain levels are stable."
                val exerciseText = if (exercisesCompleted >= 4) "You've been very consistent with exercises." else "Try to complete more exercises next week for better recovery."
                val stressText = if (stressDiff < 0) "Great job managing stress!" else "Try to incorporate more relaxation techniques to manage stress."

                Text(
                    text = "$painText $exerciseText $stressText",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { recoveryScore / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(0xFF10B981),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Overall Wellness Improvement: $wellnessImprovement",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    subtitle: String = ""
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
            }
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 11.sp, color = iconTint)
            }
        }
    }
}
