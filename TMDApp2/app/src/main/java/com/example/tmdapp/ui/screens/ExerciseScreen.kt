package com.example.tmdapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.core.net.toUri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tmdapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.tmdapp.ui.components.AppHeader
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.util.HealthRecommendationEngine


// ─────────────────────────────────────────────
//  Data Model
// ─────────────────────────────────────────────

enum class Difficulty(val label: String, val color: Color) {
    MILD("Mild", Color(0xFF4CAF50)),
    MODERATE("Moderate", Color(0xFFFF9800)),
    @Suppress("unused") SEVERE("Severe", Color(0xFFF44336))
}

data class Exercise(
    val name: String,
    val description: String,
    val durationSec: Int,
    val reps: String = "",
    val category: String = "",
    val difficulty: Difficulty = Difficulty.MILD,
    val videoResId: Int? = null,
    val steps: List<String> = emptyList()
)

// ─────────────────────────────────────────────
//  Exercise Data
// ─────────────────────────────────────────────

val allExercises = listOf(
    Exercise(
        name = "Diaphragmatic Breathing",
        description = "Sit comfortably or lie down. Relax jaw and face muscles.",
        durationSec = 300,
        reps = "5 minutes",
        category = "Relaxation",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.diaphragmatic_breathing,
        steps = listOf(
            "Sit comfortably or lie down.",
            "Breathe in slowly through your nose for 4 seconds.",
            "Exhale gently through your mouth for 6 seconds.",
            "Relax your jaw and facial muscles.",
            "Repeat this cycle for 5 minutes."
        )
    ),
    Exercise(
        name = "Warm Compress",
        description = "Apply heat therapy to the jaw and temple.",
        durationSec = 1200,
        reps = "15-20 minutes",
        category = "Relaxation",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.warm_compress,
        steps = listOf(
            "Prepare a warm, moist towel or heat pack.",
            "Apply it directly to your jaw and temple area.",
            "Keep it in place for 15-20 minutes.",
            "Repeat up to 3 times daily."
        )
    ),
    Exercise(
        name = "Neck Side Stretch",
        description = "Gently stretch your neck side to side.",
        durationSec = 120,
        reps = "3 reps each side",
        category = "Stretching",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.neck_side_stretch,
        steps = listOf(
            "Tilt your right ear to your right shoulder.",
            "Hold the stretch for 20–30 seconds.",
            "Repeat on your left side.",
            "Complete 3 repetitions on each side."
        )
    ),
    Exercise(
        name = "Controlled Mouth Opening",
        description = "Controlled mouth opening with tongue on roof of mouth.",
        durationSec = 30,
        reps = "Hold 5 seconds",
        category = "Mobility",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.controlled_mouth_opening,
        steps = listOf(
            "Place your tongue on the roof of your mouth.",
            "Slowly open your mouth as wide as is comfortable.",
            "Hold the open position for 5 seconds.",
            "Slowly close your mouth."
        )
    ),
    Exercise(
        name = "Chin Tucks",
        description = "Gently pull chin straight back for neck posture.",
        durationSec = 30,
        reps = "Hold 5 seconds",
        category = "Mobility",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.chin_tucks,
        steps = listOf(
            "Sit upright and look straight ahead.",
            "Gently pull your chin straight back to create a 'double chin' position.",
            "Hold the position for 5 seconds.",
            "Relax and repeat."
        )
    ),
    Exercise(
        name = "Jaw Muscle Self-Massage",
        description = "Apply gentle circular pressure to the masseter muscle.",
        durationSec = 120,
        reps = "1-2 minutes each side",
        category = "Relaxation",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.jaw_muscle_self_massage,
        steps = listOf(
            "Place your fingers on your masseter muscle (the cheek area over the jaw joint).",
            "Apply gentle, circular pressure.",
            "Massage for 1-2 minutes on each side.",
            "Avoid pressing too hard if it causes sharp pain."
        )
    ),
    Exercise(
        name = "Resisted Opening",
        description = "Place thumb under chin and open mouth against gentle resistance.",
        durationSec = 30,
        reps = "Hold 3-5 seconds",
        category = "Strengthening",
        difficulty = Difficulty.MODERATE,
        videoResId = R.raw.resisted_opening,
        steps = listOf(
            "Place your thumb under your chin.",
            "Push upward gently with your thumb.",
            "At the same time, try to open your mouth slowly.",
            "Hold the open position for 3-5 seconds."
        )
    ),
    Exercise(
        name = "Resisted Closing",
        description = "Pinch chin and apply downward pressure while closing.",
        durationSec = 30,
        reps = "Hold 3-5 seconds",
        category = "Strengthening",
        difficulty = Difficulty.MODERATE,
        videoResId = R.raw.resisted_closing,
        steps = listOf(
            "Gently pinch your chin with your index finger and thumb.",
            "Apply slight downward pressure.",
            "Try to close your mouth slowly against this pressure.",
            "Hold for 3-5 seconds."
        )
    ),
    Exercise(
        name = "Side-to-Side Movement",
        description = "Slowly move jaw left to right with a folded cloth between teeth.",
        durationSec = 60,
        reps = "5 reps each side",
        category = "Mobility",
        difficulty = Difficulty.MODERATE,
        videoResId = R.raw.side_by_side_movement,
        steps = listOf(
            "Place a 1/4 inch folded cloth or popsicle sticks between your front teeth.",
            "Slowly move your jaw from the left to the right.",
            "Increase the thickness of the object between your teeth as mobility improves.",
            "Repeat 5 times on each side."
        )
    ),
    Exercise(
        name = "Box Breathing",
        description = "A calming breathing technique to reduce stress and jaw tension.",
        durationSec = 120,
        reps = "2 minutes",
        category = "Stress Relief",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.box_breathing,
        steps = listOf(
            "Inhale slowly through your nose for 4 seconds.",
            "Hold your breath for 4 seconds.",
            "Exhale slowly through your mouth for 4 seconds.",
            "Hold again for 4 seconds.",
            "Repeat the cycle for 2 minutes."
        )
    ),
    Exercise(
        name = "Guided Jaw Relaxation",
        description = "Consciously relax all jaw muscles to release stored tension.",
        durationSec = 60,
        reps = "1 minute",
        category = "Relaxation",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.guided_jaw_relaxation,
        steps = listOf(
            "Sit comfortably and close your eyes.",
            "Relax your jaw muscles completely.",
            "Keep your teeth slightly apart.",
            "Let your tongue rest naturally in your mouth.",
            "Hold this relaxed position for 1 minute."
        )
    ),
    Exercise(
        name = "Shoulder Rolls",
        description = "Gentle shoulder rolls to relieve neck and upper body tension.",
        durationSec = 60,
        reps = "1 minute",
        category = "Posture Relaxation",
        difficulty = Difficulty.MILD,
        videoResId = R.raw.shoulder_rolls,
        steps = listOf(
            "Sit or stand with your arms relaxed at your sides.",
            "Roll your shoulders forward in slow circles for 30 seconds.",
            "Reverse direction and roll backward for 30 seconds.",
            "Relax your neck and upper body throughout."
        )
    )
)

