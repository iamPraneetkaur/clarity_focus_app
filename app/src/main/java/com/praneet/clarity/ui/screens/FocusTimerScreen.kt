package com.praneet.clarity.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.praneet.clarity.viewmodel.FocusViewModel

@Composable
fun FocusTimerScreen(
    viewModel: FocusViewModel,
    onCancel: () -> Unit
) {
    val totalSeconds = viewModel.timeLeft
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    // Calculate progress: current time / initial time
    val progress = if (viewModel.initialSelectedMinutes > 0) {
        totalSeconds.toFloat() / (viewModel.initialSelectedMinutes * 60f)
    } else 1f


    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Stay Focused ✨",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = onSurface
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Circular Countdown Timer
        Box(contentAlignment = Alignment.Center) {
            // Background Circle (Track)
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(280.dp),
                color = secondaryColor.copy(alpha = 0.2f),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            // Animated Progress Circle
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(280.dp),
                color = primaryColor,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            // Time Display
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Cancel Button
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            modifier = Modifier.width(200.dp).height(56.dp)
        ) {
            Text("Cancel Session", color = onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
        }
    }
}
