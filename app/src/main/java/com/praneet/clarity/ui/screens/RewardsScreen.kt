package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praneet.clarity.viewmodel.FocusViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    viewModel: FocusViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    // Colors from the image
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val accentForest = MaterialTheme.colorScheme.primary
    val accentSage = MaterialTheme.colorScheme.secondary
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val onSurface = MaterialTheme.colorScheme.onSurface

    // --- DYNAMIC DATA CALCULATION ---
    val totalMinutesFocused = viewModel.sessions.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
    val pointsEarned = totalMinutesFocused * 12 // 12 PTS per focused minute
    val formattedPoints = remember(pointsEarned) {
        NumberFormat.getNumberInstance(Locale.US).format(pointsEarned)
    }
    
    // Status Logic
    val statusTitle = when {
        pointsEarned > 10000 -> "Legendary Status"
        pointsEarned > 5000 -> "Elite Status"
        else -> "Rising Focus"
    }
    val statusDesc = when {
        pointsEarned > 10000 -> "Top 2% of focused minds"
        pointsEarned > 5000 -> "Master of deep work"
        else -> "Starting your focus journey"
    }

    Scaffold(
        containerColor = darkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = onSurface.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👤", fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Clarity", color = onSurface, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = onSurface.copy(alpha = 0.6f))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Accumulated Wealth Section
            item {
                Column(Modifier.padding(top = 16.dp)) {
                    Text(
                        "ACCUMULATED WEALTH",
                        color = greyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            formattedPoints,
                            color = onSurface,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "PTS",
                            color = accentForest,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // Legendary/Dynamic Status Card
            item {
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(accentSage, Color.Transparent)),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = accentSage.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⭐", color = accentSage)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(statusTitle, color = onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(statusDesc, color = greyText, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Main Reward Card
            item {
                Surface(
                    color = accentForest.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(Modifier.padding(24.dp)) {
                        // Background watermark-ish icon
                        Text(
                            "⚡",
                            fontSize = 120.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 20.dp, y = 20.dp)
                                .alpha(0.05f)
                        )

                        Column {
                            Surface(
                                color = accentForest,
                                shape = CircleShape
                            ) {
                                Text(
                                    "MASTERED",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Focus Streak",
                                color = onSurface,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "You have completed $totalMinutesFocused minutes of\ndeep work in total.",
                                color = greyText,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(32.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = accentForest.copy(alpha = 0.2f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚡", color = accentForest, fontSize = 20.sp)
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Text("+12 PTS / Min", color = onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Centurion Milestone Card
            item {
                val targetMinutes = 100 * 60 // 100 Hours
                val progress = (totalMinutesFocused.toFloat() / targetMinutes).coerceIn(0f, 1f)
                
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(120.dp),
                                color = accentSage,
                                strokeWidth = 8.dp,
                                trackColor = onSurface.copy(alpha = 0.05f)
                            )
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = onSurface.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⏱️", fontSize = 32.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Centurion", color = onSurface, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${totalMinutesFocused / 60} / 100 Hours Focused", color = greyText, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("${(progress * 100).toInt()}% towards goal", color = accentSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}
