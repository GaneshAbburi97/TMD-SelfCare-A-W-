package com.example.tmdapp.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.WellnessRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ActivityTab(
    painHistory: List<PainRecord>,
    sleepHistory: List<SleepRecord>,
    wellnessHistory: List<WellnessRecord>,
    assessmentHistory: List<AssessmentRecord>
) {
    val scrollState = rememberScrollState()

    // Date formatter
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Parse all dates to find the start date
    val allDates = remember(painHistory, sleepHistory, wellnessHistory, assessmentHistory) {
        val dates = mutableListOf<Date>()
        painHistory.forEach { try { dates.add(dateFormat.parse(it.date)!!) } catch (e: Exception) {} }
        sleepHistory.forEach { try { dates.add(dateFormat.parse(it.date)!!) } catch (e: Exception) {} }
        wellnessHistory.forEach { try { dates.add(dateFormat.parse(it.date)!!) } catch (e: Exception) {} }
        assessmentHistory.forEach { try { dates.add(dateFormat.parse(it.date)!!) } catch (e: Exception) {} }
        dates.sorted()
    }

    val startDate = allDates.firstOrNull() ?: Date()
    val today = Date()

    // Generate list of all date strings from startDate to today
    val calendarDays = remember(startDate, today) {
        val days = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.time = startDate
        // Start from the beginning of the week if we want a proper calendar view, or just exact days.
        // Let's just track exact dates from start to today to build the grid.
        val endCal = Calendar.getInstance()
        endCal.time = today
        
        while (!cal.after(endCal)) {
            days.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        days
    }

    // Helper functions for checking activity
    fun hasSeverePain(dateStr: String): Boolean = painHistory.any { it.date == dateStr && it.painLevel >= 7 }
    fun hasExercise(dateStr: String): Boolean = assessmentHistory.any { it.date == dateStr }
    fun isActive(dateStr: String): Boolean {
        return painHistory.any { it.date == dateStr } ||
               sleepHistory.any { it.date == dateStr } ||
               wellnessHistory.any { it.date == dateStr } ||
               assessmentHistory.any { it.date == dateStr }
    }

    // Calculate Summary Stats
    var totalActiveDays = 0
    var missedDays = 0
    var currentExerciseStreak = 0
    
    // Calculate streak by iterating backwards
    for (i in calendarDays.indices.reversed()) {
        val d = calendarDays[i]
        if (hasExercise(d)) {
            currentExerciseStreak++
        } else {
            break // streak broken
        }
    }

    calendarDays.forEach { d ->
        if (isActive(d)) totalActiveDays++ else missedDays++
    }

    val bestRecoveryDays = totalActiveDays // Simple proxy for now

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "ACTIVITY CALENDAR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val missedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Color(0xFF10B981), "Active")
            LegendItem(missedColor, "Missed")
            LegendItem(Color(0xFF3B82F6), "Exercise")
            LegendItem(Color(0xFFEF4444), "Pain")
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Calendar Grid (From start date to today)
                val gridHeight = if (calendarDays.size > 35) 300.dp else 240.dp
                Box(modifier = Modifier.height(gridHeight)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(calendarDays.size) { index ->
                            val dateStr = calendarDays[index]
                            
                            val dayColor = when {
                                hasSeverePain(dateStr) -> Color(0xFFEF4444) // Severe pain
                                hasExercise(dateStr) -> Color(0xFF3B82F6) // Exercise completed
                                isActive(dateStr) -> Color(0xFF10B981) // Active/Completed
                                else -> missedColor // Missed
                            }
                            
                            // Display the day of the month
                            val dayOfMonth = try {
                                val d = dateFormat.parse(dateStr)
                                val cal = Calendar.getInstance().apply { time = d!! }
                                cal.get(Calendar.DAY_OF_MONTH).toString()
                            } catch (e: Exception) { "" }

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(dayColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth,
                                    color = if (dayColor == missedColor) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                if (calendarDays.isEmpty()) {
                    Text(
                        "No activity logged yet.",
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACTIVITY SUMMARY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Active Days", totalActiveDays.toString(), Modifier.weight(1f))
            SummaryCard("Missed Days", missedDays.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Exercise Streak", "$currentExerciseStreak Days", Modifier.weight(1f))
            SummaryCard("Total Logs", "${allDates.size}", Modifier.weight(1f))
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
