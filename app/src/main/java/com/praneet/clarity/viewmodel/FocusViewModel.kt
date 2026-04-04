package com.praneet.clarity.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.praneet.clarity.data.model.EnergyLevel
import com.praneet.clarity.data.model.FocusSession
import com.praneet.clarity.data.repository.FocusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusViewModel(private val repository: FocusRepository) : ViewModel() {

    // --- GOAL STATE ---
    // This list will automatically update your UI when Firestore data changes
    var goals = mutableStateListOf<Map<String, Any>>()
        private set

    // --- TIMER STATE ---
    private var timerJob: Job? = null
    var timeLeft by mutableStateOf(25 * 60L) // Default 25 minutes in seconds
    var isTimerRunning by mutableStateOf(false)

    // --- UI STATE ---
    var showEnergySheet by mutableStateOf(false)
        private set

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            // FIX: We "collect" the Flow from the repository instead of using a listener
            repository.getGoals().collect { snapshot ->
                goals.clear()
                for (doc in snapshot.documents) {
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    goals.add(data)
                }
            }
        }
    }

    fun createNewGoal(title: String, hours: Int) {
        viewModelScope.launch {
            repository.addGoal(title, hours)
        }
    }

    fun startTimer(durationMinutes: Int) {
        timerJob?.cancel() // Reset any existing timer

        timeLeft = durationMinutes * 60L
        isTimerRunning = true

        timerJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            onTimerFinished()
        }
    }

    private fun onTimerFinished() {
        isTimerRunning = false
        showEnergySheet = true // Shows the pink mood check-in sheet
    }

    fun onSessionFinished() {
        showEnergySheet = true
    }

    fun completeSession(goalId: String, duration: Long, energy: EnergyLevel) {
        viewModelScope.launch {
            val session = FocusSession(
                goalId = goalId,
                durationMinutes = duration,
                energyLevel = energy
            )
            // repository.saveSession(session)
            showEnergySheet = false
        }
    }

    // This Factory is what prevents the crash during navigation
    companion object {
        fun provideFactory(repository: FocusRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FocusViewModel(repository) as T
            }
        }
    }
}