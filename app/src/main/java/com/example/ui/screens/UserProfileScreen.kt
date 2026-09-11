package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onBack: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSavedQuestions: () -> Unit = {}
) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(SampleData.sampleUserProfile) }
    var dailyNotifications by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }

    AppLuminousBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "👤 Aspirant Profile & Target",
                            color = AppTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Info Header Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, Color(0xFF2563EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AppOrangePrimary,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = profile.name,
                            color = AppTextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEDE5),
                            border = BorderStroke(1.dp, Color(0xFFFFD5C2))
                        ) {
                            Text(
                                text = "👑 ${profile.proPlanName}",
                                color = AppOrangePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem("14 Days", "Study Streak")
                            ProfileStatItem("1,420 pts", "State Points")
                            ProfileStatItem("30 Qs/Day", "Daily Target")
                        }
                    }
                }
            }

            // User Contact & Exam Target
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Target Exam & Contact Info", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileDetailRow(Icons.Default.School, "Target Exam", profile.targetExam)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppLightBorder)

                        ProfileDetailRow(Icons.Default.Email, "Email Address", profile.email)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppLightBorder)

                        ProfileDetailRow(Icons.Default.Phone, "Mobile Number", profile.phone)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppLightBorder)

                        ProfileDetailRow(Icons.Default.Language, "Language", profile.preferredLanguage)
                    }
                }
            }

            // Preferences & Toggles
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "App Preferences & Reminders", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Daily Quiz Reminder", color = AppTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = dailyNotifications,
                                onCheckedChange = { dailyNotifications = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppOrangePrimary)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sound Effects on Answer", color = AppTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = soundEffects,
                                onCheckedChange = { soundEffects = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppOrangePrimary)
                            )
                        }
                    }
                }
            }

            // Saved Questions & Revision Bookmarks
            item {
                OutlinedButton(
                    onClick = onNavigateToSavedQuestions,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEFF6FF), contentColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppOrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saved Questions & Revision Bookmarks", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            }

            // Pro Membership & Reset Options
            item {
                Button(
                    onClick = onNavigateToSubscription,
                    colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Manage Pro Subscription Plans", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }

            item {
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTextDark),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AppLightBorder),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign In / Switch User Account", fontWeight = FontWeight.Bold)
                }
            }

            item {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Practice history and score statistics reset successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Practice History", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                }
            }
        }
    }
}
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color(0xFF2563EB), fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(text = label, color = AppTextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun ProfileDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = AppTextMuted, fontSize = 11.sp)
            Text(text = value, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
