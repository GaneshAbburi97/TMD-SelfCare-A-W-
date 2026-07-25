package com.example.tmdapp.ui.screens.support

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var feedbackName by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }

    val faqs = listOf(
        "What is TMD?" to "Temporomandibular disorder (TMD) affects the jaw joints and surrounding muscles and ligaments.",
        "How to use exercises?" to "Navigate to the Exercises tab, select a recommended exercise, and follow the animated guide.",
        "How to track pain?" to "Use the Dashboard or Pain Map to log your daily pain levels and stress, keeping track of your history."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // FAQs
            Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            faqs.forEach { (question, answer) ->
                FaqItem(question, answer)
                Spacer(modifier = Modifier.height(8.dp))
            }



            Spacer(modifier = Modifier.height(24.dp))

            // Feedback Form
            Text("Send Feedback", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feedbackName,
                onValueChange = { feedbackName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feedbackMessage,
                onValueChange = { feedbackMessage = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (feedbackName.isNotBlank() && feedbackMessage.isNotBlank()) {
                        viewModel.submitFeedback(feedbackName, feedbackMessage)
                        Toast.makeText(context, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                        feedbackName = ""
                        feedbackMessage = ""
                    } else {
                        Toast.makeText(context, "Please enter name and message", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(question, style = MaterialTheme.typography.titleSmall)
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(answer, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
