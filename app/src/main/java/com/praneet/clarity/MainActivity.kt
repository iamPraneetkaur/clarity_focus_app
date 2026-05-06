package com.praneet.clarity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.praneet.clarity.navigation.AppNavGraph
import com.praneet.clarity.ui.theme.ClarityTheme
import com.praneet.clarity.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(applicationContext)
            )
            val themeStyle by settingsViewModel.themeStyle

            ClarityTheme(themeStyle = themeStyle) {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        // Permission granted! Notifications will work.
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                AppNavGraph(settingsViewModel = settingsViewModel)
            }
        }
    }
}
