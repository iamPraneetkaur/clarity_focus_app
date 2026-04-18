package com.praneet.clarity.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.data.model.EnergyLevel
import com.praneet.clarity.data.model.FocusSession
import com.praneet.clarity.data.repository.FocusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import com.google.firebase.firestore.FirebaseFirestore

class FocusViewModel(private val repository: FocusRepository) : ViewModel() {

    // --- GOAL STATE ---
    var goals = mutableStateListOf<Map<String, Any>>()
        private set
    private val firestore = FirebaseFirestore.getInstance()
    
    var initialSelectedMinutes by mutableStateOf(25)
        private set

    var selectedGoalId by mutableStateOf<String?>(null)
    var selectedGoalTitle by mutableStateOf<String?>(null)

    // --- TIMER STATE ---
    private var timerJob: Job? = null
    var timeLeft by mutableStateOf(25 * 60L)
    var isTimerRunning by mutableStateOf(false)

    // --- UI STATE ---
    var showEnergySheet by mutableStateOf(false)
        private set

    init {
        loadGoals()
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
                // Auto-select first goal if none selected
                if (selectedGoalId == null && goals.isNotEmpty()) {
                    selectGoal(goals[0]["id"] as String, goals[0]["title"] as String)
                }
            }
        }
    }

    fun selectGoal(id: String, title: String) {
        selectedGoalId = id
        selectedGoalTitle = title
    }

    fun createNewGoal(title: String, hours: Int) {
        viewModelScope.launch {
            repository.addGoal(title, hours)
        }
    }

    fun startTimer(durationMinutes: Int) {
        initialSelectedMinutes = durationMinutes
        timerJob?.cancel()

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

    fun stopTimer() {
        timerJob?.cancel()
        isTimerRunning = false
        timeLeft = initialSelectedMinutes * 60L
    }

    private fun onTimerFinished() {
        isTimerRunning = false
        showEnergySheet = true
    }

    fun completeSession(energyLevel: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val goalId = selectedGoalId ?: "general"
        val duration = initialSelectedMinutes

        val sessionData = hashMapOf(
            "userId" to userId,
            "goalId" to goalId,
            "duration" to duration,
            "energy" to energyLevel,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("sessions")
            .add(sessionData)
            .addOnSuccessListener {
                updateGoalProgress(goalId, duration)
                showEnergySheet = false
            }
    }

    private fun updateGoalProgress(goalId: String, addedMinutes: Int) {
        if (goalId == "general") return
        
        val goalRef = firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .collection("goals")
            .document(goalId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(goalRef)
            val currentMinutes = snapshot.getLong("currentMinutes") ?: 0L
            transaction.update(goalRef, "currentMinutes", currentMinutes + addedMinutes)
        }.addOnSuccessListener {
            loadGoals()
        }
    }

    companion object {
        fun provideFactory(repository: FocusRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FocusViewModel(repository) as T
            }
        }
    }
}
