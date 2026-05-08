package com.praneet.clarity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomDurationDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var minutesText by remember { mutableStateOf("") }
    val forestGreen = Color(0xFF3E4E35)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        confirmButton = { 
            Button(
                onClick = { onConfirm(minutesText.toIntOrNull() ?: 25) },
                colors = ButtonDefaults.buttonColors(containerColor = forestGreen),
                shape = RoundedCornerShape(12.dp)
            ) { 
                Text("Set Duration", fontWeight = FontWeight.Bold) 
            } 
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = forestGreen)
            }
        },
        title = { 
            Text("Custom Duration", fontWeight = FontWeight.Bold, color = forestGreen) 
        },
        text = {
            Column {
                Text("Enter focus time in minutes:", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = minutesText, 
                    onValueChange = { if (it.all { char -> char.isDigit() }) minutesText = it }, 
                    label = { Text("Minutes") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = forestGreen,
                        focusedLabelColor = forestGreen
                    )
                )
            }
        }
    )
}

@Composable
fun LocalAddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    val forestGreen = Color(0xFF3E4E35)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        confirmButton = { 
            Button(
                onClick = { onConfirm(title, minutesText.toIntOrNull() ?: 60) },
                colors = ButtonDefaults.buttonColors(containerColor = forestGreen),
                shape = RoundedCornerShape(12.dp)
            ) { 
                Text("Add Goal", fontWeight = FontWeight.Bold) 
            } 
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = forestGreen)
            }
        },
        title = { 
            Text("New Focus Goal", fontWeight = FontWeight.Bold, color = forestGreen) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("What would you like to focus on?", color = Color.Gray, fontSize = 14.sp)
                
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Goal Name (e.g. Reading)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = forestGreen,
                        focusedLabelColor = forestGreen
                    )
                )
                
                OutlinedTextField(
                    value = minutesText, 
                    onValueChange = { if (it.all { char -> char.isDigit() }) minutesText = it }, 
                    label = { Text("Target Minutes") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = forestGreen,
                        focusedLabelColor = forestGreen
                    )
                )
            }
        }
    )
}
