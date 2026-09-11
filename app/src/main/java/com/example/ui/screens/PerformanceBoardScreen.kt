package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TestResult
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceBoardScreen(
    results: List<TestResult> = emptyList(),
    onBack: () -> Unit
) {
    val totalPracticed = if (results.isNotEmpty()) results.sumOf { it.totalQuestions } else 142
    val avgAccuracy = if (results.isNotEmpty()) (results.sumOf { it.score } * 100 / results.sumOf { it.totalQuestions }.coerceAtLeast(1)) else 84

    AppLuminousBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "📊 Performance & Analytics",
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
            // Stats Overview Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, Color(0xFF2563EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$totalPracticed", color = AppOrangePrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("MCQs Practiced", color = AppTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp), color = AppLightBorder)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$avgAccuracy%", color = Color(0xFF16A34A), fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("Accuracy Rate", color = AppTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp), color = AppLightBorder)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rank #12", color = Color(0xFF2563EB), fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Text("Statewide Rank", color = AppTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Subject Mastery Breakdown",
                    color = AppTextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SubjectStatBar("Indian History", 0.88f, "88% Correct", Color(0xFF16A34A))
                    SubjectStatBar("Indian Polity & Constitution", 0.82f, "82% Correct", Color(0xFF2563EB))
                    SubjectStatBar("Andhra Pradesh Economy", 0.76f, "76% Correct", AppOrangePrimary)
                    SubjectStatBar("Geography & Environment", 0.0f, "0 MCQs Practiced", AppTextMuted)
                }
            }
        }
    }
}
}

@Composable
fun SubjectStatBar(
    subjectName: String,
    progress: Float,
    statusText: String,
    barColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AppLightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(subjectName, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(statusText, color = barColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }
        }
    }
}
