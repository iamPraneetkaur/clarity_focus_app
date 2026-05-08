package com.praneet.clarity.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.praneet.clarity.ui.components.LocalAddGoalDialog
import com.praneet.clarity.viewmodel.FocusViewModel

@Composable
fun GoalsScreen(
    viewModel: FocusViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "YOUR JOURNEY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Focus Goals",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { showAddGoalDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E4E35))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Goal", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(viewModel.goals) { goalMap ->
                val id = goalMap["id"] as? String ?: return@items
                val title = goalMap["title"] as? String ?: "Unnamed"
                val currentMins = (goalMap["currentMinutes"] as? Long ?: 0L).toInt()
                val targetMins = goalMap["targetMinutes"] as? Int ?: 60

                GoalJourneyCard(
                    title = title,
                    currentMins = currentMins,
                    targetMins = targetMins,
                    onEdit = { /* TODO: Implement Edit */ },
                    onDelete = { goalToDelete = id to title }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuoteCard()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showAddGoalDialog) {
            LocalAddGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onConfirm = { title, targetMins ->
                    viewModel.createNewGoal(title, targetMins)
                    showAddGoalDialog = false
                }
            )
        }

        goalToDelete?.let { (id, title) ->
            AlertDialog(
                onDismissRequest = { goalToDelete = null },
                title = { Text("Remove Goal") },
                text = { Text("Are you sure you want to remove '$title'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteGoal(id)
                            goalToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.onError)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { goalToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun GoalJourneyCard(
    title: String,
    currentMins: Int,
    targetMins: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (targetMins > 0) currentMins.toFloat() / targetMins.toFloat() else 0f
    val percentage = (progress * 100).toInt()
    
    val hoursSpent = currentMins / 60
    val minutesSpent = currentMins % 60
    val timeSpentText = if (hoursSpent > 0) "${hoursSpent}h ${minutesSpent}m spent" else "${minutesSpent}m spent"

    // Mocking subtitle and icon based on title
    val (icon, color, subtitle) = when {
        title.contains("Read", ignoreCase = true) -> Triple(Icons.Default.Book, Color(0xFFE8F0E5), "Daily progress")
        title.contains("Meditation", ignoreCase = true) -> Triple(Icons.Default.SelfImprovement, Color(0xFFF7E7EB), "Weekly target")
        else -> Triple(Icons.Default.Code, Color(0xFFE8EFF0), "Project milestone")
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, fontSize = 14.sp, color = Color.Gray)
                }
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(timeSpentText, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("$percentage%", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (title.contains("Meditation")) Color(0xFFD4A5A5) else if (title.contains("Read")) Color(0xFFA3B18A) else Color(0xFF4A4A4A),
                trackColor = Color.LightGray.copy(alpha = 0.2f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
fun QuoteCard() {
    val quoteGradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
    )
    
    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        Box {
            // Using a colored surface with a leaf icon overlay to mock the image background
            Surface(
                color = Color(0xFFA3B18A).copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
            
            Box(
                modifier = Modifier.fillMaxSize().background(quoteGradient)
            )
            
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    "Nurture your focus.",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Consistency is the bridge between goals and accomplishment.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
