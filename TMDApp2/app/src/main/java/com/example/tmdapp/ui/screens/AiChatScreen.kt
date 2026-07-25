package com.example.tmdapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmdapp.TmdViewModel
import com.example.tmdapp.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when messages change
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Initial greeting
    LaunchedEffect(Unit) {
        if (chatMessages.isEmpty()) {
            // Optional: You could trigger a silent "Hello" to the API here
            // But for a local start, we might just show a local message or wait for user.
            // As per plan, we can just let it be empty or add a welcome message to the VM state.
            // But we don't have a direct "add welcome message" in VM. We'll leave it empty initially,
            // or the ViewModel can be updated to start with one.
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Health Assistant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            Column {
                if (chatMessages.isEmpty()) {
                    val suggestions = listOf("My jaw hurts", "Suggest exercises", "Foods to avoid")
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.sendChatMessage(suggestion)
                                },
                                label = { Text(suggestion) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type your message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !isChatLoading,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isChatLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Disclaimer Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3CD))
                    .padding(12.dp)
            ) {
                Text(
                    text = "This AI assistant provides general wellness guidance and is not a replacement for professional medical advice.",
                    color = Color(0xFF856404),
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (chatMessages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Hello! I am your AI Health Assistant. How can I help you with your TMJ wellness today?",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                items(chatMessages) { message ->
                    ChatMessageBubble(message)
                }

                if (isChatLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val backgroundColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = formatter.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = textColor,
                fontSize = 15.sp
            )
        }
        Text(
            text = timeString,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    val dot1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dot1"
    )
    
    val dot2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ), label = "dot2"
    )
    
    val dot3 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ), label = "dot3"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f + 0.7f * dot1.value)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f + 0.7f * dot2.value)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f + 0.7f * dot3.value)))
    }
}