// ─────────────────────────────────────────────
//  Main Screen
// ─────────────────────────────────────────────

// Category display order
private val categoryOrder = listOf(
    "Relaxation", "Mobility", "Strengthening", "Stress Relief", "Stretching", "Posture Relaxation"
)

// Category color mapping
private fun categoryColor(category: String): Color = when (category) {
    "Relaxation"        -> Color(0xFF00897B)
    "Mobility"          -> Color(0xFF1565C0)
    "Strengthening"     -> Color(0xFFE65100)
    "Stress Relief"     -> Color(0xFF6A1B9A)
    "Stretching"        -> Color(0xFF2E7D32)
    "Posture Relaxation"-> Color(0xFF00838F)
    else                -> Color(0xFF455A64)
}

@Composable
fun ExerciseScreen(
    viewModel: TmdViewModel,
    onNavigateToDoctors: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val history by viewModel.history.collectAsState()
    val sleepHistory by viewModel.sleepHistory.collectAsState()
    val wellnessHistory by viewModel.wellnessHistory.collectAsState()
    val assessmentHistory by viewModel.assessmentHistory.collectAsState()

    val latestPainRec = history.firstOrNull()
    val latestSleep = sleepHistory.firstOrNull()
    val latestWellness = wellnessHistory.firstOrNull()
    val latestAssessment = assessmentHistory.firstOrNull()

    val currentPain = latestPainRec?.painLevel ?: 5
    val currentStress = latestPainRec?.stressLevel ?: 5
    val latestSleepQuality = latestWellness?.sleepQuality ?: latestSleep?.sleepQuality ?: "Average"
    val latestJawStiffness = latestWellness?.jawStiffness ?: "Medium"
    val teethGrinding = latestAssessment?.q1TeethGrinding == true || latestWellness?.teethGrinding == true
    val poorPosture = latestAssessment?.q11PoorPosture == true

    @Suppress("UNUSED_VALUE")
    var showSevereAlert by remember { mutableStateOf(currentPain in 7..10) }

    // ── Smart Recommendation Logic ──────────────────────────────
    val recommendedExercises = HealthRecommendationEngine.getRecommendedExercises(
        allExercises = allExercises,
        latestPain = currentPain,
        latestStress = currentStress,
        latestSleepQuality = latestSleepQuality,
        latestJawStiffness = latestJawStiffness,
        teethGrinding = teethGrinding,
        poorPosture = poorPosture
    )

    val recommendationMessage = HealthRecommendationEngine.generatePersonalizedRecommendation(
        latestPain = latestPainRec,
        latestSleep = latestSleep,
        latestWellness = latestWellness,
        assessment = latestAssessment
    )

    val recommendationIcon = when {
        currentStress >= 7 -> Icons.Default.SelfImprovement
        currentPain >= 7   -> Icons.Default.Warning
        else               -> Icons.Default.FitnessCenter
    }

    // ── Severe pain alert dialog ────────────────────────────────
    if (showSevereAlert && currentPain in 7..10) {
        AlertDialog(
            onDismissRequest = { showSevereAlert = false },
            title = { Text("Severe Pain Alert", color = MaterialTheme.colorScheme.error) },
            text = { Text("Your pain level is severe. Professional consultation is strongly recommended.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSevereAlert = false
                        onNavigateToDoctors()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Consult Doctor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSevereAlert = false }) {
                    Text("Continue Carefully")
                }
            }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── Group exercises by category ─────────────────────────────
    val groupedExercises = remember(recommendedExercises) {
        val grouped = recommendedExercises.groupBy { it.category }
        categoryOrder.filter { it in grouped.keys }.map { cat ->
            cat to (grouped[cat] ?: emptyList())
        }
    }

    Scaffold(
        topBar = {
            val currentUser by viewModel.currentUser.collectAsState()
            AppHeader(
                currentUser = currentUser,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ── Header + recommendation card ────────────────────────
            item {
                Text(
                    "Your Exercise Program",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentStress >= 7 || currentPain >= 7)
                            MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            recommendationIcon,
                            contentDescription = null,
                            tint = if (currentPain >= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Pain: $currentPain/10",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Stress: $currentStress/10",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                recommendationMessage,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // ── Category-grouped exercises ──────────────────────────
            groupedExercises.forEach { (category, exercises) ->
                item(key = "header_$category") {
                    Spacer(Modifier.height(4.dp))
                    CategoryHeader(category)
                }
                items(exercises, key = { it.name }) { exercise ->
                    ExerciseCard(exercise, onComplete = {
                        viewModel.saveExerciseRecord(exercise.name, exercise.durationSec, exercise.category)
                        scope.launch {
                            snackbarHostState.showSnackbar("Exercise completed successfully!")
                        }
                    })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Category Section Header
// ─────────────────────────────────────────────

@Composable
fun CategoryHeader(category: String) {
    val color = categoryColor(category)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                category,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = color.copy(alpha = 0.2f)
        )
    }
}

// ─────────────────────────────────────────────
//  Exercise Card with Video + Step Tabs
// ─────────────────────────────────────────────

@Composable
fun ExerciseCard(exercise: Exercise, onComplete: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(exercise.durationSec) }
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Watch Video, 1=Step Guide

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0 && isRunning) {
            isRunning = false
            if (!isCompleted) {
                isCompleted = true
                onComplete()
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header row ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (exercise.reps.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                exercise.reps,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryChip(exercise.category)
                    DifficultyBadge(exercise.difficulty)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Tab Selector ─────────────────────────────────
            TabSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(Modifier.height(12.dp))

            // ── Tab Content ──────────────────────────────────
            AnimatedContent(targetState = selectedTab, label = "tabContent") { tab ->
                when (tab) {
                    0 -> VideoTab(exercise)
                    1 -> StepGuideTab(exercise)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Timer / Progress ─────────────────────────────
            LinearProgressIndicator(
                progress = {
                    if (isCompleted) 1f
                    else 1f - (timeLeft.toFloat() / exercise.durationSec)
                },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Exercise Completed Successfully ✔️",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${timeLeft}s",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.weight(1f))
                    if (isRunning) {
                        Button(onClick = { isRunning = false }) { Text("Pause") }
                    } else {
                        Button(onClick = { isRunning = true }) { Text("Start") }
                    }
                    OutlinedButton(onClick = {
                        isRunning = false
                        timeLeft = 0
                        if (!isCompleted) {
                            isCompleted = true
                            onComplete()
                        }
                    }) { Text("Complete") }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Difficulty Badge
// ─────────────────────────────────────────────

@Composable
fun DifficultyBadge(difficulty: Difficulty) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(difficulty.color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            difficulty.label,
            color = difficulty.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────
//  Category Chip
// ─────────────────────────────────────────────

@Composable
fun CategoryChip(category: String) {
    val color = categoryColor(category)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            category,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────
//  Tab Selector
// ─────────────────────────────────────────────

@Composable
fun TabSelector(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Watch Video", "Step Guide")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTab
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                animationSpec = tween(200),
                label = "tabColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "tabTextColor"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    @Suppress("DEPRECATION")
                    Icon(
                        imageVector = if (index == 0) Icons.Default.PlayCircle else Icons.Default.ListAlt,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(title, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Video Tab
// ─────────────────────────────────────────────

@Composable
fun VideoTab(exercise: Exercise) {
    if (exercise.videoResId != null) {
        LocalVideoPlayer(videoResId = exercise.videoResId)
    } else {
        // Fallback if no video is available
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("No video available", color = Color.Gray)
        }
    }
}

@Composable
fun LocalVideoPlayer(videoResId: Int) {
    val context = LocalContext.current
    
    var isStarted by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    
    // The player instance - only created when started to save resources in LazyColumn
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    
    // Cleanup player when composable is disposed
    DisposableEffect(videoResId) {
        onDispose {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    // Effect to track playing state
    LaunchedEffect(exoPlayer) {
        val player = exoPlayer
        if (player != null) {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            }
            player.addListener(listener)
            isPlaying = player.isPlaying
        } else {
            isPlaying = false
        }
    }

    Column {
        // Video Preview / Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable {
                    if (!isStarted) {
                        isStarted = true
                        val player = ExoPlayer.Builder(context).build().apply {
                            val uri = "android.resource://${context.packageName}/$videoResId".toUri()
                            setMediaItem(MediaItem.fromUri(uri))
                            repeatMode = Player.REPEAT_MODE_ONE
                            volume = 1f
                            prepare()
                            playWhenReady = true
                        }
                        exoPlayer = player
                    } else {
                        if (isPlaying) {
                            exoPlayer?.pause()
                        } else {
                            exoPlayer?.volume = 1f
                            exoPlayer?.play()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isStarted && exoPlayer != null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false 
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Center play icon overlay for professional appearance
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Play Video",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(64.dp)
                    )
                    if (!isStarted) {
                        Text(
                            "Tap to Load Video",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 80.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Custom Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause Button
            Button(
                onClick = {
                    if (!isStarted) {
                        isStarted = true
                        val player = ExoPlayer.Builder(context).build().apply {
                            val uri = "android.resource://${context.packageName}/$videoResId".toUri()
                            setMediaItem(MediaItem.fromUri(uri))
                            repeatMode = Player.REPEAT_MODE_ONE
                            volume = 1f
                            prepare()
                            playWhenReady = true
                        }
                        exoPlayer = player
                    } else {
                        if (isPlaying) {
                            exoPlayer?.pause()
                        } else {
                            exoPlayer?.volume = 1f
                            exoPlayer?.play()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
                Spacer(Modifier.width(8.dp))
                Text(text = if (!isStarted) "Load" else if (isPlaying) "Pause" else "Play")
            }

            // Stop Button (Fully stops playback, resets video properly, prevents background audio)
            OutlinedButton(
                onClick = {
                    if (isStarted) {
                        exoPlayer?.pause()
                        exoPlayer?.release()
                        exoPlayer = null
                        isStarted = false
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                enabled = isStarted
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
                Spacer(Modifier.width(8.dp))
                Text("Stop")
            }

            // Fullscreen Button
            IconButton(
                onClick = { if (isStarted) isFullscreen = true },
                enabled = isStarted,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isStarted) MaterialTheme.colorScheme.surfaceVariant else Color.LightGray.copy(alpha = 0.3f))
            ) {
                Icon(
                    Icons.Default.Fullscreen, 
                    contentDescription = "Fullscreen",
                    tint = if (isStarted) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                )
            }
        }
    }

    // Fullscreen Dialog
    if (isFullscreen && exoPlayer != null) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false 
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (isPlaying) exoPlayer?.pause() else {
                                exoPlayer?.volume = 1f
                                exoPlayer?.play()
                            }
                        }
                )

                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable {
                                exoPlayer?.volume = 1f
                                exoPlayer?.play()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Play Video",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Fullscreen Overlay Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .fillMaxWidth(0.8f)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer?.pause()
                            } else {
                                exoPlayer?.volume = 1f
                                exoPlayer?.play()
                            }
                        }
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            exoPlayer?.pause()
                            exoPlayer?.seekTo(0)
                        }
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = { isFullscreen = false }
                    ) {
                        Icon(
                            Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}


// ─────────────────────────────────────────────
//  Step Guide Tab
// ─────────────────────────────────────────────

@Composable
fun StepGuideTab(exercise: Exercise) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            exercise.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        exercise.steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Step number circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${index + 1}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    step,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
