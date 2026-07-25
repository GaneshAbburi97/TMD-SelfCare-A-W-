package com.example.tmdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tmdapp.ui.screens.*
import com.example.tmdapp.ui.screens.auth.*
import com.example.tmdapp.ui.theme.TMDAppTheme

// ── Bottom Nav Destinations ────────────────────────────────────────────────────
sealed class Screen(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Dashboard  : Screen("home",       "Dashboard",  Icons.Default.Home)
    object PainMap    : Screen("painmap",    "Pain Map",   Icons.Default.Place)
    object Exercises  : Screen("exercises",  "Exercises",  Icons.Default.FitnessCenter)
    object Progress   : Screen("reports",    "Progress",   Icons.Default.BarChart)
    object Support    : Screen("support",    "Support",    Icons.Default.Info)
    object Profile    : Screen("profile",    "Profile",    Icons.Default.Person)
    object AiChat     : Screen("ai_chat",    "AI Chat",    Icons.Default.Chat)
}

class MainActivity : ComponentActivity() {
    private val viewModel: TmdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by viewModel.settingsManager.themePreference.collectAsState()
            TMDAppTheme(themePreference = themePreference) {
                MainAppScaffold(viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: TmdViewModel) {
    val navController = rememberNavController()

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.PainMap,
        Screen.Exercises,
        Screen.Progress
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route
    val showBottomBar     = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor   = MaterialTheme.colorScheme.primary
                ) {
                    val currentDestination = navBackStackEntry?.destination
                    val currentUser by viewModel.currentUser.collectAsState()

                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon     = { 
                                if (screen == Screen.Profile) {
                                    com.example.tmdapp.ui.components.ProfileAvatar(
                                        user = currentUser,
                                        size = 24.dp,
                                        textSize = 10
                                    )
                                } else {
                                    Icon(screen.icon, contentDescription = screen.label) 
                                }
                            },
                            label    = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick  = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState    = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        NavHost(
            navController    = navController,
            startDestination = if (isLoggedIn) Screen.Dashboard.route else "login",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Auth ──────────────────────────────────────────────────────────
            composable("login") {
                LoginScreen(
                    viewModel              = viewModel,
                    onNavigateToHome       = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignUp          = { navController.navigate("signup") },
                    onNavigateToForgotPassword  = { navController.navigate("forgot_password") }
                )
            }
            composable("signup") {
                SignUpScreen(
                    viewModel          = viewModel,
                    onNavigateToHome   = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("login")  { inclusive = true }
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onNavigateToLogin  = { navController.popBackStack() }
                )
            }
            composable("forgot_password") {
                ForgotPasswordScreen(
                    viewModel      = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── Main App ──────────────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                HomeScreen(
                    viewModel            = viewModel,
                    onNavigateToPainMap  = {
                        navController.navigate(Screen.PainMap.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onNavigateToExercises = {
                        navController.navigate(Screen.Exercises.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToAiChat = { navController.navigate(Screen.AiChat.route) },
                    onNavigateToWellness = { navController.navigate("wellness") },
                    onNavigateToSleep = { navController.navigate("sleep") },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable("wellness") {
                DailyWellnessScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHistory = { navController.navigate("wellness_history") }
                )
            }
            composable("wellness_history") {
                WellnessHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("sleep") {
                SleepTrackingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHistory = { navController.navigate("sleep_history") }
                )
            }
            composable("sleep_history") {
                SleepHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PainMap.route)   { 
                PainMapScreen(
                    viewModel = viewModel,
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToNotifications = { navController.navigate("notifications") }
                ) 
            }
            composable(Screen.Exercises.route) {
                ExerciseScreen(
                    viewModel = viewModel,
                    onNavigateToDoctors = { navController.navigate("doctors") }
                )
            }
            composable(Screen.Progress.route)  { 
                ReportsScreen(
                    viewModel = viewModel,
                    onNavigateToDailyLogs = { navController.navigate("daily_logs") },
                    onNavigateToAssessment = { navController.navigate("assessment") },
                    onNavigateToHealthReport = { navController.navigate("health_report") },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToNotifications = { navController.navigate("notifications") }
                ) 
            }
            composable("daily_logs") {
                DailyLogsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Support.route)   {
                com.example.tmdapp.ui.screens.support.SupportHubScreen(
                    onNavigateToDoctors = { navController.navigate("doctors") },
                    onNavigateToHelp = { navController.navigate("help") },
                    onNavigateToAppointments = { navController.navigate("appointments") }
                )
            }
            composable(Screen.Profile.route)   {
                ProfileScreen(
                    viewModel          = viewModel,
                    onNavigateToLogin  = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToHelp = { navController.navigate("help") },
                    onNavigateToDoctors = { navController.navigate("doctors") },
                    onNavigateToAppointments = { navController.navigate("appointments") },
                    onNavigateToHealthReport = { navController.navigate("health_report") },
                    onNavigateToPrivacy = { navController.navigate("privacy_settings") },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToDownloadReports = { navController.navigate("download_reports") },
                    onNavigateToReportHistory = { navController.navigate("report_history") },
                    onNavigateToTroubleshooting = { navController.navigate("troubleshooting") },
                    onNavigateToTerms = { navController.navigate("terms_conditions") },
                    onNavigateToAbout = { navController.navigate("about_app") }
                )
            }
            
            // ── Settings & Support Sub-screens ─────────────────────────────────────────
            composable("settings") {
                com.example.tmdapp.ui.screens.settings.SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToTheme = { navController.navigate("theme_settings") },
                    onNavigateToUnits = { navController.navigate("unit_settings") }
                )
            }
            composable("theme_settings") {
                com.example.tmdapp.ui.screens.settings.ThemeSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("unit_settings") {
                com.example.tmdapp.ui.screens.settings.UnitSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("doctors") {
                com.example.tmdapp.ui.screens.support.DoctorsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBooking = { doctorId -> navController.navigate("book_appointment/$doctorId") },
                    onNavigateToChat = { doctorId -> navController.navigate("chat/$doctorId") }
                )
            }
            composable("book_appointment/{doctorId}") { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                com.example.tmdapp.ui.screens.support.BookingScreen(
                    viewModel = viewModel,
                    doctorId = doctorId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("chat/{doctorId}") { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                com.example.tmdapp.ui.screens.support.ChatScreen(
                    doctorId = doctorId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("help") {
                com.example.tmdapp.ui.screens.support.HelpSupportScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("appointments") {
                com.example.tmdapp.ui.screens.support.AppointmentsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("notifications") {
                com.example.tmdapp.ui.screens.NotificationsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AiChat.route) {
                com.example.tmdapp.ui.screens.AiChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("assessment") {
                AssessmentScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("health_report") {
                com.example.tmdapp.ui.screens.progress.HealthReportScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("privacy_settings") {
                com.example.tmdapp.ui.screens.settings.PrivacySettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("terms_conditions") {
                com.example.tmdapp.ui.screens.support.TermsConditionsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("about_app") {
                com.example.tmdapp.ui.screens.support.AboutApplicationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("troubleshooting") {
                com.example.tmdapp.ui.screens.support.TroubleshootingGuideScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("report_history") {
                com.example.tmdapp.ui.screens.progress.ReportHistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("download_reports") {
                com.example.tmdapp.ui.screens.progress.DownloadReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
