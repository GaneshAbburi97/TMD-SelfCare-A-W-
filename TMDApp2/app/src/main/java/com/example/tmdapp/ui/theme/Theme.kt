package com.example.tmdapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Dark Healthcare Theme Colors
private val DarkHealthcareBackground = Color(0xFF0A192F) // Deep Navy
private val DarkHealthcareSurface = Color(0xFF112240)    // Soft Navy Card
private val DarkHealthcareSurfaceVariant = Color(0xFF1D2D50) // Slightly lighter Navy for variety
private val DarkMedicalBlue = Color(0xFF64FFDA)          // Soft Cyan Accent
private val DarkMedicalBlueSecondary = Color(0xFF4FC3F7) // Soft Blue
private val DarkMedicalBlueTertiary = Color(0xFF2E7D32)  // Medical Green
private val DarkTextPrimary = Color(0xFFE6F1FF)          // Crisp White
private val DarkTextSecondary = Color(0xFF8892B0)        // Muted Blue-Grey

// Light Healthcare Theme Colors
private val LightHealthcareBackground = Color(0xFFF5F7FA) // Very Soft Grey
private val LightHealthcareSurface = Color(0xFFFFFFFF)    // Clean White
private val LightHealthcareSurfaceVariant = Color(0xFFE2E8F0) // Soft Grey for cards/dividers
private val LightMedicalBlue = Color(0xFF0056D2)          // Professional Medical Blue
private val LightMedicalBlueSecondary = Color(0xFFE3F2FD) // Soft Light Blue
private val LightMedicalBlueTertiary = Color(0xFF2E7D32)  // Medical Green
private val LightTextPrimary = Color(0xFF1A1C1E)          // Deep Slate
private val LightTextSecondary = Color(0xFF455A64)        // Muted Slate

private val DarkColorScheme = darkColorScheme(
    primary = DarkMedicalBlue,
    secondary = DarkMedicalBlueSecondary,
    tertiary = DarkMedicalBlueTertiary,
    background = DarkHealthcareBackground,
    surface = DarkHealthcareSurface,
    surfaceVariant = DarkHealthcareSurfaceVariant,
    onPrimary = DarkHealthcareBackground,
    onSecondary = DarkHealthcareBackground,
    onTertiary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightMedicalBlue,
    secondary = LightMedicalBlueSecondary,
    tertiary = LightMedicalBlueTertiary,
    background = LightHealthcareBackground,
    surface = LightHealthcareSurface,
    surfaceVariant = LightHealthcareSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = LightTextPrimary,
    onTertiary = Color.Black,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun TMDAppTheme(
    themePreference: String = "System Default",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (themePreference) {
        "Dark Mode", "Dark" -> true
        "Light Mode", "Light" -> false
        else -> isSystemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}