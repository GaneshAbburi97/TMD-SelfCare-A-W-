package com.example.tmdapp.ui.screens.progress

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.data.model.User
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.util.DemoDataGenerator
import com.example.tmdapp.util.PdfGenerator
import com.example.tmdapp.util.UnitConverter
import com.github.mikephil.charting.data.Entry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReportScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val rawPain by viewModel.history.collectAsState()
    val rawSleep by viewModel.sleepHistory.collectAsState()
    val rawWellness by viewModel.wellnessHistory.collectAsState()
    val rawAssessment by viewModel.assessmentHistory.collectAsState()

    val mergedData = remember(rawPain, rawSleep, rawWellness, rawAssessment) {
        DemoDataGenerator.mergeWithRealData(rawPain, rawSleep, rawWellness, rawAssessment)
    }

    val painRecords = mergedData.painHistory
    val sleepRecords = mergedData.sleepHistory
    val wellnessRecords = mergedData.wellnessHistory
    val assessmentRecords = mergedData.assessmentHistory

    // Calculations
    val avgPain = painRecords.map { it.painLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val avgStress = painRecords.map { it.stressLevel }.average().takeIf { !it.isNaN() } ?: 0.0
    val avgSleep = sleepRecords.map { it.sleepHours }.average().takeIf { !it.isNaN() } ?: 0.0
    val avgWellness = wellnessRecords.map { it.energyLevel * 10 }.average().takeIf { !it.isNaN() } ?: 0.0
    val avgWater = wellnessRecords.map { it.waterIntake }.average().takeIf { !it.isNaN() }?.toInt() ?: 0
    val recoveryScore = minOf(100.0, maxOf(0.0, avgWellness)).toInt()

    val unitSystem by viewModel.settingsManager.unitSystem.collectAsState()
    val isMetric = unitSystem == "Metric"

    val stressStatus = if (avgStress < 4) "Low" else if (avgStress < 7) "Moderate" else "High"
    val sleepStatus = if (avgSleep > 7.0) "Good" else "Improving"

    var previousReports by remember { mutableStateOf(getPreviousReports(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val uri = PdfGenerator.generateHealthcareReport(context, null, painRecords, sleepRecords, wellnessRecords, assessmentRecords)
                        if (uri != null) {
                            sharePdf(context, uri)
                            previousReports = getPreviousReports(context)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF0EA5E9))
                    }
                    IconButton(onClick = {
                        val uri = PdfGenerator.generateHealthcareReport(context, null, painRecords, sleepRecords, wellnessRecords, assessmentRecords)
                        if (uri != null) {
                            viewPdf(context, uri)
                            previousReports = getPreviousReports(context)
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = Color(0xFF0EA5E9))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Track your recovery and wellness insights", fontSize = 14.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(24.dp))

            // TOP SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Overall Recovery Score", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    Text("$recoveryScore%", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Stress Status", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text(stressStatus, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("Sleep Quality", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text(sleepStatus, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("1. PAIN & STRESS REPORT")
            ReportCard {
                ReportRow("Daily Pain Average", String.format("%.1f / 10", avgPain))
                ReportRow("Stress Level Average", String.format("%.1f / 10", avgStress))
                val painTrend = if (painRecords.takeLast(7).map { it.painLevel }.average() < avgPain) "Decreasing 📉" else "Stable"
                ReportRow("Weekly Trend", painTrend)
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("2. SLEEP ANALYTICS REPORT")
            ReportCard {
                ReportRow("Average Sleep Duration", String.format("%.1f hrs", avgSleep))
                val goodSleeps = sleepRecords.count { it.sleepQuality == "Good" }
                ReportRow("Quality Consistency", "$goodSleeps good nights")
                ReportRow("Jaw Clenching Instances", sleepRecords.count { it.jawClenching }.toString())
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("3. EXERCISE REPORT")
            ReportCard {
                val completed = assessmentRecords.size
                ReportRow("Completed Exercises", "$completed sessions")
                ReportRow("Therapy Adherence", if (completed > 15) "Excellent" else "Moderate")
                ReportRow("Recovery Contribution", "High")
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("4. WELLNESS & LIFESTYLE REPORT")
            ReportCard {
                ReportRow("Water Intake (Avg)", UnitConverter.formatWater(avgWater, isMetric))
                val highStressDays = assessmentRecords.count { it.q10HighStress }
                ReportRow("High Stress Days", "$highStressDays days")
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("5. FINAL HEALTH SUMMARY")
            ReportCard {
                val summary = if (recoveryScore > 75) {
                    "Pain intensity has gradually reduced over the past month. Exercise consistency and improved sleep patterns are contributing positively toward recovery."
                } else {
                    "You are making steady progress. Keep up with your daily exercises and prioritize sleep to see further improvements in your recovery score."
                }
                Text(summary, color = Color(0xFF475569), fontSize = 14.sp, lineHeight = 20.sp)
            }

            if (previousReports.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                SectionTitle("PREVIOUS REPORTS")
                previousReports.forEach { file ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        onClick = {
                            val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            viewPdf(context, uri)
                        }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF0EA5E9))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(file.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(file.lastModified()))
                                Text(date, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ReportCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF64748B), fontSize = 14.sp)
        Text(value, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

fun getPreviousReports(context: Context): List<File> {
    val dir = File(context.cacheDir, "reports")
    if (!dir.exists()) return emptyList()
    return dir.listFiles()?.filter { it.extension == "pdf" }?.sortedByDescending { it.lastModified() } ?: emptyList()
}

fun sharePdf(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Health Report"))
}

fun viewPdf(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "View Health Report"))
}
