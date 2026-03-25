package com.praneet.clarity.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

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
}