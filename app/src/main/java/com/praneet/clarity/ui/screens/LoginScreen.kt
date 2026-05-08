package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F0E5), Color(0xFFCEDBC8))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Logo Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color(0xFF3E4E35),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Clarity",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3E4E35)
                )
                Text(
                    "Master your focus",
                    fontSize = 16.sp,
                    color = Color(0xFF3E4E35).copy(alpha = 0.6f)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = if (isLogin) "Welcome Back" else "Create Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E4E35)
                    )

                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3E4E35),
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedLabelColor = Color(0xFF3E4E35)
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
                            focusedBorderColor = Color(0xFF3E4E35),
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedLabelColor = Color(0xFF3E4E35)
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
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3E4E35)
                        ),
                        enabled = !loading.value
                    ) {
                        if (loading.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (isLogin) "Login" else "Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (errorMessage.value.isNotEmpty()) {
                        Text(
                            text = errorMessage.value,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    TextButton(
                        onClick = { isLogin = !isLogin },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                            color = Color(0xFF3E4E35),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
