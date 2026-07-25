package com.example.tmdapp.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.util.UnitConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSettingsScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val unitSystem by viewModel.settingsManager.unitSystem.collectAsState()
    val options = listOf("Metric", "Imperial")

    val isMetric = unitSystem == "Metric"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Units & Measurement Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Customize how health data is displayed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            options.forEach { option ->
                val isSelected = unitSystem == option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.settingsManager.setUnitSystem(option) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (option == "Metric") "Metric System" else "Imperial System",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (option == "Metric") "cm, kg, liters, Celsius" else "feet/inches, lbs, ounces, Fahrenheit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Preview Card
            Text(
                text = "Live Conversion Preview",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PreviewRow("Height (170 cm)", UnitConverter.formatHeight(170f, isMetric))
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    PreviewRow("Weight (70 kg)", UnitConverter.formatWeight(70f, isMetric))
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    PreviewRow("Water (4 glasses)", UnitConverter.formatWater(4, isMetric))
                }
            }
        }
    }
}

@Composable
fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
