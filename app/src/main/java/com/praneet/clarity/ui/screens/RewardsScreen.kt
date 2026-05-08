package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praneet.clarity.viewmodel.FocusViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RewardsScreen(
    viewModel: FocusViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    val accentForest = Color(0xFF3E4E35)
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val onSurface = MaterialTheme.colorScheme.onSurface

    val totalMinutesFocused = viewModel.sessions.sumOf { (it["duration"] as? Number)?.toLong() ?: 0L }
    val pointsEarned = totalMinutesFocused * 12
    val formattedPoints = remember(pointsEarned) {
        NumberFormat.getNumberInstance(Locale.US).format(pointsEarned)
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
                "ACHIEVEMENTS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = greyText,
                letterSpacing = 1.sp
            )
            Text(
                "Your Rewards",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        // Points Summary Card
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
                        .padding(32.dp)
                ) {
                    Column {
                        Text(
                            "TOTAL POINTS",
                            color = accentForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                formattedPoints,
                                color = accentForest,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "PTS",
                                color = accentForest.copy(alpha = 0.6f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Milestone Section
        item {
            Text(
                "MILESTONES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = greyText,
                letterSpacing = 1.sp
            )
        }

        item {
            MilestoneCard(
                title = "Rising Star",
                description = "Focus for 10 hours to reach the next level.",
                icon = Icons.Default.Star,
                progress = (totalMinutesFocused.toFloat() / 600f).coerceIn(0f, 1f),
                color = Color(0xFFF7E7EB)
            )
        }

        item {
            MilestoneCard(
                title = "Focus Master",
                description = "Complete 50 sessions of deep focus.",
                icon = Icons.Default.EmojiEvents,
                progress = (viewModel.sessions.size.toFloat() / 50f).coerceIn(0f, 1f),
                color = Color(0xFFE8F0E5)
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun MilestoneCard(
    title: String,
    description: String,
    icon: ImageVector,
    progress: Float,
    color: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = color,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(description, color = Color.Gray, fontSize = 13.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Color(0xFF3E4E35),
                trackColor = Color.LightGray.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "${(progress * 100).toInt()}% completed",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E4E35)
            )
        }
    }
}
