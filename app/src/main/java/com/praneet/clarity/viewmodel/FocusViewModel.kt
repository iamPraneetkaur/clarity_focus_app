package com.praneet.clarity.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.praneet.clarity.data.repository.FocusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FocusViewModel(
    private val repository: FocusRepository,
    private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("focus_prefs", Context.MODE_PRIVATE)

    var goals = mutableStateListOf<Map<String, Any>>()
        private set

    var sessions = mutableStateListOf<Map<String, Any>>()
        private set

    private val firestore = FirebaseFirestore.getInstance()

    var initialSelectedMinutes by mutableStateOf(25)
        private set

    var selectedGoalId by mutableStateOf<String?>(null)
    var selectedGoalTitle by mutableStateOf<String?>(null)

    private var timerJob: Job? = null
    var timeLeft by mutableStateOf(25 * 60L)
    var isTimerRunning by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
        private set

    var showEnergySheet by mutableStateOf(false)
        private set

    var showCompletionCheer by mutableStateOf(false)
        private set

    init {
        loadGoals()
        loadSessions()
        restoreTimerState()
    }

    private fun restoreTimerState() {
        val isPausedStored = prefs.getBoolean("is_paused", false)
        val storedTimeLeft = prefs.getLong("remaining_time", -1L)
        val endTime = prefs.getLong("end_time", 0L)

        if (isPausedStored && storedTimeLeft > 0) {
            isPaused = true
            isTimerRunning = true
            timeLeft = storedTimeLeft
            initialSelectedMinutes = prefs.getInt("initial_minutes", 25)
        } else if (endTime > System.currentTimeMillis()) {
            isTimerRunning = true
            isPaused = false
            initialSelectedMinutes = prefs.getInt("initial_minutes", 25)
            startTimerCountdown(endTime)
        } else {
            clearTimerPrefs()
        }
    }

    private fun saveTimerRunning(endTime: Long, initialMinutes: Int) {
        prefs.edit().apply {
            putLong("end_time", endTime)
            putInt("initial_minutes", initialMinutes)
            putBoolean("is_paused", false)
            apply()
        }
    }

    private fun saveTimerPaused(remainingSeconds: Long) {
        prefs.edit().apply {
            putLong("remaining_time", remainingSeconds)
            putBoolean("is_paused", true)
            remove("end_time")
            apply()
        }
    }

    private fun clearTimerPrefs() {
        prefs.edit()
            .remove("end_time")
            .remove("initial_minutes")
            .remove("is_paused")
            .remove("remaining_time")
            .apply()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.getGoals().collect { snapshot ->
                goals.clear()
                for (doc in snapshot.documents) {
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    goals.add(data)
                }

                if (selectedGoalId == null && goals.isNotEmpty()) {
                    val firstGoal = goals[0]
                    val id = firstGoal["id"] as? String ?: return@collect
                    val title = firstGoal["title"] as? String ?: "Unnamed"
                    selectGoal(id, title)
                }
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            repository.getAllSessions().collect { snapshot ->
                sessions.clear()
                for (doc in snapshot.documents) {
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    sessions.add(data)
                }
            }
        }
    }

    fun selectGoal(id: String, title: String) {
        selectedGoalId = id
        selectedGoalTitle = title
    }

    fun updateSelectedDuration(minutes: Int) {
        initialSelectedMinutes = minutes
        timeLeft = minutes * 60L
    }

    fun createNewGoal(title: String, minutes: Int) {
        viewModelScope.launch {
            repository.addGoal(title, minutes)
        }
    }

    fun startTimer(durationMinutes: Int) {
        initialSelectedMinutes = durationMinutes
        timerJob?.cancel()

        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        saveTimerRunning(endTime, durationMinutes)

        timeLeft = durationMinutes * 60L
        isTimerRunning = true
        isPaused = false

        startTimerCountdown(endTime)
    }

    private fun startTimerCountdown(endTime: Long) {
        timerJob = viewModelScope.launch {
            while (System.currentTimeMillis() < endTime) {
                delay(1000)
                timeLeft = (endTime - System.currentTimeMillis()) / 1000
            }
            onTimerFinished()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        isPaused = true
        saveTimerPaused(timeLeft)
    }

    fun resumeTimer() {
        isPaused = false
        val durationMillis = timeLeft * 1000L
        val newEndTime = System.currentTimeMillis() + durationMillis
        saveTimerRunning(newEndTime, initialSelectedMinutes)
        startTimerCountdown(newEndTime)
    }

    fun stopTimer() {
        timerJob?.cancel()
        isTimerRunning = false
        isPaused = false
        timeLeft = initialSelectedMinutes * 60L
        clearTimerPrefs()
    }

    private fun onTimerFinished() {
        isTimerRunning = false
        isPaused = false
        showCompletionCheer = true
        clearTimerPrefs()
    }

    fun dismissCheer() {
        showCompletionCheer = false
        showEnergySheet = true
    }

    fun completeSession(energyLevel: String) {
        showEnergySheet = false
    }

    companion object {
        fun provideFactory(repository: FocusRepository, context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FocusViewModel(repository, context) as T
                }
            }
    }
}