package com.example.tmdapp.ui.screens.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroubleshootingGuideScreen(
    onNavigateBack: () -> Unit
) {
    val guides = listOf(
        "Exercise video is not playing or loading." to "Ensure you have an active internet connection. Try clearing the app cache or restarting the application. The videos are preloaded on the background, but if storage is full, they might fail.",
        "Pain Map data is not syncing." to "Check your Privacy Settings and ensure 'Health Data Sync' is enabled. If the problem persists, log out and log back in.",
        "I cannot download my health reports." to "Ensure the app has storage permissions. Check your device's 'Downloads' folder. Try generating a simpler report date range.",
        "The AI Chat isn't responding." to "The AI requires an internet connection. If the server is busy, it may take a few seconds. Try sending the message again.",
        "How do I reset my password?" to "Log out from the Profile screen and click on 'Forgot Password' on the Login page."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Troubleshooting Guide") },
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
                "Find solutions to common issues below. Expand an item to see more details.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            guides.forEach { (issue, solution) ->
                TroubleshootItem(issue, solution)
            }
        }
    }
}

@Composable
fun TroubleshootItem(issue: String, solution: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = solution,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
