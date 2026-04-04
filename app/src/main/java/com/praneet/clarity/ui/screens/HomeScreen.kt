package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.praneet.clarity.utils.AlarmHelper // Ensure this file exists in utils!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    focusViewModel: FocusViewModel
) {
    val context = LocalContext.current
    var showAddGoalDialog by remember { mutableStateOf(false) }

    // 🎨 Palette
    val OffWhite = Color(0xFFF8FAF9)
    val SoftRose = Color(0xFFFCE4EC)
    val DeepText = Color(0xFF4A4A4A)
    val AccentPink = Color(0xFFF472B6)

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { Text("Clarity ✨", color = DeepText, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }) {
                        Text("Exit", color = AccentPink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Text("🌸", fontSize = 80.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 20.dp, y = (-20).dp).alpha(0.3f))

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Hi, Praneet!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepText)
                Text("Your progress is looking great.", fontSize = 14.sp, color = DeepText.copy(alpha = 0.6f))

                Spacer(modifier = Modifier.height(24.dp))

                Surface(color = Color.White, shape = RoundedCornerShape(32.dp), shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(85.dp).clip(CircleShape).background(SoftRose), contentAlignment = Alignment.Center) {
                            Text("50%", fontWeight = FontWeight.Black, fontSize = 24.sp, color = DeepText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Your Goals 🎀", color = DeepText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { showAddGoalDialog = true }) {
                        Text("➕", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real Goal Loop
                focusViewModel.goals.forEach { goalMap ->
                    val title = goalMap["title"] as? String ?: "Unnamed Goal"
                    val target = (goalMap["targetHours"] as? Number)?.toInt() ?: 1
                    val current = (goalMap["currentMinutes"] as? Number)?.toLong() ?: 0L
                    val totalMinutes = target * 60f
                    val progress = if (totalMinutes > 0) (current / totalMinutes) else 0f

                    CuteGoalCard(title, progress, if (progress >= 1f) "Done!" else "${(progress * 100).toInt()}%", if (progress >= 1f) Color(0xFFBBF7D0) else Color(0xFFBAE6FD), DeepText)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        focusViewModel.startTimer(25)
                        // This might remain red until you create AlarmHelper.kt
                        AlarmHelper.scheduleAlarm(context, 25)
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text(if (focusViewModel.isTimerRunning) "Focusing... ⏳" else "Start Focus Session ☁️", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CuteStatItem("3h 45m", "Focus", Color(0xFF7DD3FC), DeepText)
                    CuteStatItem("5", "Sessions", AccentPink, DeepText)
                    CuteStatItem("🔥 4", "Streak", Color(0xFF4ADE80), DeepText)
                }
            }

            if (showAddGoalDialog) {
                LocalAddGoalDialog(
                    onDismiss = { showAddGoalDialog = false },
                    onConfirm = { title, hours ->
                        focusViewModel.createNewGoal(title, hours)
                        showAddGoalDialog = false
                    }
                )
            }

            if (focusViewModel.showEnergySheet) {
                EnergyCheckSheet(onDismiss = { energy -> focusViewModel.completeSession("active_goal", 25, energy) })
            }
        }
    }
}

@Composable
fun CuteGoalCard(title: String, progress: Float, status: String, accent: Color, textCol: Color) {
    Surface(color = Color.White, shape = RoundedCornerShape(28.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = textCol, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(status, color = textCol.copy(alpha = 0.4f), fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = { progress }, color = accent, trackColor = accent.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape))
        }
    }
}

@Composable
fun CuteStatItem(value: String, label: String, color: Color, textCol: Color) {
    Surface(color = Color.White, shape = RoundedCornerShape(20.dp), modifier = Modifier.width(90.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 12.dp)) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = textCol.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LocalAddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { onConfirm(title, hours.toIntOrNull() ?: 1) }) { Text("Add") } },
        title = { Text("New Goal") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal Name") })
                OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Target Hours") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    )
}