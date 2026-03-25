package com.praneet.clarity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.praneet.clarity.auth.FirebaseAuthManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {

                        if (isLogin) {
                            FirebaseAuthManager.loginUser(
                                email.value,
                                password.value,
                                onSuccess = { onLoginSuccess() },
                                onError = { errorMessage.value = it }
                            )

                        } else {
                            FirebaseAuthManager.signupUser(
                                email.value,
                                password.value,
                                onSuccess = { onLoginSuccess() },
                                onError = { errorMessage.value = it }
                            )
                        }
                    }
                ) {
                    Text(if (isLogin) "Login" else "Sign Up")
                }
                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = Color.Red
                    )
                }
                TextButton(
                    onClick = { isLogin = !isLogin }
                ) {
                    Text(
                        if (isLogin)
                            "Don't have an account? Sign Up"
                        else
                            "Already have an account? Login"
                    )
                }
            }
        }
    }
}