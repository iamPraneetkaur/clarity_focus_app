package com.praneet.clarity.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.components.EnergyCheckSheet
import com.praneet.clarity.viewmodel.FocusViewModel
import com.praneet.clarity.utils.AlarmHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    focusViewModel: FocusViewModel,
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    var showAddGoalDialog by remember { mutableStateOf(false) }
    
    // 🎨 Use Palette from Theme
    val bgGreen = MaterialTheme.colorScheme.background
    val darkGreen = MaterialTheme.colorScheme.primary
    val lightGreen = MaterialTheme.colorScheme.secondary
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Alex"
    
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hourOfDay) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val dateString = dateFormat.format(Date()).uppercase()

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
                containerColor = bgGreen,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Image Placeholder
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.LightGray
                        ) {
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = "Profile",
                                modifier = Modifier.padding(8.dp),
                                tint = Color.Gray
                            )
                        }
                        
                        Text(
                            "Clarity", 
                            color = darkGreen, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 20.sp
                        )
                        
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                Icons.Outlined.Settings, 
                                contentDescription = "Settings", 
                                tint = darkGreen
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Greeting
                    Text(
                        text = "$greeting, $userName",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )
                    Text(
                        text = dateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = greyText,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // --- 1. TIME AWARENESS CARD ---
                    TimeAwarenessCard(darkGreen, lightGreen)

                    Spacer(modifier = Modifier.height(40.dp))

                    // --- 2. GOALS SECTION ---
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.SpaceBetween, 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your Goals", 
                            color = darkGreen, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 22.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(lightGreen)
                                .clickable { showAddGoalDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = darkGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        focusViewModel.goals.forEach { goalMap ->
                            val id = goalMap["id"] as String
                            val title = goalMap["title"] as? String ?: "Unnamed"
                            val isSelected = focusViewModel.selectedGoalId == id

                            GoalChip(
                                title = title,
                                isSelected = isSelected,
                                onClick = { focusViewModel.selectGoal(id, title) },
                                darkGreen = darkGreen,
                                lightGrey = Color(0xFFF1F1F1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- 3. DURATION SECTION ---
                    Text(
                        "DURATION", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = greyText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val timeOptions = listOf(25, 45, 60)
                        timeOptions.forEach { mins ->
                            DurationCircle(
                                mins = mins,
                                isSelected = focusViewModel.initialSelectedMinutes == mins,
                                onClick = { 
                                    focusViewModel.startTimer(mins)
                                    focusViewModel.stopTimer()
                                },
                                darkGreen = darkGreen,
                                lightGreen = lightGreen
                            )
                        }
                        
                        // Edit Icon Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F1F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Custom Duration", tint = greyText, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // --- 4. START BUTTON ---
                    Button(
                        onClick = {
                            if (focusViewModel.selectedGoalId != null) {
                                focusViewModel.startTimer(focusViewModel.initialSelectedMinutes)
                                AlarmHelper.scheduleAlarm(context, focusViewModel.initialSelectedMinutes)
                            }
                        },
                        enabled = focusViewModel.selectedGoalId != null,
                        shape = RoundedCornerShape(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkGreen,
                            disabledContainerColor = darkGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Start Focus Session", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // --- 5. DAILY STATS (Bottom) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(Icons.Default.Schedule, "Focus:", "2h 15m", darkGreen)
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.LightGray)
                        StatItem(Icons.Default.CheckCircle, "Completed:", "3", darkGreen)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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
fun TimeAwarenessCard(darkGreen: Color, lightGreen: Color) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val remainingHours = 24 - hour
    val progress = (hour.toFloat() / 24f)

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(48.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$remainingHours hours",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen,
                    lineHeight = 48.sp
                )
                Text(
                    text = "remain in your\nday",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkGreen.copy(alpha = 0.7f),
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Make the most of\nyour time today",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(100.dp),
                    color = darkGreen,
                    trackColor = lightGreen.copy(alpha = 0.3f),
                    strokeWidth = 12.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )
            }
        }
    }
}

@Composable
fun GoalChip(title: String, isSelected: Boolean, onClick: () -> Unit, darkGreen: Color, lightGrey: Color) {
    val icon = when (title.uppercase()) {
        "STUDY" -> Icons.Default.Book
        "WORKOUT" -> Icons.Default.FitnessCenter
        "CODING" -> Icons.Default.Code
        else -> Icons.Default.Flag
    }
    
    Surface(
        onClick = onClick,
        color = if (isSelected) darkGreen else lightGrey,
        shape = RoundedCornerShape(32.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (isSelected) Color.White else darkGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title.uppercase(),
                color = if (isSelected) Color.White else darkGreen.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DurationCircle(mins: Int, isSelected: Boolean, onClick: () -> Unit, darkGreen: Color, lightGreen: Color) {
    Box(
        modifier = Modifier
            .size(72.dp, 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) lightGreen else Color(0xFFF1F1F1))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$mins min",
            color = if (isSelected) darkGreen else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatItem(icon: ImageVector, label: String, value: String, darkGreen: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = darkGreen.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = darkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
