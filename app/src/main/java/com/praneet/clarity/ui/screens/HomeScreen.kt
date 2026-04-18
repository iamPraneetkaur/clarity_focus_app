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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    focusViewModel: FocusViewModel,
    onNavigateToStats: () -> Unit = {}
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
                            IconButton(onClick = onNavigateToStats) {
                                Text("📈", fontSize = 20.sp)
                            }
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
                        
                        // --- 1. TIME AWARENESS CARD ---
                        TimeAwarenessCard(accentPink, softRose)

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- 2. TIME PICKER ---
                        Text(
                            "Focus Duration ☁️", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = deepText.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val timeOptions = listOf(5, 15, 25, 45, 60)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(timeOptions) { mins ->
                                FilterChip(
                                    selected = focusViewModel.initialSelectedMinutes == mins,
                                    onClick = { 
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

                        // --- 3. GOALS SECTION ---
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
                                    status = "${current}m / ${target}m",
                                    accent = if (isSelected) accentPink else Color(0xFFBAE6FD),
                                    isSelected = isSelected,
                                    onClick = { focusViewModel.selectGoal(id, title) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // --- 4. START BUTTON ---
                        Button(
                            onClick = {
                                if (focusViewModel.selectedGoalId != null) {
                                    focusViewModel.startTimer(focusViewModel.initialSelectedMinutes)
                                    AlarmHelper.scheduleAlarm(context, focusViewModel.initialSelectedMinutes)
                                }
                            },
                            enabled = focusViewModel.selectedGoalId != null,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentPink,
                                disabledContainerColor = accentPink.copy(alpha = 0.5f)
                            ),
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- 5. DAILY STATS (Bottom) ---
                        DailyStatsRow(deepText, accentPink)

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (showAddGoalDialog) {
                        LocalAddGoalDialog(
                            onDismiss = { showAddGoalDialog = false },
                            onConfirm = { title, targetMins ->
                                focusViewModel.createNewGoal(title, targetMins)
                                showAddGoalDialog = false
                            }
                        )
                    }

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
fun TimeAwarenessCard(accent: Color, bg: Color) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val remainingHours = 24 - hour
    val progress = (hour.toFloat() / 24f)

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    color = accent,
                    trackColor = bg,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Today is ${(progress * 100).toInt()}% complete", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$remainingHours hours left to make an impact ✨", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DailyStatsRow(textCol: Color, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CuteStatItem("2h 15m", "Focus", accent, textCol)
        CuteStatItem("6", "Sessions", Color(0xFF7DD3FC), textCol)
        CuteStatItem("🔥 3", "Streak", Color(0xFF4ADE80), textCol)
    }
}

@Composable
fun CuteStatItem(value: String, label: String, color: Color, textCol: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(label, color = textCol.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
