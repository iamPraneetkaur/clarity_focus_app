package com.praneet.clarity.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.components.CompletionCheerDialog
import com.praneet.clarity.ui.components.EnergyCheckSheet
import com.praneet.clarity.ui.components.CustomDurationDialog
import com.praneet.clarity.ui.components.LocalAddGoalDialog
import com.praneet.clarity.viewmodel.FocusViewModel
import com.praneet.clarity.utils.AlarmHelper
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    focusViewModel: FocusViewModel,
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showCustomDurationDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isGoalDropdownExpanded by remember { mutableStateOf(false) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Alex"
    
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hourOfDay) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    AnimatedContent(
        targetState = focusViewModel.isTimerRunning,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$greeting, $userName",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Let's make today productive",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = "Profile",
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                FocusTimeCard()
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Start Focus Session", 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val presets = listOf(25, 45, 60)
                            val currentDur = focusViewModel.initialSelectedMinutes
                            
                            presets.forEach { mins ->
                                DurationChip(
                                    mins = mins,
                                    isSelected = currentDur == mins,
                                    onClick = { focusViewModel.updateSelectedDuration(mins) }
                                )
                            }
                            
                            DurationChip(
                                label = "Custom",
                                isSelected = currentDur !in presets,
                                onClick = { showCustomDurationDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ExposedDropdownMenuBox(
                            expanded = isGoalDropdownExpanded,
                            onExpandedChange = { isGoalDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = focusViewModel.selectedGoalTitle ?: "Select a Goal",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Session Goal") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGoalDropdownExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = isGoalDropdownExpanded,
                                onDismissRequest = { isGoalDropdownExpanded = false }
                            ) {
                                focusViewModel.goals.forEach { goalMap ->
                                    val id = goalMap["id"] as? String ?: return@forEach
                                    val title = goalMap["title"] as? String ?: "Unnamed"
                                    DropdownMenuItem(
                                        text = { Text(title) },
                                        onClick = {
                                            focusViewModel.selectGoal(id, title)
                                            isGoalDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (focusViewModel.selectedGoalId != null) {
                                    focusViewModel.startTimer(focusViewModel.initialSelectedMinutes)
                                    AlarmHelper.scheduleAlarm(context, focusViewModel.initialSelectedMinutes, focusViewModel.selectedGoalId)
                                }
                            },
                            enabled = focusViewModel.selectedGoalId != null,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3E4E35),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Focus", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Goals", 
                        color = MaterialTheme.colorScheme.onBackground, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 20.sp
                    )
                    TextButton(onClick = { showAddGoalDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Goal")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    focusViewModel.goals.forEach { goalMap ->
                        val id = goalMap["id"] as? String ?: return@forEach
                        val title = goalMap["title"] as? String ?: "Unnamed"
                        val current = (goalMap["currentMinutes"] as? Long ?: 0L).toInt()
                        val target = goalMap["targetMinutes"] as? Int ?: 60

                        GoalCard(
                            title = title,
                            current = current,
                            target = target,
                            onLongClick = { goalToDelete = id to title }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = "\"Small steps today lead to big success\ntomorrow\"",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (showAddGoalDialog) {
                    LocalAddGoalDialog(
                        onDismiss = { showAddGoalDialog = false },
                        onConfirm = { title, targetMins ->
                            focusViewModel.createNewGoal(title, targetMins)
                            showAddGoalDialog = false
                        }
                    )
                }

                if (showCustomDurationDialog) {
                    CustomDurationDialog(
                        onDismiss = { showCustomDurationDialog = false },
                        onConfirm = { mins ->
                            focusViewModel.updateSelectedDuration(mins)
                            showCustomDurationDialog = false
                        }
                    )
                }

                if (focusViewModel.showCompletionCheer) {
                    CompletionCheerDialog(onDismiss = { focusViewModel.dismissCheer() })
                }

                if (focusViewModel.showEnergySheet) {
                    EnergyCheckSheet(
                        onDismiss = { energyLevel ->
                            focusViewModel.completeSession(energyLevel.name)
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
                                    focusViewModel.deleteGoal(id)
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
    }
}

@Composable
fun FocusTimeCard() {
    val cardGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFB8C69F), Color(0xFFA3B18A))
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp)
            )

            Row(
                modifier = Modifier.padding(28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "85% Focus\ntime left\ntoday",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3627),
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "STEADY PROGRESS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3627).copy(alpha = 0.6f),
                        letterSpacing = 1.2.sp
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0.85f },
                        modifier = Modifier.size(84.dp),
                        color = Color(0xFF2C3627),
                        trackColor = Color(0xFF2C3627).copy(alpha = 0.1f),
                        strokeWidth = 10.dp,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        "85",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3627)
                    )
                }
            }
        }
    }
}

@Composable
fun DurationChip(mins: Int? = null, label: String? = null, isSelected: Boolean, onClick: () -> Unit) {
    val text = label ?: "${mins}m"
    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFFDAE2D3) else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 72.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFF3E4E35) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalCard(title: String, current: Int, target: Int, onLongClick: () -> Unit) {
    val progress = if (target > 0) current.toFloat() / target.toFloat() else 0f
    val (icon, color) = when {
        title.contains("Read", ignoreCase = true) -> Icons.Default.Book to Color(0xFFFFD1DC)
        title.contains("Hydration", ignoreCase = true) -> Icons.Default.WaterDrop to Color(0xFFD1F2D1)
        else -> Icons.Default.Flag to Color(0xFFE0E0E0)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .width(160.dp)
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = color,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Color(0xFF4A3434),
                trackColor = Color.LightGray.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("$current/$target ${if (title.contains("Read")) "pages" else "mins"}", fontSize = 11.sp, color = Color.Gray)
        }
    }
}
