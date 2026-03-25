package com.praneet.clarity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.LoginScreen
import com.praneet.clarity.ui.OnboardingScreen
import com.praneet.clarity.ui.home.HomeScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    val startDestination = "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding"){
            OnboardingScreen(
                onContinue = {
                    navController.navigate("home"){
                        popUpTo("onboarding") {inclusive=true}
                    }
                }
            )
        }
    }
}