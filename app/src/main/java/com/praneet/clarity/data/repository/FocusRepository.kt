package com.praneet.clarity.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.praneet.clarity.data.model.FocusSession
import kotlinx.coroutines.tasks.await

class FocusRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val goalsCollection 
        get() = firestore.collection("users").document(userId).collection("goals")
    
    private val sessionsCollection = firestore.collection("sessions")

    suspend fun addGoal(title: String, targetMinutes: Int) {
        if (userId.isEmpty()) return
        val goal = hashMapOf(
            "title" to title,
            "targetMinutes" to targetMinutes,
            "currentMinutes" to 0L,
            "createdAt" to System.currentTimeMillis()
        )
        goalsCollection.add(goal).await()
    }

    fun getGoals() = if (userId.isEmpty()) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("empty").snapshots()
    } else {
        goalsCollection.orderBy("createdAt").snapshots()
    }

    suspend fun saveSession(session: FocusSession) {
        try {
            sessionsCollection.add(session).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    fun getRecentSessions(limit: Int = 10) = sessionsCollection
        .whereEqualTo("userId", userId)
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(limit.toLong())
        .snapshots()

    fun getAllSessions() = if (userId.isEmpty()) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("empty").snapshots()
    } else {
        sessionsCollection
            .whereEqualTo("userId", userId)
            .snapshots()
    }
}
