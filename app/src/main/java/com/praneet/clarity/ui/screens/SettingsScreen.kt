package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val accentBlue = MaterialTheme.colorScheme.primary
    val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val logoutRed = Color(0xFFE57373)
    val onSurface = MaterialTheme.colorScheme.onSurface

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "Alex Sterling"
    val userEmail = currentUser?.email ?: "alex.s@clarity.io"

    Scaffold(
        containerColor = darkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = accentBlue)
                    }
                },
                title = {
                    Text("Settings", color = onSurface, fontWeight = FontWeight.Bold)
                },
                actions = {
                    Text(
                        "CLARITY",
                        modifier = Modifier.padding(end = 16.dp),
                        color = greyText,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ACCOUNT SECTION
            item {
                SectionHeader("ACCOUNT", greyText)
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Profile Row
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                color = onSurface.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("👤", fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(userName, color = onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(userEmail, color = greyText, fontSize = 13.sp)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = greyText)
                        }
                        
                        HorizontalDivider(color = onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsRow(
                            icon = Icons.Default.Lock,
                            title = "Change Password",
                            showChevron = true,
                            greyText = greyText,
                            onSurface = onSurface
                        )
                        
                        HorizontalDivider(color = onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            title = "Logout",
                            titleColor = logoutRed,
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                onLogout()
                            },
                            greyText = greyText,
                            onSurface = onSurface
                        )
                    }
                }
            }

            // APPEARANCE SECTION
            item {
                SectionHeader("APPEARANCE", greyText)
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, null, tint = greyText)
                            Spacer(Modifier.width(16.dp))
                            Text("Theme", color = onSurface, fontWeight = FontWeight.Medium)
                        }
                        
                        Surface(
                            color = onSurface.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                ThemeButton("LIGHT", false, greyText)
                                ThemeButton("DARK", true, accentBlue)
                                ThemeButton("SYSTEM", false, greyText)
                            }
                        }
                    }
                }
            }

            // ABOUT SECTION
            item {
                SectionHeader("ABOUT", greyText)
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        AboutRow("Developer", "Julien Vance", Icons.Default.Code, greyText, onSurface)
                        HorizontalDivider(color = onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                        AboutRow("Version", "2.4.1-Stable", Icons.Default.Info, greyText, onSurface)
                        HorizontalDivider(color = onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                        AboutRow("Terms of Service", "", Icons.Default.OpenInNew, greyText, onSurface, showExternal = true)
                    }
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun SectionHeader(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.Unspecified,
    showChevron: Boolean = false,
    greyText: Color,
    onSurface: Color,
    onClick: () -> Unit = {}
) {
    val actualTitleColor = if (titleColor == Color.Unspecified) onSurface else titleColor
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (titleColor == Color.Unspecified) greyText else titleColor)
        Spacer(Modifier.width(16.dp))
        Text(title, color = actualTitleColor, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (showChevron) {
            Icon(Icons.Default.ChevronRight, null, tint = greyText)
        }
    }
}

@Composable
fun AboutRow(
    label: String,
    value: String,
    icon: ImageVector,
    greyText: Color,
    onSurface: Color,
    showExternal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = greyText)
        Spacer(Modifier.width(16.dp))
        Text(label, color = onSurface, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (value.isNotEmpty()) {
            Text(value, color = greyText, fontSize = 14.sp)
        }
        if (showExternal) {
            Icon(Icons.Default.OpenInNew, null, tint = greyText, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ThemeButton(text: String, isSelected: Boolean, accentColor: Color) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        onClick = {}
    ) {
        val greyText = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) accentColor else greyText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
