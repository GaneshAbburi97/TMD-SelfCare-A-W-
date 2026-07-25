package com.example.tmdapp.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tmd_settings", Context.MODE_PRIVATE)

    private val _dailyReminders = MutableStateFlow(prefs.getBoolean("daily_reminders", true))
    val dailyReminders: StateFlow<Boolean> = _dailyReminders

    private val _exerciseNotifications = MutableStateFlow(prefs.getBoolean("exercise_notifications", true))
    val exerciseNotifications: StateFlow<Boolean> = _exerciseNotifications

    private val _themePreference = MutableStateFlow(prefs.getString("theme_preference", "Light") ?: "Light")
    val themePreference: StateFlow<String> = _themePreference

    private val _unitSystem = MutableStateFlow(prefs.getString("unit_system", "Metric") ?: "Metric")
    val unitSystem: StateFlow<String> = _unitSystem

    private val _language = MutableStateFlow(prefs.getString("language", "English") ?: "English")
    val language: StateFlow<String> = _language

    fun setDailyReminders(enabled: Boolean) {
        prefs.edit().putBoolean("daily_reminders", enabled).apply()
        _dailyReminders.value = enabled
    }

    fun setExerciseNotifications(enabled: Boolean) {
        prefs.edit().putBoolean("exercise_notifications", enabled).apply()
        _exerciseNotifications.value = enabled
    }

    fun setThemePreference(theme: String) {
        prefs.edit().putString("theme_preference", theme).apply()
        _themePreference.value = theme
    }

    fun setUnitSystem(system: String) {
        prefs.edit().putString("unit_system", system).apply()
        _unitSystem.value = system
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _language.value = lang
    }

    fun clearAppData() {
        prefs.edit().clear().apply()
        _dailyReminders.value = true
        _exerciseNotifications.value = true
        _themePreference.value = "Light"
        _unitSystem.value = "Metric"
        _language.value = "English"
    }
}
