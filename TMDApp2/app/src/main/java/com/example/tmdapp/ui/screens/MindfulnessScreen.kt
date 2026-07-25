package com.example.tmdapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MindfulnessScreen() {
    var selectedMood by remember { mutableStateOf("") }
    val moods = listOf("😊 Happy", "😐 Neutral", "😟 Stressed", "😠 Angry")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CBT & Mindfulness",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("How are you feeling today?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            moods.forEach { mood ->
                FilterChip(
                    selected = selectedMood == mood,
                    onClick = { selectedMood = mood },
                    label = { Text(mood) }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Guided Breathing", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Inhale as the circle grows, exhale as it shrinks.", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(40.dp))

        BreathingCircle()
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CBT Tip:", fontWeight = FontWeight.Bold)
                Text("Notice if you are clenching your jaw when stressed. Try to keep your teeth slightly apart.")
            }
        }
    }
}

@Composable
fun BreathingCircle() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .scale(scale)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}
