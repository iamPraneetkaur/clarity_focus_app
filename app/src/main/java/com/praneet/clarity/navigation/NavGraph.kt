package com.praneet.clarity.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.LoginScreen
import com.praneet.clarity.ui.OnboardingScreen
import com.praneet.clarity.ui.screens.HomeScreen
import com.praneet.clarity.viewmodel.FocusViewModel

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
            val repository = com.praneet.clarity.data.repository.FocusRepository()
            val factory = FocusViewModel.provideFactory(repository)
            val viewModel: FocusViewModel = viewModel(factory = factory)
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                focusViewModel = viewModel
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