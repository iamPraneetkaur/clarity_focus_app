package com.praneet.clarity.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.data.repository.FocusRepository
import com.praneet.clarity.ui.screens.LoginScreen
import com.praneet.clarity.ui.screens.OnboardingScreen
import com.praneet.clarity.ui.screens.HomeScreen
import com.praneet.clarity.ui.screens.StatsScreen
import com.praneet.clarity.ui.screens.GoalsScreen
import com.praneet.clarity.ui.screens.RewardsScreen
import com.praneet.clarity.ui.screens.SettingsScreen
import com.praneet.clarity.viewmodel.FocusViewModel
import com.praneet.clarity.viewmodel.SettingsViewModel

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Focus : Screen("home", Icons.Default.Timer, "Home")
    object Goals : Screen("goals", Icons.Default.Timeline, "Goals")
    object Stats : Screen("stats", Icons.Default.BarChart, "Sessions")
    object Rewards : Screen("rewards", Icons.Default.EmojiEvents, "Rewards")
    object Settings : Screen("settings", Icons.Default.Person, "Profile")
}

@Composable
fun AppNavGraph(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    val startDestination = when {
        currentUser == null -> "login"
        currentUser.displayName.isNullOrEmpty() -> "onboarding"
        else -> "main_content"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main_content") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignupSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("onboarding") {
            OnboardingScreen(onComplete = {
                navController.navigate("main_content") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable("main_content") {
            val context = LocalContext.current.applicationContext
            val repository = FocusRepository()
            val factory = FocusViewModel.provideFactory(repository, context)
            val viewModel: FocusViewModel = viewModel(factory = factory)

            MainScaffold(
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main_content") { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: FocusViewModel,
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    // Removed Screen.Settings from bottom bar items as per user request
    val items = listOf(Screen.Focus, Screen.Goals, Screen.Stats, Screen.Rewards)

    // Hide top and bottom bars if timer is running for immersion
    val showBars = !viewModel.isTimerRunning
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface

    Scaffold(
        topBar = {
            if (showBars) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                if (currentRoute == Screen.Settings.route) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                            }
                        },
                        title = {
                            Text("Settings", color = onSurface, fontWeight = FontWeight.Bold)
                        },
                        actions = {
                            Text(
                                "CLARITY",
                                modifier = Modifier.padding(end = 16.dp),
                                color = onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = "Logo",
                                tint = Color(0xFF5E7153),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Clarity",
                                color = onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }

                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (showBars) {
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
            composable(Screen.Goals.route) {
                GoalsScreen(
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
                    onLogout = onLogout,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
