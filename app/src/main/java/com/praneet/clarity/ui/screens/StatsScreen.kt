package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.praneet.clarity.viewmodel.FocusViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: FocusViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val accentForest = MaterialTheme.colorScheme.primary
    val accentSage = MaterialTheme.colorScheme.secondary
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val onSurface = MaterialTheme.colorScheme.onSurface

    // --- DATA PROCESSING ---
    val sessions = viewModel.sessions
    val totalMinutes = sessions.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
    val totalSessions = sessions.size

    val focusHours = totalMinutes / 60
    val focusMins = totalMinutes % 60
    val focusTimeText = if (focusHours > 0) "${focusHours}h ${focusMins}m" else "${focusMins}m"

    // Weekly Chart Processing
    val weeklyData = remember(sessions) {
        val days = mutableListOf<Pair<String, Float>>()
        val dayNames = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        
        // Get totals for last 7 days
        val dailyTotals = mutableListOf<Long>()
        for (i in 6 downTo 0) {
            val checkCal = Calendar.getInstance()
            checkCal.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = checkCal.apply { 
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            
            val dayEnd = checkCal.apply { 
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val dayTotal = sessions.filter {
                val ts = it["timestamp"] as? Timestamp
                val time = ts?.toDate()?.time ?: 0L
                time in dayStart..dayEnd
            }.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
            
            dailyTotals.add(dayTotal)
            days.add(dayNames[checkCal.get(Calendar.DAY_OF_WEEK) - 1] to 0f)
        }

        val maxTotal = dailyTotals.maxOrNull()?.coerceAtLeast(1L) ?: 1L
        dailyTotals.mapIndexed { index, total ->
            days[index].first to (total.toFloat() / maxTotal.toFloat()).coerceIn(0.05f, 1f)
        }
    }

    val currentWeekRange = remember {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        val end = Calendar.getInstance()
        val start = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
        "${sdf.format(start.time)} - ${sdf.format(end.time)}".uppercase()
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            item {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        "Productivity",
                        color = onSurface,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Insights",
                        color = accentForest,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "You've focused for $focusTimeText across $totalSessions sessions.",
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
                                Text("Weekly Focus", color = onSurface, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Focus time per day", color = greyText, fontSize = 13.sp)
                            }
                            
                            Surface(
                                color = onSurface.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    currentWeekRange,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = accentForest,
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
                            weeklyData.forEachIndexed { index, (day, ratio) ->
                                val isToday = index == 6
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(onSurface.copy(alpha = 0.05f)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(ratio)
                                                .clip(CircleShape)
                                                .background(if (isToday) accentForest else accentForest.copy(alpha = 0.4f))
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        day,
                                        color = if (isToday) onSurface else greyText,
                                        fontSize = 10.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Deep Work Ratio Card (Bottom)
            item {
                // Realistic calculation: sum of durations > 20 mins / total sum
                val deepWorkMinutes = sessions.filter { (it["duration"] as? Number)?.toLong() ?: 0L >= 25 }.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
                val deepWorkRatio = if (totalMinutes > 0) deepWorkMinutes.toFloat() / totalMinutes.toFloat() else 0f
                val deepWorkPercentage = (deepWorkRatio * 100).toInt()

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
                            Text("Deep Work Ratio", color = onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(32.dp))
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { deepWorkRatio },
                                modifier = Modifier.fillMaxSize(),
                                color = accentSage,
                                strokeWidth = 14.dp,
                                trackColor = onSurface.copy(alpha = 0.05f),
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "$deepWorkPercentage%",
                                color = onSurface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Sessions of 25m+ are considered Deep Work",
                            color = greyText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}
