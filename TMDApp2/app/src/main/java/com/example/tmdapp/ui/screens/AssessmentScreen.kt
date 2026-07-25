package com.example.tmdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.util.UnitConverter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    // ... (previous state variables)
    // Boolean Questions State
    var q1 by remember { mutableStateOf(false) }
    var q2 by remember { mutableStateOf(false) }
    var q3 by remember { mutableStateOf(false) }
    var q4 by remember { mutableStateOf(false) }
    var q5 by remember { mutableStateOf(false) }
    var q6 by remember { mutableStateOf(false) }
    var q7 by remember { mutableStateOf(false) }
    var q8 by remember { mutableStateOf(false) }
    var q9 by remember { mutableStateOf(false) }
    var q10 by remember { mutableStateOf(false) }
    var q11 by remember { mutableStateOf(false) }
    var q12 by remember { mutableStateOf(false) }

    // Additional Inputs State
    var sleepDuration by remember { mutableFloatStateOf(7f) }
    var waterIntake by remember { mutableFloatStateOf(2f) }
    var stressFrequency by remember { mutableStateOf("Sometimes") }
    var jawPainFrequency by remember { mutableStateOf("Rarely") }
    var exerciseConsistency by remember { mutableStateOf("Occasionally") }

    val unitSystem by viewModel.settingsManager.unitSystem.collectAsState()
    val isMetric = unitSystem == "Metric"

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val frequencyOptions = listOf("Never", "Rarely", "Sometimes", "Often", "Always")
    val exerciseOptions = listOf("Never", "Occasionally", "Few times a week", "Daily")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TMD Health Assessment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.saveAssessment(
                        q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12,
                        sleepDuration, waterIntake, stressFrequency, jawPainFrequency, exerciseConsistency
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Assessment saved successfully!")
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Help us understand your habits and symptoms for better recommendations.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // HABIT QUESTIONS
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Habits & Symptoms", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    
                    BooleanQuestion("Do you grind your teeth during sleep?", q1) { q1 = it }
                    BooleanQuestion("Do you clench your jaw frequently?", q2) { q2 = it }
                    BooleanQuestion("Do you chew gum regularly?", q3) { q3 = it }
                    BooleanQuestion("Do you bite nails or objects?", q4) { q4 = it }
                    BooleanQuestion("Do you experience jaw clicking sounds?", q5) { q5 = it }
                    BooleanQuestion("Do you have difficulty chewing?", q6) { q6 = it }
                    BooleanQuestion("Do you feel jaw stiffness in the morning?", q7) { q7 = it }
                    BooleanQuestion("Do you experience headaches frequently?", q8) { q8 = it }
                    BooleanQuestion("Do you sleep less than 6 hours daily?", q9) { q9 = it }
                    BooleanQuestion("Do you experience high stress regularly?", q10) { q10 = it }
                    BooleanQuestion("Do you maintain poor neck/posture habits?", q11) { q11 = it }
                    BooleanQuestion("Do you use one side of the jaw more while chewing?", q12) { q12 = it }
                }
            }

            // LIFESTYLE INPUTS
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lifestyle Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))

                    Text("Sleep Duration: ${String.format("%.1f", sleepDuration)} hrs", fontWeight = FontWeight.Medium)
                    Slider(
                        value = sleepDuration,
                        onValueChange = { sleepDuration = it },
                        valueRange = 2f..14f,
                        steps = 23,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Water Intake: ${UnitConverter.formatWaterFromLiters(waterIntake, isMetric)}", fontWeight = FontWeight.Medium)
                    Slider(
                        value = waterIntake,
                        onValueChange = { waterIntake = it },
                        valueRange = 0f..5f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    DropdownInput("Stress Frequency", stressFrequency, frequencyOptions) { stressFrequency = it }
                    Spacer(Modifier.height(16.dp))
                    DropdownInput("Jaw Pain Frequency", jawPainFrequency, frequencyOptions) { jawPainFrequency = it }
                    Spacer(Modifier.height(16.dp))
                    DropdownInput("Exercise Consistency", exerciseConsistency, exerciseOptions) { exerciseConsistency = it }
                }
            }
        }
    }
}

@Composable
fun BooleanQuestion(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(label: String, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
