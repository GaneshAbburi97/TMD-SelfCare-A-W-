package com.example.tmdapp.ui.screens.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.data.model.Doctor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val doctors = viewModel.doctorsList

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consult Doctors") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:0000000000") // Mock number
                        }
                        context.startActivity(intent)
                    },
                    onBook = { onNavigateToBooking(doctor.id) },
                    onChat = { onNavigateToChat(doctor.id) }
                )
            }
        }
    }
}

@Composable
fun DoctorCard(
    doctor: Doctor,
    onCall: () -> Unit,
    onBook: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(doctor.name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(doctor.rating, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(doctor.specialization, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Experience: ${doctor.experience}", style = MaterialTheme.typography.bodySmall)
            Text("Location: ${doctor.location}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onCall, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onBook, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Event, contentDescription = "Book", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Book")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onChat) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
