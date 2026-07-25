package com.example.tmdapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.ui.components.ProfileAvatar
import com.example.tmdapp.ui.theme.MedicalBluePrimary
import com.example.tmdapp.ui.theme.MedicalTextSecondary
import com.example.tmdapp.util.ImageStorageUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TmdViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToDoctors: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToHealthReport: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToDownloadReports: () -> Unit = {},
    onNavigateToReportHistory: () -> Unit = {},
    onNavigateToTroubleshooting: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    var showPhotoOptions by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempUri != null) {
                val newPath = ImageStorageUtil.saveImageToInternalStorage(context, tempUri!!)
                viewModel.updateUserProfile(
                    currentUser?.name ?: "",
                    currentUser?.email ?: "",
                    newPath,
                    currentUser?.heightCm,
                    currentUser?.weightKg
                )
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempUri = ImageStorageUtil.createTempImageUri(context)
            tempUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val newPath = ImageStorageUtil.saveImageToInternalStorage(context, uri)
                viewModel.updateUserProfile(
                    currentUser?.name ?: "",
                    currentUser?.email ?: "",
                    newPath,
                    currentUser?.heightCm,
                    currentUser?.weightKg
                )
            }
        }
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "TMD Care AI",
                            color = MedicalBluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Already on Profile */ }) {
                        ProfileAvatar(user = currentUser, size = 32.dp, textSize = 14)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MedicalBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ACCOUNT SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.clickable { showPhotoOptions = true }
                    ) {
                        ProfileAvatar(user = currentUser, size = 100.dp, textSize = 32)
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MedicalBluePrimary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentUser?.name ?: "User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentUser?.heightCm != null) {
                            Text(
                                text = "Height: ${currentUser?.heightCm} cm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (currentUser?.weightKg != null) {
                            Text(
                                text = "Weight: ${currentUser?.weightKg} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showEditProfile = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SETTINGS SECTION
            SectionHeader(title = "SETTINGS")
            val unitSystem by viewModel.settingsManager.unitSystem.collectAsState()
            val themePreference by viewModel.settingsManager.themePreference.collectAsState()
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    LinkRow(
                        icon = Icons.Default.Palette,
                        title = "Theme Settings",
                        subtitle = themePreference,
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Straighten,
                        title = "Unit Settings",
                        subtitle = if (unitSystem == "Metric") "Metric" else "Imperial",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Notifications,
                        title = "Notification Settings",
                        onClick = onNavigateToNotifications
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Settings",
                        onClick = onNavigateToPrivacy
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REPORTS SECTION
            SectionHeader(title = "REPORTS")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    LinkRow(
                        icon = Icons.Default.Description,
                        title = "Health Reports",
                        onClick = onNavigateToHealthReport
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Download,
                        title = "Download Reports",
                        onClick = onNavigateToDownloadReports
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.History,
                        title = "Report History",
                        onClick = onNavigateToReportHistory
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SUPPORT SECTION
            SectionHeader(title = "SUPPORT")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    LinkRow(
                        icon = Icons.Default.HelpCenter,
                        title = "Help Center",
                        onClick = onNavigateToHelp
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.HelpOutline,
                        title = "FAQs",
                        onClick = onNavigateToHelp
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.MedicalServices,
                        title = "Find a Doctor",
                        onClick = onNavigateToDoctors
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Event,
                        title = "My Appointments",
                        onClick = onNavigateToAppointments
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    LinkRow(
                        icon = Icons.Default.Feedback,
                        title = "Feedback Form",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Build,
                        title = "Troubleshooting Guide",
                        onClick = onNavigateToTroubleshooting
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LEGAL SECTION
            SectionHeader(title = "LEGAL")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    LinkRow(
                        icon = Icons.Default.Lock,
                        title = "Privacy Policy",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Assignment,
                        title = "Terms & Conditions",
                        onClick = onNavigateToTerms
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Info,
                        title = "About Application",
                        onClick = onNavigateToAbout
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ACCOUNT MANAGEMENT
            SectionHeader(title = "ACCOUNT MANAGEMENT")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    LinkRow(
                        icon = Icons.Default.ExitToApp,
                        title = "Logout",
                        titleColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            viewModel.logout()
                            onNavigateToLogin()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    LinkRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            viewModel.deleteAccount()
                            onNavigateToLogin()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Version 2.4.1 (Clinical Release)",
                color = MedicalTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // Photo Options Bottom Sheet
        if (showPhotoOptions) {
            ModalBottomSheet(
                onDismissRequest = { showPhotoOptions = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Profile Photo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    ListItem(
                        headlineContent = { Text("Take Photo") },
                        leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showPhotoOptions = false
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                tempUri = ImageStorageUtil.createTempImageUri(context)
                                tempUri?.let { cameraLauncher.launch(it) }
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Choose from Gallery") },
                        leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showPhotoOptions = false
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )

                    if (currentUser?.profileImagePath != null) {
                        ListItem(
                            headlineContent = { Text("Remove Photo", color = Color.Red) },
                            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                            modifier = Modifier.clickable {
                                showPhotoOptions = false
                                viewModel.updateUserProfile(
                                    currentUser?.name ?: "",
                                    currentUser?.email ?: "",
                                    null,
                                    currentUser?.heightCm,
                                    currentUser?.weightKg
                                )
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfile) {
            var editName by remember { mutableStateOf(currentUser?.name ?: "") }
            var editEmail by remember { mutableStateOf(currentUser?.email ?: "") }
            var editHeight by remember { mutableStateOf(currentUser?.heightCm?.toString() ?: "") }
            var editWeight by remember { mutableStateOf(currentUser?.weightKg?.toString() ?: "") }

            AlertDialog(
                onDismissRequest = { showEditProfile = false },
                title = { Text("Edit Profile") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editHeight,
                                onValueChange = { editHeight = it },
                                label = { Text("Height (cm)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editWeight,
                                onValueChange = { editWeight = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val height = editHeight.toFloatOrNull()
                        val weight = editWeight.toFloatOrNull()
                        viewModel.updateUserProfile(editName, editEmail, currentUser?.profileImagePath, height, weight)
                        showEditProfile = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfile = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), // Slate gray
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp, start = 4.dp, top = 8.dp)
    )
}

@Composable
fun LinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (titleColor == MaterialTheme.colorScheme.onBackground) MaterialTheme.colorScheme.onSurfaceVariant else titleColor.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                fontWeight = if (subtitle != null) FontWeight.SemiBold else FontWeight.Normal
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go",
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
        )
    }
}
