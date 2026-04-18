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
fun RewardsScreen(viewModel: FocusViewModel) {
    // Colors from the image
    val darkBg = Color(0xFF0D0D0D)
    val cardBg = Color(0xFF1A1A1C)
    val accentBlue = Color(0xFF74A2FF)
    val accentPurple = Color(0xFFC084FC)
    val greyText = Color(0xFF8E8E93)

    // --- DYNAMIC DATA CALCULATION ---
    val totalMinutesFocused = viewModel.goals.sumOf { (it["currentMinutes"] as? Long ?: 0L).toInt() }
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
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👤", fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Clarity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.6f))
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
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "PTS",
                            color = accentBlue,
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
                            brush = Brush.horizontalGradient(listOf(accentPurple, Color.Transparent)),
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
                            color = Color(0xFF2D1B4E)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⭐", color = accentPurple)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(statusTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(statusDesc, color = greyText, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Main Reward Card (10-Day Streak Example - Practical logic would count consecutive days in session collection)
            item {
                Surface(
                    color = Color(0xFF1E222E),
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
                                color = accentBlue,
                                shape = CircleShape
                            ) {
                                Text(
                                    "MASTERED",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Focus Streak",
                                color = Color.White,
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
                                    color = accentBlue.copy(alpha = 0.2f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚡", color = accentBlue, fontSize = 20.sp)
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Text("+12 PTS / Min", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Centurion Milestone Card (Dynamic based on 100 hour goal)
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
                                color = accentPurple,
                                strokeWidth = 8.dp,
                                trackColor = Color.White.copy(alpha = 0.05f)
                            )
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = Color.Black
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⏱️", fontSize = 32.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Centurion", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${totalMinutesFocused / 60} / 100 Hours Focused", color = greyText, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("${(progress * 100).toInt()}% towards goal", color = accentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}
