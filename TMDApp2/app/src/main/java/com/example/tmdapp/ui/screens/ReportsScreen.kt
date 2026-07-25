package com.example.tmdapp.ui.screens

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.ui.components.AppHeader
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.ui.screens.progress.ActivityTab
import com.example.tmdapp.ui.screens.progress.MonthlyTab
import com.example.tmdapp.ui.screens.progress.TrendsTab
import com.example.tmdapp.ui.screens.progress.WeeklyTab
import com.example.tmdapp.util.PdfGenerator
import java.io.File
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.tmdapp.util.DemoDataGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: TmdViewModel,
    onNavigateToDailyLogs: () -> Unit,
    onNavigateToAssessment: () -> Unit,
    onNavigateToHealthReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val painHistory by viewModel.history.collectAsState()
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()
    val sleepHistory by viewModel.sleepHistory.collectAsState()
    val wellnessHistory by viewModel.wellnessHistory.collectAsState()
    val assessmentHistory by viewModel.assessmentHistory.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Trends", "Weekly", "Monthly", "Activity")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        topBar = {
            val currentUser by viewModel.currentUser.collectAsState()
            AppHeader(
                currentUser = currentUser,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToHealthReport,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = "Generate Health Report") },
                text = { Text("Health Report", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> TrendsTab(painHistory, sleepHistory, wellnessHistory, assessmentHistory)
                    1 -> WeeklyTab(painHistory, sleepHistory, wellnessHistory, assessmentHistory)
                    2 -> MonthlyTab(painHistory, sleepHistory, wellnessHistory, assessmentHistory)
                    3 -> ActivityTab(painHistory, sleepHistory, wellnessHistory, assessmentHistory)
                }
            }
        }
    }
}

// Removed exportProgressPdf as we now use HealthReportScreen
