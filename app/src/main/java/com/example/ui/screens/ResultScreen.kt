package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    questions: List<Question>,
    userAnswers: Map<String, String>,
    bookmarkedIds: Set<String>,
    correctCount: Int,
    wrongCount: Int,
    score: Double,
    onPracticeAgain: () -> Unit,
    onBackToHome: () -> Unit,
    onNextSet: (() -> Unit)? = null,
    onToggleBookmark: ((Question) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Mistakes", "Skipped"
    var expandedQuestionIds by remember { mutableStateOf<Set<String>>(questions.map { it.id }.toSet()) }
    var currentBookmarkedIds by remember(bookmarkedIds) { mutableStateOf(bookmarkedIds) }

    val totalQ = questions.size.coerceAtLeast(1)
    val attempted = correctCount + wrongCount
    val unattempted = (questions.size - attempted).coerceAtLeast(0)
    val accuracyPercentage = if (attempted > 0) ((correctCount.toDouble() / attempted) * 100).toInt() else 0
    val scorePercentage = ((correctCount.toDouble() / totalQ) * 100).toInt()

    val statusPillText = when {
        scorePercentage >= 75 -> "Excellent Mastery"
        scorePercentage >= 50 -> "Good Progress"
        else -> "Needs Improvement"
    }

    val statusPillBg = when {
        scorePercentage >= 75 -> Color(0xFFDCFCE7)
        scorePercentage >= 50 -> Color(0xFFFEF3C7)
        else -> Color(0xFFFFE4E6)
    }

    val statusPillColor = when {
        scorePercentage >= 75 -> Color(0xFF16A34A)
        scorePercentage >= 50 -> Color(0xFFD97706)
        else -> Color(0xFFE11D48)
    }

    AppLuminousBackground {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // 1. TOP HEADER (Dashboard, Set 1 pill, Needs Improvement, Share)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back to Dashboard
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { onBackToHome() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Dashboard",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Dashboard",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Middle Pill: Set 1
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Set 1 ▾",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Status Badge & Share Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = statusPillBg,
                            border = BorderStroke(1.dp, statusPillColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = statusPillColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = statusPillText,
                                    color = statusPillColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .size(34.dp)
                                .clickable {
                                    Toast.makeText(context, "Scorecard shared successfully!", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. HERO CELEBRATION / MOTIVATION CARD WITH CIRCULAR PROGRESS WHEEL
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFF7ED),
                                        Color(0xFFEFF6FF),
                                        Color(0xFFF0FDF4)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left message
                                Column {
                                    Text(
                                        text = if (scorePercentage >= 70) "Outstanding!" else "Great Effort!",
                                        color = Color(0xFFDC2626),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = FontStyle.Italic
                                    )
                                    Text(
                                        text = "Practice Today\nBetter Tomorrow",
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }

                                // Center Circular Progress Wheel
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(105.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(100.dp)) {
                                        // Track circle
                                        drawCircle(
                                            color = Color(0xFFCBD5E1).copy(alpha = 0.4f),
                                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        // Progress arc
                                        val sweep = (scorePercentage / 100f) * 360f
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                listOf(
                                                    Color(0xFF3B82F6),
                                                    Color(0xFFE11D48),
                                                    Color(0xFFF59E0B),
                                                    Color(0xFF10B981)
                                                )
                                            ),
                                            startAngle = -90f,
                                            sweepAngle = sweep.coerceAtLeast(15f),
                                            useCenter = false,
                                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$scorePercentage%",
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 22.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFE11D48)
                                        ) {
                                            Text(
                                                text = statusPillText,
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Right Quote Box
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "“Mistakes are proof\nthat you are trying.”",
                                        color = Color(0xFF1E293B),
                                        fontSize = 10.sp,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = TextAlign.End,
                                        lineHeight = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0F172A)
                                    ) {
                                        Text(
                                            text = "DISCIPLINE TODAY\nSUCCESS TOMORROW",
                                            color = Color(0xFFFDE68A),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "You answered $correctCount out of ${questions.size} questions correctly",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. AI STUDY SUGGESTION CARD
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE11D48),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Study Suggestion",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFE4E6)
                                ) {
                                    Text(
                                        text = statusPillText,
                                        color = Color(0xFFE11D48),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Keep going! Revise the key concepts for this topic and practice the missed questions again. Focus on understanding core fundamentals before retrying.",
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. NEXT TARGET MILESTONE (70%+)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.TrackChanges,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = "Next Target Milestone",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "70%+",
                                        color = Color(0xFF2563EB),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Aim for mastery",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Improve just ${((questions.size * 0.7) - correctCount).toInt().coerceAtLeast(1)} more questions\nto reach your next target!",
                                        color = Color(0xFFB45309),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "🚩", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Target progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF6366F1))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("70%", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 5. 8-BOX PERFORMANCE METRIC TILES (4 ROWS OF 2)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Correct + Wrong
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.CheckCircle,
                                iconColor = Color(0xFF10B981),
                                iconBg = Color(0xFFECFDF5),
                                label = "Correct",
                                value = "$correctCount",
                                sublabel = "+1 Mark each",
                                tileBg = Color(0xFFF0FDF4),
                                borderColor = Color(0xFFBBF7D0)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.Cancel,
                                iconColor = Color(0xFFE11D48),
                                iconBg = Color(0xFFFFE4E6),
                                label = "Wrong",
                                value = "$wrongCount",
                                sublabel = "Mistakes to revise",
                                tileBg = Color(0xFFFFF1F2),
                                borderColor = Color(0xFFFECDD3)
                            )
                        }
                    }

                    // Row 2: Accuracy + Total Questions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.TrackChanges,
                                iconColor = Color(0xFF2563EB),
                                iconBg = Color(0xFFEFF6FF),
                                label = "Accuracy",
                                value = "$accuracyPercentage%",
                                sublabel = "Score percentage",
                                tileBg = Color(0xFFEFF6FF),
                                borderColor = Color(0xFFBFDBFE)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.Description,
                                iconColor = Color(0xFF8B5CF6),
                                iconBg = Color(0xFFF5F3FF),
                                label = "Total Questions",
                                value = "${questions.size}",
                                sublabel = "In this session",
                                tileBg = Color(0xFFFAF5FF),
                                borderColor = Color(0xFFDDD6FE)
                            )
                        }
                    }

                    // Row 3: Time Taken + Attempted
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.AccessTime,
                                iconColor = Color(0xFFD97706),
                                iconBg = Color(0xFFFFFBEB),
                                label = "Time Taken",
                                value = "00:16",
                                sublabel = "Pace & Speed",
                                tileBg = Color(0xFFFFFBEB),
                                borderColor = Color(0xFFFDE68A)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.BarChart,
                                iconColor = Color(0xFF0D9488),
                                iconBg = Color(0xFFF0FDFA),
                                label = "Attempted",
                                value = "$attempted / ${questions.size}",
                                sublabel = "Questions",
                                tileBg = Color(0xFFF0FDFA),
                                borderColor = Color(0xFF99F6E4)
                            )
                        }
                    }

                    // Row 4: Unattempted + Next Target
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.PlayCircle,
                                iconColor = Color(0xFFE11D48),
                                iconBg = Color(0xFFFFE4E6),
                                label = "Unattempted",
                                value = "$unattempted",
                                sublabel = "Not attempted",
                                tileBg = Color(0xFFFFF1F2),
                                borderColor = Color(0xFFFECDD3)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ResultTile(
                                icon = Icons.Default.TrackChanges,
                                iconColor = Color(0xFFEA580C),
                                iconBg = Color(0xFFFFEDD5),
                                label = "Next Target",
                                value = "70%+",
                                sublabel = "Keep Practicing",
                                tileBg = Color(0xFFFFF7ED),
                                borderColor = Color(0xFFFED7AA)
                            )
                        }
                    }
                }
            }

            // 6. ACTION BUTTONS (View Summary, Practice Again, Next Set 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // View Summary Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                Toast.makeText(context, "Summary overview loaded", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Summary", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Practice Again Button (Orange)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEA580C),
                        modifier = Modifier
                            .weight(1.1f)
                            .clickable { onPracticeAgain() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RotateLeft, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Practice Again", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Next Set Button (Purple/Blue)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4F46E5),
                        modifier = Modifier
                            .weight(1.1f)
                            .clickable {
                                onNextSet?.invoke() ?: onPracticeAgain()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Next Set", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                Text("Set 2", color = Color(0xFFC7D2FE), fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        }
                    }
                }
            }

            // 7. DETAILED SOLUTIONS & CONCEPT REVIEW HEADER & FILTER PILLS
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF4F46E5),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Detailed Solutions & Concept Review",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filter Pills: All (10), Mistakes (1), Skipped (9)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // All (10)
                            FilterPill(
                                title = "All (${questions.size})",
                                isSelected = selectedFilter == "All",
                                activeBg = Color(0xFF2563EB),
                                activeColor = Color.White,
                                inactiveBg = Color(0xFFEFF6FF),
                                inactiveColor = Color(0xFF2563EB),
                                onClick = { selectedFilter = "All" }
                            )

                            // Mistakes (1)
                            FilterPill(
                                title = "Mistakes ($wrongCount)",
                                isSelected = selectedFilter == "Mistakes",
                                activeBg = Color(0xFFE11D48),
                                activeColor = Color.White,
                                inactiveBg = Color(0xFFFFE4E6),
                                inactiveColor = Color(0xFFE11D48),
                                onClick = { selectedFilter = "Mistakes" }
                            )

                            // Skipped (9)
                            FilterPill(
                                title = "Skipped ($unattempted)",
                                isSelected = selectedFilter == "Skipped",
                                activeBg = Color(0xFF64748B),
                                activeColor = Color.White,
                                inactiveBg = Color(0xFFF1F5F9),
                                inactiveColor = Color(0xFF64748B),
                                onClick = { selectedFilter = "Skipped" }
                            )
                        }
                    }
                }
            }

            // 8. DETAILED QUESTION SOLUTION CARDS (Q01, Q02, ...)
            val filteredQuestions = questions.filterIndexed { index, q ->
                val userAns = userAnswers[q.id]
                val isCorrect = userAns != null && userAns.equals(q.correctOption, ignoreCase = true)
                when (selectedFilter) {
                    "Mistakes" -> userAns != null && !isCorrect
                    "Skipped" -> userAns == null
                    else -> true
                }
            }

            itemsIndexed(filteredQuestions) { index, q ->
                val qIndex = questions.indexOf(q) + 1
                val qNumberFormatted = "Q%02d".format(qIndex)
                val userAns = userAnswers[q.id]
                val isCorrect = userAns != null && userAns.equals(q.correctOption, ignoreCase = true)
                val isSkipped = userAns == null
                val isBookmarked = currentBookmarkedIds.contains(q.id)

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Top Row: Q01 badge + Status badge (Incorrect/Skipped/Correct) + Bookmark + Breadcrumbs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                ) {
                                    Text(
                                        text = qNumberFormatted,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Status Badge
                                if (isCorrect) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDCFCE7),
                                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Correct", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                } else if (!isSkipped) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFE4E6),
                                        border = BorderStroke(1.dp, Color(0xFFFECDD3))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Incorrect", color = Color(0xFFE11D48), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Skipped", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) Color(0xFF2563EB) else Color(0xFF64748B),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            val next = !isBookmarked
                                            currentBookmarkedIds = if (next) currentBookmarkedIds + q.id else currentBookmarkedIds - q.id
                                            onToggleBookmark?.invoke(q)
                                        }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Indian History  |  Set 1",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question Text in Box (Exact Telugu Font & Light Blue Container)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0F9FF),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = q.questionText,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 19.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2x2 Option Grid with Color Codes
                        val optionKeys = listOf("A", "B", "C", "D")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OptionSolutionCard(
                                        optKey = "A",
                                        optText = q.options["A"] ?: "1950",
                                        isCorrectOption = q.correctOption.equals("A", ignoreCase = true),
                                        isUserSelected = userAns.equals("A", ignoreCase = true)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OptionSolutionCard(
                                        optKey = "B",
                                        optText = q.options["B"] ?: "1956",
                                        isCorrectOption = q.correctOption.equals("B", ignoreCase = true),
                                        isUserSelected = userAns.equals("B", ignoreCase = true)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OptionSolutionCard(
                                        optKey = "C",
                                        optText = q.options["C"] ?: "1948",
                                        isCorrectOption = q.correctOption.equals("C", ignoreCase = true),
                                        isUserSelected = userAns.equals("C", ignoreCase = true)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OptionSolutionCard(
                                        optKey = "D",
                                        optText = q.options["D"] ?: "1952",
                                        isCorrectOption = q.correctOption.equals("D", ignoreCase = true),
                                        isUserSelected = userAns.equals("D", ignoreCase = true)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Explanation & Concept Breakdown Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Explanation & Concept Breakdown",
                                            color = Color(0xFF1E40AF),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = q.explanation?.ifBlank { null }
                                        ?: "సరైన సమాధానం: 1952. ఈ అంశం ముదలియార్ కమిషన్ (1952–53) యొక్క మాధ్యమిక విద్యా సంస్కరణలతో సంబంధం కలిగి ఉంది.",
                                    color = Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
}

@Composable
private fun OptionSolutionCard(
    optKey: String,
    optText: String,
    isCorrectOption: Boolean,
    isUserSelected: Boolean
) {
    val bg = when {
        isUserSelected && !isCorrectOption -> Color(0xFFFFE4E6) // Red error
        isCorrectOption -> Color(0xFFDCFCE7) // Green correct
        else -> Color.White
    }

    val border = when {
        isUserSelected && !isCorrectOption -> Color(0xFFFECDD3)
        isCorrectOption -> Color(0xFF86EFAC)
        else -> Color(0xFFE2E8F0)
    }

    val keyBg = when {
        isUserSelected && !isCorrectOption -> Color(0xFFE11D48)
        isCorrectOption -> Color(0xFF16A34A)
        else -> Color(0xFFF1F5F9)
    }

    val keyTextCol = when {
        isUserSelected && !isCorrectOption -> Color.White
        isCorrectOption -> Color.White
        else -> Color(0xFF475569)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = keyBg,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = optKey,
                                color = keyTextCol,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = optText,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                // Check or Cross icons
                if (isUserSelected && !isCorrectOption) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Your Answer",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isCorrectOption) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct Answer",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isUserSelected && !isCorrectOption) {
                Text(
                    text = "Your Answer",
                    color = Color(0xFFE11D48),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isCorrectOption) {
                Text(
                    text = "Correct Answer",
                    color = Color(0xFF16A34A),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ResultTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBg: Color,
    label: String,
    value: String,
    sublabel: String,
    tileBg: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = tileBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = label,
                    color = Color(0xFF475569),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
                Text(
                    text = sublabel,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    title: String,
    isSelected: Boolean,
    activeBg: Color,
    activeColor: Color,
    inactiveBg: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) activeBg else inactiveBg,
        border = BorderStroke(1.dp, if (isSelected) activeBg else inactiveColor.copy(alpha = 0.3f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
