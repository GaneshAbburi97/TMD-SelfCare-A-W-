package com.example.tmdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.data.model.WellnessRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessHistoryScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val wellnessHistory by viewModel.wellnessHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wellness History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (wellnessHistory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No wellness records found.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(wellnessHistory) { record ->
                    WellnessRecordCard(record)
                }
            }
        }
    }
}

@Composable
fun WellnessRecordCard(record: WellnessRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(record.date, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = record.mood,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WellnessFactorItem(icon = Icons.Default.Bedtime, label = "Sleep", value = record.sleepQuality)
                WellnessFactorItem(icon = Icons.Default.Face, label = "Jaw", value = record.jawStiffness)
                WellnessFactorItem(icon = Icons.Default.Bolt, label = "Energy", value = "${record.energyLevel}/10")
            }

            if (record.notes.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = record.notes,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun WellnessFactorItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
        Text(label, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
