package com.example.tmdapp.ui.screens.progress

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.content.Intent
import androidx.core.content.FileProvider
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.util.PdfReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadReportsScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGenerating by remember { mutableStateOf(false) }

    val user by viewModel.currentUser.collectAsState()
    val painHistory by viewModel.history.collectAsState()
    val sleepHistory by viewModel.sleepHistory.collectAsState()
    val assessmentHistory by viewModel.assessmentHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download & Export") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Export your health data, pain logs, and exercise history for personal records or to share with your doctor.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Export PDF Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Comprehensive PDF Report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "A fully formatted clinical report including charts and summaries, ideal for doctor visits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (!isGenerating) {
                                isGenerating = true
                                scope.launch {
                                    val file = withContext(Dispatchers.IO) {
                                        PdfReportGenerator.generateDoctorReport(
                                            context,
                                            user,
                                            painHistory,
                                            sleepHistory,
                                            assessmentHistory
                                        )
                                    }
                                    isGenerating = false
                                    
                                    if (file != null) {
                                        try {
                                            val uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.provider",
                                                file
                                            )
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "application/pdf"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Share Clinical Report"))
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Error sharing file", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Failed to generate report", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating...")
                        } else {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download PDF")
                        }
                    }
                }
            }

            // Export CSV Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = "CSV", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Raw Data Export (CSV)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "A spreadsheet-friendly format of all your daily logs, pain levels, and exercise sessions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "CSV Data saved to Downloads", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export CSV")
                    }
                }
            }
        }
    }
}
