package com.example.tmdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.TmdViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackingScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var sleepHours by remember { mutableFloatStateOf(8f) }
    var sleepQuality by remember { mutableStateOf("Average") }
    var jawClenching by remember { mutableStateOf(false) }
    var morningStiffness by remember { mutableStateOf("Medium") }
    var wakeupFeeling by remember { mutableStateOf("Tired") }
    var notes by remember { mutableStateOf("") }
    
    var showInsight by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Better sleep supports jaw recovery and relaxation.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // 1. SLEEP HOURS
            SleepSection(title = "Sleep Duration", icon = Icons.Default.Schedule) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0h", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                        Text("${sleepHours.toInt()} Hours", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                        Text("12h", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                    Slider(
                        value = sleepHours,
                        onValueChange = { sleepHours = it },
                        valueRange = 0f..12f,
                        steps = 11
                    )
                }
            }

            // 2. SLEEP QUALITY
            SleepSection(title = "Sleep Quality", icon = Icons.Default.Bedtime) {
                WellnessChoiceRow(
                    options = listOf("Poor", "Average", "Good"),
                    selected = sleepQuality,
                    onSelected = { sleepQuality = it }
                )
            }

            // 3. NIGHT JAW CLENCHING
            SleepSection(title = "Night Jaw Clenching", icon = Icons.Default.Shield) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Did you experience jaw clenching or teeth grinding?", modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Switch(checked = jawClenching, onCheckedChange = { jawClenching = it })
                }
            }

            // 4. MORNING JAW STIFFNESS
            SleepSection(title = "Morning Jaw Stiffness", icon = Icons.Default.Face) {
                WellnessChoiceRow(
                    options = listOf("Low", "Medium", "Severe"),
                    selected = morningStiffness,
                    onSelected = { morningStiffness = it }
                )
            }

            // 5. WAKE-UP FEELING
            SleepSection(title = "Wake-up Feeling", icon = Icons.Default.SentimentSatisfied) {
                val feelings = listOf(
                    "Refreshed" to "🌞",
                    "Tired" to "😴",
                    "Stressed" to "😰",
                    "Fatigued" to "😵‍💫"
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    feelings.forEach { (name, emoji) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (wakeupFeeling == name) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { wakeupFeeling = name }
                                .padding(8.dp)
                        ) {
                            Text(emoji, fontSize = 24.sp)
                            Text(name, fontSize = 12.sp, color = if (wakeupFeeling == name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // 6. OPTIONAL NOTES
            SleepSection(title = "Notes", icon = Icons.Default.Notes) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("How was your sleep today?", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (showInsight) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = getSleepInsight(sleepQuality, jawClenching, sleepHours),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveSleepRecord(
                        sleepHours, sleepQuality, jawClenching, morningStiffness, wakeupFeeling, notes
                    )
                    showInsight = true
                    scope.launch {
                        snackbarHostState.showSnackbar("Sleep entry saved successfully!")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Sleep Entry", fontWeight = FontWeight.SemiBold)
            }
            
            OutlinedButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Sleep History")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SleepSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

fun getSleepInsight(quality: String, clenching: Boolean, hours: Float): String {
    return when {
        clenching -> "Jaw clenching detected during sleep. Consider using a night guard."
        quality == "Poor" -> "Poor sleep may increase jaw tension. Try relaxation techniques before bed."
        hours < 6 -> "Low sleep duration can affect recovery. Aim for 7-9 hours of sleep."
        else -> "Good sleep supports muscle recovery. Keep maintaining healthy sleep habits."
    }
}
