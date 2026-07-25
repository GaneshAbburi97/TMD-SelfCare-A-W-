package com.example.tmdapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.ui.components.AppHeader
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.util.HealthRecommendationEngine
import com.example.tmdapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ── colour tokens ──────────────────────────────────────────────────────────────
private val NavyBlue       @Composable get() = if (MaterialTheme.colorScheme.primary == Color(0xFF64FFDA)) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF003366)
private val NavyBlueText   @Composable get() = if (MaterialTheme.colorScheme.primary == Color(0xFF64FFDA)) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
private val BrandBlue      @Composable get() = MaterialTheme.colorScheme.primary
private val SoftBlue       @Composable get() = MaterialTheme.colorScheme.secondary
private val TealGreen      @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
private val TealAccent     @Composable get() = MaterialTheme.colorScheme.primary
private val ProgressGreen  @Composable get() = Color(0xFF2E7D32)
private val ActionButtonBg @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val CardWhite      @Composable get() = MaterialTheme.colorScheme.surface
private val TextPrimary    @Composable get() = MaterialTheme.colorScheme.onBackground
private val TextSecondary  @Composable get() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
private val DangerRed      @Composable get() = MaterialTheme.colorScheme.error
private val PageBg         @Composable get() = MaterialTheme.colorScheme.background

// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    viewModel: TmdViewModel,
    onNavigateToPainMap: () -> Unit = {},
    onNavigateToExercises: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAiChat: () -> Unit = {},
    onNavigateToWellness: () -> Unit = {},
    onNavigateToSleep: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val history     by viewModel.history.collectAsState()
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()
    val sleepHistory by viewModel.sleepHistory.collectAsState()
    val wellnessHistory by viewModel.wellnessHistory.collectAsState()
    val assessmentHistory by viewModel.assessmentHistory.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchCurrentUser() }

    val todayStr = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) }
    val hasPainLog = remember(history) { history.any { it.date == todayStr } }
    val hasSleepLog = remember(sleepHistory) { sleepHistory.any { it.date == todayStr } }
    val hasWellnessLog = remember(wellnessHistory) { wellnessHistory.any { it.date == todayStr } }
    val completedExercisesCount = remember(exerciseHistory) { exerciseHistory.count { it.date == todayStr } }

    val progressPercent = HealthRecommendationEngine.calculateDailyProgress(
        hasPainLog = hasPainLog,
        hasSleepLog = hasSleepLog,
        hasWellnessLog = hasWellnessLog,
        completedExercisesCount = completedExercisesCount
    )

    val latestRecord     = history.firstOrNull()
    val latestSleep = sleepHistory.firstOrNull()
    val latestWellness = wellnessHistory.firstOrNull()
    val latestAssessment = assessmentHistory.firstOrNull()

    val greeting         = buildGreeting()
    val userName         = currentUser?.name?.uppercase() ?: "USER"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAiChat,
                containerColor = NavyBlue,
                contentColor = NavyBlueText
            ) {
                Icon(Icons.Default.Chat, contentDescription = "AI Chat")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PageBg)
        ) {
            Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // ── HEADER ──────────────────────────────────────────────────────
            AppHeader(
                currentUser = currentUser,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )

            Spacer(Modifier.height(20.dp))

            // ── WELCOME SECTION ─────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "$greeting, $userName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Your Care Hub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                val streak = viewModel.calculateStreak()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$streak Day Streak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── DAILY PROGRESS CARD ─────────────────────────────────────────
            DailyProgressCard(
                progressPercent = progressPercent,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ── DAILY RECOMMENDATION CARD ───────────────────────────────────────
            DailyRecommendationCard(
                latestRecord    = latestRecord,
                latestSleep = latestSleep,
                latestWellness = latestWellness,
                latestAssessment = latestAssessment,
                onStartBreathing = onNavigateToExercises,
                modifier        = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(20.dp))

            // ── ACTION BUTTONS GRID ─────────────────────────────────────────
            Row(
                modifier            = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ActionButton(
                    title     = "Log Pain",
                    icon      = Icons.Default.Add,
                    bgColor   = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    modifier  = Modifier.weight(1f),
                    onClick   = onNavigateToPainMap
                )
                ActionButton(
                    title     = "Start Exercises",
                    icon      = Icons.Default.PlayArrow,
                    bgColor   = ActionButtonBg,
                    iconColor = BrandBlue,
                    textColor = TextPrimary,
                    modifier  = Modifier.weight(1f),
                    onClick   = onNavigateToExercises
                )
            }

            Spacer(Modifier.height(14.dp))

            ActionButton(
                title     = "Daily Wellness",
                icon      = Icons.Default.Favorite,
                bgColor   = ActionButtonBg,
                iconColor = BrandBlue,
                textColor = TextPrimary,
                modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                subtitle  = "Track recovery and wellness",
                onClick   = onNavigateToWellness
            )

            Spacer(Modifier.height(14.dp))

            ActionButton(
                title     = "Sleep Tracking",
                icon      = Icons.Default.Bedtime,
                bgColor   = ActionButtonBg,
                iconColor = BrandBlue,
                textColor = TextPrimary,
                modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                subtitle  = "Monitor sleep and recovery",
                onClick   = onNavigateToSleep
            )

            Spacer(Modifier.height(24.dp))

            // ── RECENT STATUS ───────────────────────────────────────────────
            RecentStatusSection(
                latestRecord = latestRecord,
                modifier     = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(20.dp))

            // ── MOTIVATION BANNER ───────────────────────────────────────────
            MotivationBanner(modifier = Modifier.padding(horizontal = 20.dp))

            Spacer(Modifier.height(8.dp))
        }
    }
}
}

