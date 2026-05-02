package com.praneet.clarity.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

object FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signupUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d("FIREBASE_AUTH", "Signup success")
                    onSuccess()

                } else {

                    Log.e("FIREBASE_AUTH", "Signup failed", task.exception)
                    onError(task.exception?.message ?: "Signup failed")

                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Log.d("FIREBASE_AUTH", "Login success")
                    onSuccess()
                } else {
                    Log.e("FIREBASE_AUTH", "Login failed", task.exception)
                    onError(task.exception?.message ?: "Login failed")
                }

            }
    }

    fun updateUserName(name: String, onComplete: (Boolean) -> Unit) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
}
