package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praneet.clarity.viewmodel.FocusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: FocusViewModel) {
    val darkBg = Color(0xFF0D0D0D)
    val cardBg = Color(0xFF1A1A1C)
    val accentBlue = Color(0xFF74A2FF)
    val accentPurple = Color(0xFFC084FC)
    val greyText = Color(0xFF8E8E93)

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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            item {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        "Productivity",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Insights",
                        color = accentBlue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your weekly momentum is up by 12%.",
                        color = greyText,
                        fontSize = 16.sp
                    )
                }
            }

            // Weekly Focus Card
            item {
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("Weekly Focus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Deep work hours per day", color = greyText, fontSize = 13.sp)
                            }
                            
                            Surface(
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    "MARCH 12 - 18",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = accentBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(48.dp))

                        // Chart with even spacing and track backgrounds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                            val data = listOf(0.4f, 0.6f, 1.0f, 0.5f, 0.7f, 0.15f, 0.1f)
                            
                            days.forEachIndexed { index, day ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(data[index])
                                                .clip(CircleShape)
                                                .background(if (index == 2) accentBlue else accentBlue.copy(alpha = 0.3f))
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        day,
                                        color = if (index == 2) Color.White else greyText,
                                        fontSize = 10.sp,
                                        fontWeight = if (index == 2) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Deep Work Ratio Card (Bottom)
            item {
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Deep Work Ratio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(32.dp))
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier.fillMaxSize(),
                                color = accentPurple,
                                strokeWidth = 14.dp,
                                trackColor = Color.White.copy(alpha = 0.05f),
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "65%",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}
