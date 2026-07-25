package com.example.tmdapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tmdapp.data.model.User
import com.example.tmdapp.ui.theme.MedicalBluePrimary
import java.io.File

@Composable
fun ProfileAvatar(
    user: User?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textSize: Int = 16
) {
    val imagePath = user?.profileImagePath
    val nameStr = user?.name ?: "Guest User"
    val initials = if (nameStr.isNotBlank()) {
        val parts = nameStr.trim().split(Regex("\\s+"))
        if (parts.size > 1) "${parts[0].first()}${parts[1].first()}" else "${parts[0].first()}"
    } else {
        "G"
    }.uppercase()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        if (!imagePath.isNullOrEmpty() && File(imagePath).exists()) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                color = MedicalBluePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = textSize.sp
            )
        }
    }
}
