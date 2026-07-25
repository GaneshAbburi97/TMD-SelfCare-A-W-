package com.example.tmdapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdapp.data.local.SessionManager
import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.model.User
import com.example.tmdapp.data.repository.AuthRepository
import com.example.tmdapp.data.repository.PainRepository
import com.example.tmdapp.data.repository.WellnessRepository
import com.example.tmdapp.data.repository.SleepRepository
import com.example.tmdapp.data.repository.AssessmentRepository
import com.example.tmdapp.data.repository.ExerciseRepository
import com.example.tmdapp.data.repository.ChatRepository
import com.example.tmdapp.data.local.SettingsManager
import com.example.tmdapp.data.model.Doctor
import com.example.tmdapp.data.model.WellnessRecord
import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.model.AssessmentRecord
import io.github.jan.supabase.auth.auth
import com.example.tmdapp.data.model.ExerciseRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.tmdapp.data.model.Appointment
import com.example.tmdapp.data.model.AppNotification
import java.util.UUID

class TmdViewModel(application: Application) : AndroidViewModel(application) {
    val sessionManager = SessionManager(application)
    val settingsManager = SettingsManager(application)
    
    private val _isLoggedIn = MutableStateFlow(sessionManager.currentUserId.value != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // Supabase-backed repositories (no longer need Room database or DAOs)
    private val authRepository = AuthRepository()
    private val painRepository = PainRepository()
    private val wellnessRepository = WellnessRepository()
    private val sleepRepository = SleepRepository()
    private val assessmentRepository = AssessmentRepository()
    private val exerciseRepository = ExerciseRepository()
    private val chatRepository = ChatRepository()
    
    private val _history = MutableStateFlow<List<PainRecord>>(emptyList())
    val history: StateFlow<List<PainRecord>> = _history

    private val _wellnessHistory = MutableStateFlow<List<WellnessRecord>>(emptyList())
    val wellnessHistory: StateFlow<List<WellnessRecord>> = _wellnessHistory.asStateFlow()

    private val _sleepHistory = MutableStateFlow<List<SleepRecord>>(emptyList())
    val sleepHistory: StateFlow<List<SleepRecord>> = _sleepHistory.asStateFlow()

    private val _assessmentHistory = MutableStateFlow<List<AssessmentRecord>>(emptyList())
    val assessmentHistory: StateFlow<List<AssessmentRecord>> = _assessmentHistory.asStateFlow()

    private val _exerciseHistory = MutableStateFlow<List<ExerciseRecord>>(emptyList())
    val exerciseHistory: StateFlow<List<ExerciseRecord>> = _exerciseHistory.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()
    
    private val _chatMessages = MutableStateFlow<List<com.example.tmdapp.data.model.ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<com.example.tmdapp.data.model.ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Persisted state for Pain Map screen to survive tab navigation
    val selectedRegions = MutableStateFlow<Set<String>>(setOf("Left Jaw"))
    val painIntensity = MutableStateFlow(7f)
    val stressLevel = MutableStateFlow(5f)
    val hasLoadedInitialPainMap = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            try {
                com.example.tmdapp.data.remote.SupabaseClient.client.auth.awaitInitialization()
            } catch (e: Exception) {
                // Ignore if it's already initialized or fails
            }
            sessionManager.currentUserId.collectLatest { userId ->
                if (userId != null) {
                    _isLoggedIn.value = true
                    loadAllData(userId)
                } else {
                    _isLoggedIn.value = false
                    _history.value = emptyList()
                    _wellnessHistory.value = emptyList()
                    _sleepHistory.value = emptyList()
                    _assessmentHistory.value = emptyList()
                    _exerciseHistory.value = emptyList()
                    _currentUser.value = null
                }
            }
        }

