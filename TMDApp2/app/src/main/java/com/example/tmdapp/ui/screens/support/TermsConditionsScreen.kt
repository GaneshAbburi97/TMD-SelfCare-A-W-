package com.example.tmdapp.ui.screens.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions") },
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
                "Last Updated: May 2026",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                "1. Medical Disclaimer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "The TMD Care AI application is designed to assist in tracking symptoms and providing recommended exercises for Temporomandibular Disorders. The content provided is for informational purposes only and does not substitute professional medical advice, diagnosis, or treatment.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "2. User Responsibilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "You agree to use the application safely and responsibly. Do not perform exercises if they cause severe pain, and consult a healthcare professional immediately if symptoms worsen.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "3. Data Privacy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "We take your privacy seriously. Your health data, including pain logs and exercise history, is stored securely. We do not sell your personal data to third parties. Please refer to our Privacy Policy for more details.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                "4. Subscription and Billing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Certain features, such as direct doctor consultations, may require premium access or pay-per-use fees. By using these services, you agree to our billing terms.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
