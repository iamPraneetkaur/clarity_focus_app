package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.praneet.clarity.auth.FirebaseAuthManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {

            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = if (isLogin) "Login" else "Sign Up",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Button(
                    onClick = {
                        loading.value = true
                        if (isLogin) {
                            FirebaseAuthManager.loginUser(
                                email.value,
                                password.value,
                                onSuccess = { 
                                    loading.value = false
                                    onLoginSuccess() 
                                },
                                onError = { 
                                    loading.value = false
                                    errorMessage.value = it 
                                }
                            )
                        } else {
                            FirebaseAuthManager.signupUser(
                                email.value,
                                password.value,
                                onSuccess = { 
                                    loading.value = false
                                    onSignupSuccess() 
                                },
                                onError = { 
                                    loading.value = false
                                    errorMessage.value = it 
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !loading.value
                ) {
                    if (loading.value) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isLogin) "Login" else "Sign Up", fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    onClick = { isLogin = !isLogin },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        if (isLogin)
                            "Don't have an account? Sign Up"
                        else
                            "Already have an account? Login",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
