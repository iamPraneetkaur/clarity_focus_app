package com.praneet.clarity.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praneet.clarity.viewmodel.FocusViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FocusTimerScreen(
    viewModel: FocusViewModel,
    onCancel: () -> Unit
) {
    val totalSeconds = viewModel.timeLeft
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F0E5), Color(0xFFCEDBC8))
    )

    val estimatedEnd = remember(viewModel.timeLeft) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, viewModel.timeLeft.toInt())
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Active Session Badge
            Surface(
                color = Color(0xFFA3B18A).copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFA3B18A).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.RadioButtonChecked,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF3E4E35)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ACTIVE SESSION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E4E35),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = viewModel.selectedGoalTitle ?: "Deep Work",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3E4E35)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Large White Timer Circle
            Surface(
                modifier = Modifier.size(300.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF3E4E35)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Focus Mode Active",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimerStatItem(label = "ESTIMATED END", value = estimatedEnd)
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                TimerStatItem(label = "BLOCK TYPE", value = "Pomodoro")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pause Button
                OutlinedButton(
                    onClick = {
                        if (viewModel.isPaused) viewModel.resumeTimer() else viewModel.pauseTimer()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, Color(0xFF3E4E35).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3E4E35))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (viewModel.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (viewModel.isPaused) "Resume" else "Pause", fontWeight = FontWeight.Bold)
                    }
                }

                // End Session Button
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3E4E35),
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("End Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TimerStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3E4E35)
        )
    }
}