        // Mock some notifications
        _notifications.value = listOf(
            AppNotification(UUID.randomUUID().toString(), "Time for Exercise", "Your daily jaw exercise session is pending.", System.currentTimeMillis() - 3600000),
            AppNotification(UUID.randomUUID().toString(), "Log Your Pain", "Don't forget to map your pain today.", System.currentTimeMillis() - 86400000)
        )
    }

    private fun loadAllData(userId: String) {
        viewModelScope.launch {
            try {
                painRepository.getRecordsForUser(userId).collectLatest { records ->
                    _history.value = records.sortedByDescending { it.timestamp }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        viewModelScope.launch {
            try {
                wellnessRepository.getWellnessRecordsForUser(userId).collectLatest { records ->
                    _wellnessHistory.value = records
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        viewModelScope.launch {
            try {
                sleepRepository.getSleepRecordsForUser(userId).collectLatest { records ->
                    _sleepHistory.value = records
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        viewModelScope.launch {
            try {
                assessmentRepository.getAssessmentRecordsForUser(userId).collectLatest { records ->
                    _assessmentHistory.value = records
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        viewModelScope.launch {
            try {
                exerciseRepository.getRecordsForUser(userId).collectLatest { records ->
                    _exerciseHistory.value = records
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun refreshData() {
        val userId = sessionManager.currentUserId.value ?: return
        loadAllData(userId)
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
        }
    }

    fun updateUserProfile(name: String, email: String, imagePath: String?, heightCm: Float? = null, weightKg: Float? = null) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            val success = authRepository.updateUserProfile(userId, name, email, imagePath, heightCm, weightKg)
            if (success) {
                fetchCurrentUser()
            }
        }
    }

    fun deleteAccount() {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            val success = authRepository.deleteAccount(userId)
            if (success) {
                logout()
            }
        }
    }

    fun saveRecord(pain: Int, stress: Int, location: String, type: String) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val newRecord = painRepository.saveRecord(userId, pain, stress, location, type)
                _history.value = listOf(newRecord) + _history.value
                refreshData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveExerciseRecord(exerciseName: String, durationSec: Int, category: String) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val newRecord = exerciseRepository.saveRecord(userId, exerciseName, durationSec, category)
                _exerciseHistory.value = listOf(newRecord) + _exerciseHistory.value
                refreshData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveWellnessRecord(
        sleepQuality: String,
        jawStiffness: String,
        teethGrinding: Boolean,
        mood: String,
        waterIntake: Int,
        energyLevel: Int,
        notes: String
    ) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val newRecord = wellnessRepository.saveWellnessRecord(
                    userId, sleepQuality, jawStiffness, teethGrinding, mood, waterIntake, energyLevel, notes
                )
                _wellnessHistory.value = listOf(newRecord) + _wellnessHistory.value
                refreshData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveSleepRecord(
        sleepHours: Float,
        sleepQuality: String,
        jawClenching: Boolean,
        morningStiffness: String,
        wakeupFeeling: String,
        notes: String
    ) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val newRecord = sleepRepository.saveSleepRecord(
                    userId, sleepHours, sleepQuality, jawClenching, morningStiffness, wakeupFeeling, notes
                )
                _sleepHistory.value = listOf(newRecord) + _sleepHistory.value
                refreshData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveAssessment(
        q1: Boolean, q2: Boolean, q3: Boolean, q4: Boolean, q5: Boolean, q6: Boolean,
        q7: Boolean, q8: Boolean, q9: Boolean, q10: Boolean, q11: Boolean, q12: Boolean,
        sleepDur: Float, water: Float, stressFreq: String, painFreq: String, exerciseCons: String
    ) {
        val userId = sessionManager.currentUserId.value ?: return
        viewModelScope.launch {
            try {
                // Generate Smart Analysis
                val analysisBuilder = StringBuilder()
            if (q1 || q2) analysisBuilder.append("Frequent jaw clenching/grinding detected. Consider a night guard and relaxation exercises.\n")
            if (q3 || q4) analysisBuilder.append("Chewing gum or biting objects strains your jaw muscles. Try to reduce these habits.\n")
            if (q10 || stressFreq == "Often" || stressFreq == "Always") analysisBuilder.append("High stress levels observed. Incorporate CBT and breathing exercises into your routine.\n")
            if (q9 || sleepDur < 6f) analysisBuilder.append("Poor sleep may increase TMD severity. Aim for at least 7 hours of rest.\n")
            if (q11) analysisBuilder.append("Poor posture can contribute to neck and jaw pain. Practice posture correction exercises.\n")
            if (q12) analysisBuilder.append("Chewing on one side can cause muscle imbalance. Try to distribute chewing evenly.\n")
            
            var finalAnalysis = analysisBuilder.toString().trim()
            if (finalAnalysis.isEmpty()) {
                finalAnalysis = "Your habits look good! Keep maintaining a balanced lifestyle for optimal TMD recovery."
            }

            val record = AssessmentRecord(
                userId = userId,
                q1TeethGrinding = q1, q2JawClenching = q2, q3ChewGum = q3, q4BiteNails = q4,
                q5JawClicking = q5, q6DifficultyChewing = q6, q7MorningStiffness = q7,
                q8FrequentHeadaches = q8, q9SleepLessThan6Hours = q9, q10HighStress = q10,
                q11PoorPosture = q11, q12OneSideChewing = q12,
                sleepDuration = sleepDur, waterIntake = water, stressFrequency = stressFreq,
                jawPainFrequency = painFreq, exerciseConsistency = exerciseCons,
                smartAnalysis = finalAnalysis
            )
            assessmentRepository.saveAssessmentRecord(record)
            refreshData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun signUp(name: String, email: String, passwordRaw: String, onSuccess: () -> Unit, onNeedsOtp: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.signUp(name, email, passwordRaw)
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    sessionManager.saveUserId(user.id)
                    onSuccess()
                } else {
                    onNeedsOtp()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Signup failed. Please try again.")
            }
        }
    }

    fun login(email: String, passwordRaw: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = authRepository.login(email, passwordRaw)
            if (success) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    sessionManager.saveUserId(user.id)
                }
                onSuccess()
            } else {
                onError("Invalid email or password.")
            }
        }
    }

    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = authRepository.verifyEmailOtp(email, otp)
            if (success) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    sessionManager.saveUserId(user.id)
                    onSuccess()
                } else {
                    onError("OTP verified, but failed to load user profile.")
                }
            } else {
                onError("Invalid or expired OTP code.")
            }
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = authRepository.loginWithGoogle(idToken)
                if (success) {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        sessionManager.saveUserId(user.id)
                        onSuccess()
                    } else {
                        onError("Failed to fetch user profile after Google login")
                    }
                } else {
                    onError("Google login failed via Supabase")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Authentication failed")
            }
        }
    }

    fun resetPassword(email: String) {
        // Supabase supports password reset via email
        // supabase.auth.resetPasswordForEmail(email)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            
            // Sign out of Google to clear the default cached account
            try {
                val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(getApplication<Application>(), gso)
                googleSignInClient.signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            sessionManager.clearSession()
        }
    }

    fun getRecommendation(pain: Int, stress: Int): String {
        return when {
            pain >= 7 && stress >= 7 -> "Severe Condition: CBT + Heat Therapy + Rest"
            pain >= 7 -> "High Pain: Apply Heat + Limit Jaw Movement"
            stress >= 7 -> "High Stress: Perform Breathing Exercise (CBT)"
            pain >= 4 -> "Moderate Pain: Jaw Exercises + Relaxation"
            else -> "Mild Condition: Maintain Routine Care"
        }
    }

    val doctorsList = listOf(
        Doctor("1", "Dr. Ravi Kumar", "Dentist", "8 years", "Chennai", "4.8"),
        Doctor("2", "Dr. Priya Sharma", "TMJ Specialist", "10 years", "Hyderabad", "4.9"),
        Doctor("3", "Dr. Arjun Mehta", "Physiotherapist", "6 years", "Bangalore", "4.7")
    )

    fun submitFeedback(name: String, message: String) {
        // Mock submission
        println("Feedback submitted by $name: $message")
    }

    fun bookAppointment(doctorId: String, date: String, time: String, reason: String) {
        val doctor = doctorsList.find { it.id == doctorId }
        val newAppointment = Appointment(
            id = UUID.randomUUID().toString(),
            doctorName = doctor?.name ?: "Unknown Doctor",
            date = date,
            time = time
        )
        _appointments.value = _appointments.value + newAppointment
    }

    fun calculateStreak(): Int {
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
        val allDates = (_history.value.map { it.date } + 
                       _wellnessHistory.value.map { it.date } +
                       _sleepHistory.value.map { it.date } +
                       _exerciseHistory.value.map { it.date }).distinct()
        
        if (allDates.isEmpty()) return 0
        
        val dateObjects = allDates.mapNotNull { 
            try { sdf.parse(it) } catch (e: Exception) { null } 
        }.sortedDescending()
        
        if (dateObjects.isEmpty()) return 0

        val cal = java.util.Calendar.getInstance()
        val today = sdf.parse(sdf.format(cal.time)) ?: return 0
        
        var streak = 0
        var expectedDate = today

        // If the most recent log isn't today or yesterday, streak is broken.
        val diffToLatest = (today.time - dateObjects.first().time) / (1000 * 60 * 60 * 24)
        if (diffToLatest > 1) return 0
        
        // Start expecting from the latest logged day
        expectedDate = dateObjects.first()

        for (date in dateObjects) {
            val diff = (expectedDate.time - date.time) / (1000 * 60 * 60 * 24)
            if (diff == 0L) {
                streak++
                cal.time = expectedDate
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                expectedDate = cal.time
            } else {
                break
            }
        }
        return streak
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val userMsg = com.example.tmdapp.data.model.ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            _chatMessages.value = _chatMessages.value + userMsg
            _isChatLoading.value = true
            
            val aiResponseText = chatRepository.sendMessage(text)
            
            val aiMsg = com.example.tmdapp.data.model.ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = aiResponseText,
                timestamp = System.currentTimeMillis()
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isChatLoading.value = false
        }
    }
}
