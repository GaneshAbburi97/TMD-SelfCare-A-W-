package com.example.tmdapp.ui.screens.support

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: TmdViewModel,
    doctorId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }
    val doctor = viewModel.doctorsList.find { it.id == doctorId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (showConfirmation) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Appointment Confirmed!", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Doctor: ${doctor?.name}", style = MaterialTheme.typography.bodyLarge)
                        Text("Date: $date", style = MaterialTheme.typography.bodyLarge)
                        Text("Time: $time", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Done")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Booking with ${doctor?.name ?: "Doctor"}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g., 10:00 AM)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for visit") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (date.isNotBlank() && time.isNotBlank() && reason.isNotBlank()) {
                            viewModel.bookAppointment(doctorId, date, time, reason)
                            showConfirmation = true
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Booking")
                }
            }
        }
    }
}
