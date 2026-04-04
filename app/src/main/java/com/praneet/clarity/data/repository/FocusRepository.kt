package com.praneet.clarity.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.praneet.clarity.data.model.FocusSession
import kotlinx.coroutines.tasks.await

class FocusRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val goalsCollection = firestore.collection("users").document(userId).collection("goals")
    suspend fun addGoal(title: String, targetMinutes: Int) {
        val goal = hashMapOf(
            "title" to title,
            "targetMinutes" to targetMinutes,
            "currentMinutes" to 0L,
            "createdAt" to System.currentTimeMillis()
        )
        goalsCollection.add(goal).await()
    }
    fun getGoals() = goalsCollection.orderBy("createdAt").snapshots()

    private val sessionsCollection = firestore.collection("sessions")

    suspend fun saveSession(session: FocusSession) {
        try {
            sessionsCollection.add(session).await()
        } catch (e: Exception) {

        }
    }

}