package com.example.tmdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.data.model.PainRecord
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogsScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Pain Logs", fontWeight = FontWeight.Bold) },
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
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No logs found yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(history) { record ->
                    DailyLogReceipt(record)
                }
            }
        }
    }
}

@Composable
fun DailyLogReceipt(record: PainRecord) {
    val timeStr = remember(record.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = record.date,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = timeStr,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PAIN LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
                    Text(
                        text = "${record.painLevel}/10",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.painLevel >= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("STRESS LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
                    Text(
                        text = "${record.stressLevel}/10",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.stressLevel >= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Area: ${record.location}",
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Rec: ${getRecommendation(record.painLevel, record.stressLevel)}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun getRecommendation(pain: Int, stress: Int): String {
    return when {
        pain >= 7 && stress >= 7 -> "High stress & severe pain — breathing and gentle relaxation recommended."
        stress >= 7 -> "Stress is elevated — try breathing & relaxation exercises first."
        pain >= 7 -> "Severe pain — only gentle exercises recommended. Consult a doctor."
        pain in 4..6 -> "Gentle exercises recommended to avoid strain."
        else -> "Continue with mobility and strengthening exercises."
    }
}
