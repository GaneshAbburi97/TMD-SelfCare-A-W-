package com.example.tmdapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled._3dRotation
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.ui.components.AppHeader
import com.example.tmdapp.TmdViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PainMapScreen(
    viewModel: TmdViewModel,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val history by viewModel.history.collectAsState()
    val latestRecord = history.firstOrNull()
    
    val selectedRegions by viewModel.selectedRegions.collectAsState()
    val intensity by viewModel.painIntensity.collectAsState()
    val stressLevel by viewModel.stressLevel.collectAsState()
    
    var suggestedRegion by remember { mutableStateOf<String?>(null) }
    val hasLoadedInitial by viewModel.hasLoadedInitialPainMap.collectAsState()

    // Populate initial selections from last session's record (runs once on first non-null load)
    LaunchedEffect(latestRecord?.id) {
        if (!hasLoadedInitial && latestRecord != null && latestRecord.location.isNotBlank()) {
            val regions = latestRecord.location.split(",").map { it.trim() }.toSet()
            viewModel.selectedRegions.value = regions
            viewModel.painIntensity.value = latestRecord.painLevel.toFloat()
            viewModel.stressLevel.value = latestRecord.stressLevel.toFloat()
            viewModel.hasLoadedInitialPainMap.value = true
        }
        // Always keep the hint text current (this does NOT reset sliders or region chips)
        suggestedRegion = if (latestRecord != null && latestRecord.location.isNotBlank())
            latestRecord.location.split(",").first().trim()
        else null
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
        containerColor = MaterialTheme.colorScheme.background // Clean white/grey medical theme
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Removed PainMapHeaderSection as AppHeader is now in Scaffold topBar
                Spacer(modifier = Modifier.height(24.dp))
                TitleSection(suggestedRegion)
                Spacer(modifier = Modifier.height(16.dp))
                MainVisualContainer(selectedRegions)
                Spacer(modifier = Modifier.height(24.dp))
                QuickSelectionSection(selectedRegions) { region ->
                    val newRegions = if (selectedRegions.contains(region)) {
                        selectedRegions - region
                    } else {
                        selectedRegions + region
                    }
                    viewModel.selectedRegions.value = newRegions
                }
                Spacer(modifier = Modifier.height(24.dp))
                IntensitySection(intensity) { newIntensity ->
                    viewModel.painIntensity.value = newIntensity
                }
                Spacer(modifier = Modifier.height(24.dp))
                StressSection(stressLevel) { newStress ->
                    viewModel.stressLevel.value = newStress
                }
                Spacer(modifier = Modifier.height(32.dp))
                SaveLocationButton(onClick = {
                    viewModel.saveRecord(
                        pain = intensity.toInt(),
                        stress = stressLevel.toInt(),
                        location = selectedRegions.joinToString(", "),
                        type = "Map Log"
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Location and levels saved successfully!",
                            duration = SnackbarDuration.Short
                        )
                    }
                })
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TitleSection(suggestedRegion: String? = null) {
    Column {
        Text(
            text = "Map Your Pain",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Rotate and tap the areas where you feel discomfort.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            lineHeight = 20.sp
        )
        if (suggestedRegion != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Based on previous logs, your $suggestedRegion is frequently affected",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MainVisualContainer(selectedRegions: Set<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Face Mesh / Human Face Representation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary), // Teal background
                contentAlignment = Alignment.Center
            ) {
                FaceMeshView(selectedRegions)

                // Render dots for each selected region
                selectedRegions.forEach { region ->
                    val (alignment, offset) = getRegionPosition(region)
                    Box(modifier = Modifier.align(alignment).then(offset)) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F))
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }
            }

            // Floating control buttons
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingControlButton(Icons.Default._3dRotation, "Rotate")
                FloatingControlButton(Icons.Default.ZoomIn, "Zoom In")
                FloatingControlButton(Icons.Default.ZoomOut, "Zoom Out")
            }
        }
    }
}

@Composable
fun FaceMeshView(selectedRegions: Set<String>) {
    val lineColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f)
    val meshColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f)
    Canvas(modifier = Modifier.size(200.dp)) {
        val width = size.width
        val height = size.height
        
        // Simple Face Mesh Representation
        val facePath = Path().apply {
            // Outline of head
            moveTo(width * 0.5f, height * 0.1f)
            cubicTo(width * 0.1f, height * 0.1f, width * 0.1f, height * 0.7f, width * 0.5f, height * 0.95f)
            cubicTo(width * 0.9f, height * 0.7f, width * 0.9f, height * 0.1f, width * 0.5f, height * 0.1f)
        }
        
        drawPath(
            path = facePath,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Horizontal mesh lines
        for (i in 1..4) {
            val y = height * (0.1f + i * 0.16f)
            drawLine(
                color = meshColor,
                start = Offset(width * 0.2f, y),
                end = Offset(width * 0.8f, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Vertical mesh lines
        for (i in 1..3) {
            val x = width * (0.25f + i * 0.125f)
            drawLine(
                color = meshColor,
                start = Offset(x, height * 0.2f),
                end = Offset(x, height * 0.8f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

fun getRegionPosition(region: String): Pair<Alignment, Modifier> {
    return when (region) {
        "Left Jaw" -> Alignment.CenterStart to Modifier.offset(x = 65.dp, y = 30.dp)
        "Right Jaw" -> Alignment.CenterEnd to Modifier.offset(x = (-65).dp, y = 30.dp)
        "Chin" -> Alignment.BottomCenter to Modifier.offset(y = (-45).dp)
        "Neck" -> Alignment.BottomCenter to Modifier.offset(y = (-15).dp)
        "Head" -> Alignment.TopCenter to Modifier.offset(y = 35.dp)
        "Left Ear" -> Alignment.CenterStart to Modifier.offset(x = 45.dp, y = 0.dp)
        "Right Ear" -> Alignment.CenterEnd to Modifier.offset(x = (-45).dp, y = 0.dp)
        else -> Alignment.Center to Modifier.offset()
    }
}

@Composable
fun FloatingControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickSelectionSection(selectedRegions: Set<String>, onRegionToggle: (String) -> Unit) {
    Column {
        Text(
            text = "QUICK SELECTION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val regions = listOf("Head", "Left Ear", "Right Ear", "Left Jaw", "Right Jaw", "Chin", "Neck")
            regions.forEach { region ->
                SelectionPill(
                    text = region,
                    isSelected = selectedRegions.contains(region),
                    onClick = { onRegionToggle(region) }
                )
            }
        }
    }
}

@Composable
fun SelectionPill(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun IntensitySection(intensity: Float, onIntensityChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTENSITY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Text(
                text = "${intensity.toInt()}/10",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = intensity,
            onValueChange = onIntensityChange,
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "MILD", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            Text(text = "SEVERE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StressSection(stress: Float, onStressChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STRESS LEVEL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Text(
                text = "${stress.toInt()}/10",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = stress,
            onValueChange = onStressChange,
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "LOW", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            Text(text = "HIGH", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SaveLocationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Save Location",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


