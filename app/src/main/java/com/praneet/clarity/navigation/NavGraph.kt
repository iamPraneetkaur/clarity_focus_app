package com.praneet.clarity.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.LoginScreen
import com.praneet.clarity.ui.OnboardingScreen
import com.praneet.clarity.ui.screens.HomeScreen
import com.praneet.clarity.ui.screens.StatsScreen
import com.praneet.clarity.ui.screens.RewardsScreen
import com.praneet.clarity.viewmodel.FocusViewModel

sealed class Screen(val route: String, val icon: String, val label: String) {
    object Home : Screen("home", "🏠", "Home")
    object Stats : Screen("stats", "📈", "Stats")
    object Rewards : Screen("rewards", "🌸", "Garden")
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val repository = com.praneet.clarity.data.repository.FocusRepository()
    val factory = FocusViewModel.provideFactory(repository)
    val viewModel: FocusViewModel = viewModel(factory = factory)

    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "main_content" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("main_content") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        
        composable("onboarding") {
            OnboardingScreen(onContinue = {
                navController.navigate("main_content") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable("main_content") {
            MainScaffold(viewModel, onLogout = {
                navController.navigate("login") {
                    popUpTo("main_content") { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun MainScaffold(viewModel: FocusViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Stats, Screen.Rewards)

    // Hide bottom bar if timer is running for immersion
    val showBottomBar = !viewModel.isTimerRunning

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFFF8FAF9),
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Text(screen.icon, fontSize = 20.sp) },
                            label = { Text(screen.label, fontSize = 12.sp) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFF472B6),
                                selectedTextColor = Color(0xFFF472B6),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFFFCE4EC)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onLogout = onLogout,
                    focusViewModel = viewModel,
                    onNavigateToStats = { navController.navigate(Screen.Stats.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
            composable(Screen.Rewards.route) {
                RewardsScreen(viewModel = viewModel)
            }
        }
    }
}
