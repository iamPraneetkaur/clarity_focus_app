package com.praneet.clarity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.praneet.clarity.ui.theme.ThemeStyle
import com.praneet.clarity.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.provideFactory(LocalContext.current.applicationContext)
    )
) {
    val themeStyle by settingsViewModel.themeStyle
    val onSurface = MaterialTheme.colorScheme.onSurface
    val greyText = onSurface.copy(alpha = 0.6f)
    val cardBg = MaterialTheme.colorScheme.surface
    val accentForest = Color(0xFF3E4E35)

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "Alex Sterling"
    val userEmail = currentUser?.email ?: "alex.s@clarity.io"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = greyText,
                letterSpacing = 1.sp
            )
            Text(
                "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        // Profile Card
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = accentForest.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = accentForest, modifier = Modifier.size(30.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(userName, color = onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userEmail, color = greyText, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = greyText)
                }
            }
        }

        // Appearance Section
        item {
            SectionHeader("APPEARANCE")
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, null, tint = accentForest, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("Theme Style", color = onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(onSurface.copy(alpha = 0.05f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ThemeOptionButton(
                            text = "MINIMAL",
                            isSelected = themeStyle == ThemeStyle.MINIMAL,
                            modifier = Modifier.weight(1f)
                        ) {
                            settingsViewModel.setThemeStyle(ThemeStyle.MINIMAL)
                        }
                        ThemeOptionButton(
                            text = "SERENE",
                            isSelected = themeStyle == ThemeStyle.SERENE,
                            modifier = Modifier.weight(1f)
                        ) {
                            settingsViewModel.setThemeStyle(ThemeStyle.SERENE)
                        }
                        ThemeOptionButton(
                            text = "GAME",
                            isSelected = themeStyle == ThemeStyle.GAME,
                            modifier = Modifier.weight(1f)
                        ) {
                            settingsViewModel.setThemeStyle(ThemeStyle.GAME)
                        }
                    }
                }
            }
        }

        // Security Section
        item {
            SectionHeader("SECURITY")
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        onClick = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = onSurface.copy(alpha = 0.05f))
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout",
                        titleColor = Color(0xFFE57373),
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onLogout()
                        }
                    )
                }
            }
        }

        // About Section
        item {
            SectionHeader("INFO")
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItem(icon = Icons.Default.Code, title = "Developer", value = "Julien Vance")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = onSurface.copy(alpha = 0.05f))
                    SettingsItem(icon = Icons.Default.Info, title = "Version", value = "2.4.1 Stable")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = onSurface.copy(alpha = 0.05f))
                    SettingsItem(icon = Icons.Default.OpenInNew, title = "Privacy Policy")
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    titleColor: Color = Color.Unspecified,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (titleColor == Color.Unspecified) Color(0xFF3E4E35) else titleColor, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            color = if (titleColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else titleColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(value, color = Color.Gray, fontSize = 14.sp)
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ThemeOptionButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFF3E4E35) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