// ── DAILY PROGRESS CARD ───────────────────────────────────────────────────────
@Composable
private fun DailyProgressCard(progressPercent: Int, modifier: Modifier = Modifier) {
    val progress  by animateFloatAsState(
        targetValue = progressPercent / 100f,
        animationSpec = tween(800),
        label = "progressAnim"
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier              = Modifier.padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Daily Progress",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text       = "$progressPercent% Completed",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress      = { progress },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color         = ProgressGreen,
                    trackColor    = Color(0xFFE8F5E9)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = "Based on logs & exercises",
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
            }
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector        = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint               = BrandBlue,
                modifier           = Modifier.size(48.dp)
            )
        }
    }
}

// ── DAILY RECOMMENDATION CARD ─────────────────────────────────────────────────────
@Composable
private fun DailyRecommendationCard(
    latestRecord: com.example.tmdapp.data.model.PainRecord?,
    latestSleep: com.example.tmdapp.data.model.SleepRecord?,
    latestWellness: com.example.tmdapp.data.model.WellnessRecord?,
    latestAssessment: com.example.tmdapp.data.model.AssessmentRecord?,
    onStartBreathing: () -> Unit,
    modifier:         Modifier = Modifier
) {
    val recommendation = HealthRecommendationEngine.generatePersonalizedRecommendation(
        latestPain = latestRecord,
        latestSleep = latestSleep,
        latestWellness = latestWellness,
        assessment = latestAssessment
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSecondary,
                    modifier           = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Daily Recommendation",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text     = recommendation,
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.85f),
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick  = onStartBreathing,
                shape    = RoundedCornerShape(50),
                colors   = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text       = "START NOW  →",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        }
    }
}

// ── ACTION BUTTON ─────────────────────────────────────────────────────────────
@Composable
private fun ActionButton(
    title:     String,
    icon:      ImageVector,
    bgColor:   Color,
    iconColor: Color,
    textColor: Color,
    modifier:  Modifier = Modifier,
    subtitle:  String? = null,
    onClick:   () -> Unit
) {
    Card(
        modifier  = modifier
            .height(if (subtitle == null) 110.dp else 125.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = if (bgColor == MaterialTheme.colorScheme.surface || bgColor == MaterialTheme.colorScheme.surfaceVariant) 
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)) 
            else null
    ) {
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = title,
                        tint               = iconColor,
                        modifier           = Modifier.size(22.dp)
                    )
                }
                if (subtitle != null) {
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text       = title,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = textColor
                        )
                        Text(
                            text       = subtitle,
                            fontSize   = 12.sp,
                            color      = textColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            if (subtitle == null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text       = title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = textColor
                )
            }
        }
    }
}

// ── RECENT STATUS SECTION ─────────────────────────────────────────────────────
@Composable
private fun RecentStatusSection(
    latestRecord: PainRecord?,
    modifier:     Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Recent Status",
                fontSize   = 17.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Text(
                text     = "View History",
                fontSize = 13.sp,
                color    = BrandBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(12.dp))

        if (latestRecord == null) {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(18.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "No pain logs yet. Tap \"Log Pain\" to get started.",
                        color    = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            RecentStatusCard(record = latestRecord)
        }
    }
}

@Composable
private fun RecentStatusCard(record: PainRecord) {
    val displayTime = remember(record.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
    }
    val painBadgeColor = when {
        record.painLevel >= 7 -> DangerRed
        record.painLevel >= 4 -> Color(0xFFF57C00)
        else                  -> ProgressGreen
    }
    val tags = buildList {
        if (record.type.isNotBlank()) add(record.type)
        if (record.location.isNotBlank()) add(record.location)
        if (record.painLevel >= 5)       add("Stiffness")
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.Schedule,
                        contentDescription = null,
                        tint               = TextSecondary,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = "Today, $displayTime",
                        fontSize = 12.sp,
                        color    = TextSecondary
                    )
                }
                // Pain level badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(painBadgeColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = "LEVEL ${record.painLevel}",
                        color      = Color.White,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text      = buildDescription(record),
                fontSize  = 14.sp,
                color     = TextPrimary,
                maxLines  = 3,
                overflow  = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            if (tags.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFE3F2FD))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text     = tag,
                                fontSize = 12.sp,
                                color    = BrandBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── MOTIVATION BANNER ─────────────────────────────────────────────────────────
@Composable
private fun MotivationBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(NavyBlue, SoftBlue)
                )
            )
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text       = "✨  Stay consistent, feel better.",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color.White,
                modifier   = Modifier.weight(1f)
            )
            Icon(
                imageVector        = Icons.Default.Favorite,
                contentDescription = null,
                tint               = Color(0xFFEF9A9A),
                modifier           = Modifier.size(22.dp)
            )
        }
    }
}

// ── HELPERS ───────────────────────────────────────────────────────────────────
private fun buildGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11  -> "GOOD MORNING"
        in 12..16 -> "GOOD AFTERNOON"
        in 17..20 -> "GOOD EVENING"
        else      -> "GOOD NIGHT"
    }
}

private fun buildDescription(record: PainRecord): String {
    val locationStr = record.location.ifBlank { "jaw area" }
    val typeStr     = record.type.ifBlank { "discomfort" }
    return "$typeStr pain in the $locationStr. " +
           "Pain level ${record.painLevel}/10, stress level ${record.stressLevel}/10."
}
