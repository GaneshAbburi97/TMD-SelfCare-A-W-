package com.example.tmdapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onNavigateBack: () -> Unit
) {
    var shareAnalytics by remember { mutableStateOf(true) }
    var healthDataSync by remember { mutableStateOf(true) }
    var personalizedAds by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Settings") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Manage how your data is used and shared within the application.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Share Analytics Data") },
                supportingContent = { Text("Help us improve the app by sharing anonymous usage data and crash reports.") },
                trailingContent = {
                    Switch(checked = shareAnalytics, onCheckedChange = { shareAnalytics = it })
                }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

            ListItem(
                headlineContent = { Text("Health Data Sync") },
                supportingContent = { Text("Sync your pain logs and exercise data across your devices securely.") },
                trailingContent = {
                    Switch(checked = healthDataSync, onCheckedChange = { healthDataSync = it })
                }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

            ListItem(
                headlineContent = { Text("Personalized Recommendations") },
                supportingContent = { Text("Allow the AI to use your historical data to provide customized therapy suggestions.") },
                trailingContent = {
                    Switch(checked = personalizedAds, onCheckedChange = { personalizedAds = it })
                }
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Data Deletion",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* Handle data request/deletion */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Request Data Deletion")
            }
        }
    }
}
