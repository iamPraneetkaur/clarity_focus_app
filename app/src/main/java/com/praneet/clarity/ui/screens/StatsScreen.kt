package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.praneet.clarity.viewmodel.FocusViewModel
import java.util.*

@Composable
fun StatsScreen(
    viewModel: FocusViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    val accentForest = Color(0xFF3E4E35)
    val accentSage = Color(0xFFA3B18A)
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val onSurface = MaterialTheme.colorScheme.onSurface

    // --- DATA PROCESSING ---
    val sessions = viewModel.sessions
    val totalMinutes = sessions.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
    val totalSessions = sessions.size

    val focusHours = totalMinutes / 60
    val focusMins = totalMinutes % 60
    val focusTimeText = if (focusHours > 0) "${focusHours}h ${focusMins}m" else "${focusMins}m"

    val weeklyData = remember(sessions) {
        val dayNames = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        val totals = mutableListOf<Long>()
        val labels = mutableListOf<String>()
        
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = cal.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
            val dayEnd = cal.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.timeInMillis

            val dayTotal = sessions.filter {
                val ts = it["timestamp"] as? Timestamp
                val time = ts?.toDate()?.time ?: 0L
                time in dayStart..dayEnd
            }.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
            
            totals.add(dayTotal)
            labels.add(dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1])
        }

        val max = totals.maxOrNull()?.coerceAtLeast(1L) ?: 1L
        totals.mapIndexed { index, t -> labels[index] to (t.toFloat() / max.toFloat()).coerceAtLeast(0.1f) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "PERFORMANCE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = greyText,
                letterSpacing = 1.sp
            )
            Text(
                "Focus Insights",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Great job! You've focused for $focusTimeText this week.",
                color = greyText,
                fontSize = 15.sp
            )
        }

        // Weekly Chart Card
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Weekly Progress", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Surface(
                            color = accentSage.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "LAST 7 DAYS",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentForest
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyData.forEach { (day, ratio) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
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
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(accentSage, accentForest)
                                                )
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(day, fontSize = 10.sp, color = greyText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Total Sessions",
                    value = totalSessions.toString(),
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFFF7E7EB)
                )
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Avg. Focus",
                    value = if (totalSessions > 0) "${totalMinutes / totalSessions}m" else "0m",
                    icon = Icons.Default.BarChart,
                    color = Color(0xFFE8F0E5)
                )
            }
        }

        // Summary Card with Gradient
        item {
            Surface(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFB8C69F), Color(0xFFA3B18A))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            "Deep Work Ratio",
                            color = accentForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val deepWorkMins = sessions.filter { (it["duration"] as? Number)?.toLong() ?: 0L >= 25 }.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
                        val ratio = if (totalMinutes > 0) deepWorkMins.toFloat() / totalMinutes.toFloat() else 0f
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${(ratio * 100).toInt()}%",
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black,
                                color = accentForest
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "Your sessions are primarily high-intensity focus periods.",
                                fontSize = 14.sp,
                                color = accentForest.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun StatMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 1.dp
    ) {
        Column(Modifier.padding(20.dp)) {
            Surface(
                color = color,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 13.sp, color = Color.Gray)
        }
    }
}
