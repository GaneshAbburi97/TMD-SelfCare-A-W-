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
import com.example.tmdapp.util.UnitConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWellnessScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var sleepQuality by remember { mutableStateOf("Average") }
    var jawStiffness by remember { mutableStateOf("Medium") }
    var teethGrinding by remember { mutableStateOf(false) }
    var mood by remember { mutableStateOf("Neutral") }
    var waterIntake by remember { mutableIntStateOf(4) }
    var energyLevel by remember { mutableFloatStateOf(5f) }
    var notes by remember { mutableStateOf("") }
    
    val unitSystem by viewModel.settingsManager.unitSystem.collectAsState()
    val isMetric = unitSystem == "Metric"
    
    var showInsight by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Wellness Check-In", fontWeight = FontWeight.Bold) },
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
                "Track recovery and wellness factors affecting your jaw health.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // 1. SLEEP QUALITY
            WellnessSection(title = "Sleep Quality", icon = Icons.Default.Bedtime) {
                WellnessChoiceRow(
                    options = listOf("Poor", "Average", "Good"),
                    selected = sleepQuality,
                    onSelected = { sleepQuality = it }
                )
            }

            // 2. MORNING JAW STIFFNESS
            WellnessSection(title = "Morning Jaw Stiffness", icon = Icons.Default.Face) {
                WellnessChoiceRow(
                    options = listOf("Low", "Medium", "Severe"),
                    selected = jawStiffness,
                    onSelected = { jawStiffness = it }
                )
            }

            // 3. TEETH GRINDING
            WellnessSection(title = "Teeth Grinding", icon = Icons.Default.Shield) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Did you experience teeth grinding last night?", modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Switch(checked = teethGrinding, onCheckedChange = { teethGrinding = it })
                }
            }

            // 4. MOOD TODAY
            WellnessSection(title = "Mood Today", icon = Icons.Default.SentimentSatisfied) {
                val moods = listOf(
                    "Calm" to "🧘",
                    "Neutral" to "😐",
                    "Stressed" to "😰",
                    "Tired" to "😴"
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    moods.forEach { (name, emoji) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (mood == name) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { mood = name }
                                .padding(8.dp)
                        ) {
                            Text(emoji, fontSize = 24.sp)
                            Text(name, fontSize = 12.sp, color = if (mood == name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // 5. WATER INTAKE
            WellnessSection(title = "Water Intake (Glasses)", icon = Icons.Default.WaterDrop) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { if (waterIntake > 0) waterIntake-- }) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = UnitConverter.formatWater(waterIntake, isMetric),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    IconButton(onClick = { waterIntake++ }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // 6. ENERGY LEVEL
            WellnessSection(title = "Energy Level", icon = Icons.Default.Bolt) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                        Text("${energyLevel.toInt()}/10", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                        Text("10", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                    Slider(
                        value = energyLevel,
                        onValueChange = { energyLevel = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
            }

            // 7. OPTIONAL NOTES
            WellnessSection(title = "Notes", icon = Icons.Default.Notes) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("How are you feeling today?", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (showInsight) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = getWellnessInsight(sleepQuality, energyLevel, waterIntake),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveWellnessRecord(
                        sleepQuality, jawStiffness, teethGrinding, mood, waterIntake, energyLevel.toInt(), notes
                    )
                    showInsight = true
                    scope.launch {
                        snackbarHostState.showSnackbar("Wellness entry saved successfully!")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Wellness Entry", fontWeight = FontWeight.SemiBold)
            }
            
            OutlinedButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Wellness History")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WellnessChoiceRow(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = selected == option
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelected(option) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = option,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WellnessSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
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

fun getWellnessInsight(sleep: String, energy: Float, water: Int): String {
    return when {
        sleep == "Poor" -> "Poor sleep may contribute to jaw tension. Try to rest well tonight."
        energy < 4 -> "Stress and low energy can worsen TMD symptoms. Take it easy today."
        water < 5 -> "Good hydration supports muscle relaxation. Aim for 8 glasses of water."
        else -> "Great job on maintaining your wellness factors today!"
    }
}
