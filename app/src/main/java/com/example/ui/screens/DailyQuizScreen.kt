package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.Question
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(
    onBack: () -> Unit,
    onStartDailyQuiz: (List<Question>) -> Unit
) {
    AppLuminousBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "⚡ Daily Quiz Sprint",
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
            // Streak Header Banner
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
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "14 Day Study Streak Active! 🔥",
                            color = AppTextDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Complete today's 10 MCQs to maintain your streak & earn 100 XP",
                            color = AppTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Daily Challenge Details
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Challenge • High Yield MCQs",
                                color = Color(0xFF2563EB),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFEDE5),
                                border = BorderStroke(1.dp, Color(0xFFFFD5C2))
                            ) {
                                Text(
                                    text = "+100 XP Reward",
                                    color = AppOrangePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Mixed Subject Daily Challenge",
                            color = AppTextDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Covers Indian History, Economy, Constitution & AP Specific GK questions selected for today's practice.",
                            color = AppTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onStartDailyQuiz(SampleData.sampleDailyQuestions) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppOrangePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Today's Daily Sprint", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }
            }

            // Benefits / Rewards section
            item {
                Text(
                    text = "Daily Challenge Rewards",
                    color = AppTextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RewardRow("🔥 Streak Boost", "Build consistent daily practice habits for APPSC exam prep.")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppLightBorder)
                        RewardRow("👑 State Rank Points", "Earn bonus points to climb up the State Leaderboard.")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppLightBorder)
                        RewardRow("💡 Instant Solutions", "Detailed explanations provided for every option right after submission.")
                    }
                }
            }
        }
    }
}
}

@Composable
fun RewardRow(title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Stars, contentDescription = null, tint = AppOrangePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = desc, color = AppTextSecondary, fontSize = 12.sp)
        }
    }
}
