package com.praneet.clarity.ui.components

import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CompletionCheerDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "cheer")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Sweet "music" using system ringtone for a longer melodic feel
    val mediaPlayer = remember {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        MediaPlayer.create(context, alarmUri).apply {
            isLooping = true
        }
    }

    LaunchedEffect(Unit) {
        mediaPlayer.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    "🌿",
                    fontSize = 100.sp,
                    modifier = Modifier.scale(scale)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "YOU DID IT!",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Hey look, you have done it!\nThat focus was incredible. ✨",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(64.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        "Awesome! ☁️", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
