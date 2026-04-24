package com.praneet.clarity.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
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
import com.praneet.clarity.ui.screens.SettingsScreen
import com.praneet.clarity.viewmodel.FocusViewModel

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Focus : Screen("home", Icons.Default.Timer, "Focus")
    object Stats : Screen("stats", Icons.Default.BarChart, "Stats")
    object Rewards : Screen("rewards", Icons.Default.EmojiEvents, "Rewards")
    object Settings : Screen("settings", Icons.Default.Person, "Settings")
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
    val items = listOf(Screen.Focus, Screen.Stats, Screen.Rewards, Screen.Settings)

    // Hide bottom bar if timer is running for immersion
    val showBottomBar = !viewModel.isTimerRunning
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = surfaceColor,
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = surfaceColor,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(80.dp)
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        items.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = { 
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp, 32.dp)
                                                .clip(CircleShape)
                                                .background(primaryColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(screen.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                        }
                                    } else {
                                        Icon(screen.icon, contentDescription = null, tint = onSurface.copy(alpha = 0.4f))
                                    }
                                },
                                label = { 
                                    Text(
                                        screen.label,
                                        color = if (isSelected) primaryColor else onSurface.copy(alpha = 0.4f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ) 
                                },
                                selected = isSelected,
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
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = onSurface.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Focus.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Focus.route) {
                HomeScreen(
                    onLogout = onLogout,
                    focusViewModel = viewModel,
                    onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Rewards.route) {
                RewardsScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}
