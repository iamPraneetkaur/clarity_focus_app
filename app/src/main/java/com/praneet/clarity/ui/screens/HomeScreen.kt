package com.praneet.clarity.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.components.EnergyCheckSheet
import com.praneet.clarity.viewmodel.FocusViewModel
import com.praneet.clarity.utils.AlarmHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    focusViewModel: FocusViewModel
) {
    val context = LocalContext.current
    var showAddGoalDialog by remember { mutableStateOf(false) }
    
    // 🎨 Soft Minimalism Palette
    val offWhite = Color(0xFFF8FAF9)
    val softRose = Color(0xFFFCE4EC)
    val deepText = Color(0xFF4A4A4A)
    val accentPink = Color(0xFFF472B6)

    // THE SWITCH: Toggles between Dashboard and Immersive Timer
    AnimatedContent(
        targetState = focusViewModel.isTimerRunning,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ScreenSwitch"
    ) { timerRunning ->
        if (timerRunning) {
            FocusTimerScreen(
                viewModel = focusViewModel,
                onCancel = { 
                    focusViewModel.stopTimer()
                    AlarmHelper.cancelAlarm(context)
                }
            )
        } else {
            Scaffold(
                containerColor = offWhite,
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        title = { 
                            Text(
                                "Clarity ✨", 
                                color = deepText, 
                                fontWeight = FontWeight.SemiBold, 
                                fontSize = 20.sp
                            ) 
                        },
                        actions = {
                            TextButton(onClick = {
                                FirebaseAuth.getInstance().signOut()
                                onLogout()
                            }) {
                                Text(
                                    "Exit", 
                                    color = accentPink, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    // Decorative Background Emoji
                    Text(
                        "🌸", 
                        fontSize = 80.sp, 
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 20.dp, y = (-20).dp)
                            .alpha(0.3f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Hi, Focus Friend! ✨", 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = deepText
                        )
                        Text(
                            "Ready for some deep work?", 
                            fontSize = 14.sp, 
                            color = deepText.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // TIME PICKER
                        Text(
                            "Pick your focus time ☁️", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = deepText.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val timeOptions = listOf(5, 10, 15, 25, 45, 60)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(timeOptions) { mins ->
                                FilterChip(
                                    selected = focusViewModel.initialSelectedMinutes == mins,
                                    onClick = { 
                                        // Update state without starting the timer yet
                                        focusViewModel.startTimer(mins)
                                        focusViewModel.stopTimer()
                                    },
                                    label = { Text("${mins}m") },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = softRose,
                                        selectedLabelColor = accentPink
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Select a Goal 🎀", 
                                color = deepText, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { showAddGoalDialog = true }) {
                                Text("➕", fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // List of Goals
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            focusViewModel.goals.forEach { goalMap ->
                                val id = goalMap["id"] as String
                                val title = goalMap["title"] as? String ?: "Unnamed"
                                val target = (goalMap["targetMinutes"] as? Number)?.toInt() ?: 60
                                val current = (goalMap["currentMinutes"] as? Number)?.toLong() ?: 0L
                                val progress = if (target > 0) (current.toFloat() / (target)) else 0f
                                val isSelected = focusViewModel.selectedGoalId == id

                                CuteGoalCard(
                                    title = title,
                                    progress = progress,
                                    status = if (progress >= 1f) "Done!" else "${(progress * 100).toInt()}%",
                                    accent = if (isSelected) accentPink else Color(0xFFBAE6FD),
                                    isSelected = isSelected,
                                    onClick = { focusViewModel.selectGoal(id, title) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // START BUTTON
                        Button(
                            onClick = {
                                focusViewModel.startTimer(focusViewModel.initialSelectedMinutes)
                                AlarmHelper.scheduleAlarm(context, focusViewModel.initialSelectedMinutes)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = accentPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                "Start Focus Session ☁️", 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Dialog for adding goals
                    if (showAddGoalDialog) {
                        LocalAddGoalDialog(
                            onDismiss = { showAddGoalDialog = false },
                            onConfirm = { title, targetMins ->
                                focusViewModel.createNewGoal(title, targetMins)
                                showAddGoalDialog = false
                            }
                        )
                    }

                    // Bottom sheet for energy level
                    if (focusViewModel.showEnergySheet) {
                        EnergyCheckSheet(
                            onDismiss = { energyLevel ->
                                focusViewModel.completeSession(energyLevel.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CuteGoalCard(
    title: String, 
    progress: Float, 
    status: String, 
    accent: Color, 
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, accent) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title, 
                    color = Color(0xFF4A4A4A), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 15.sp
                )
                if (isSelected) Text(" ✨", fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    status, 
                    color = Color(0xFF4A4A4A).copy(alpha = 0.4f), 
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                color = accent,
                trackColor = accent.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun LocalAddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { 
            Button(onClick = { onConfirm(title, minutesText.toIntOrNull() ?: 60) }) { 
                Text("Add") 
            } 
        },
        title = { Text("New Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Goal Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = minutesText, 
                    onValueChange = { minutesText = it }, 
                    label = { Text("Target Minutes") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
