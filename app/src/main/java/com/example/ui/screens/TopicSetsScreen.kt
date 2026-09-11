package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.PracticeSetModel
import com.example.model.Subject
import com.example.model.TopicModel
import com.example.model.UnitModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicSetsScreen(
    subject: Subject,
    unit: UnitModel,
    topic: TopicModel,
    onBack: () -> Unit,
    onStartSet: (PracticeSetModel) -> Unit,
    onNavigateToHome: () -> Unit = onBack,
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToCurrentAffairs: () -> Unit = {},
    onNavigateToMockTests: () -> Unit = {},
    onNavigateToDailyQuiz: () -> Unit = {},
    onNavigateToSavedQuestions: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    var isBookmarked by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val practiceSets = remember(subject.id, unit.id, topic.id) {
        SampleData.getPracticeSetsForTopic(subject.id, unit, topic)
    }

    val totalMcqs = remember(practiceSets) { practiceSets.sumOf { it.questionsCount } }
    val completedSetsCount = remember(practiceSets) { practiceSets.count { it.status == "Completed" || it.isCompleted } }

    AppLuminousBackground {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // 1. TOP APP BAR / HEADER ROW
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Middle Pill: 1 Indian History (Unit/Subject Pill)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEA580C),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${unit.unitNumber.takeIf { it > 0 } ?: 1}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subject.name,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Right Icons: Bookmark + Menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(42.dp)
                                .clickable {
                                    isBookmarked = !isBookmarked
                                    Toast.makeText(
                                        context,
                                        if (isBookmarked) "Saved to bookmarks" else "Removed from bookmarks",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) Color(0xFF2563EB) else Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Box {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { showMoreMenu = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("🏠 Home Dashboard") },
                                    onClick = { showMoreMenu = false; onNavigateToHome() }
                                )
                                DropdownMenuItem(
                                    text = { Text("👑 Admin Portal") },
                                    onClick = { showMoreMenu = false; onNavigateToAdmin() }
                                )
                                DropdownMenuItem(
                                    text = { Text("📰 Current Affairs") },
                                    onClick = { showMoreMenu = false; onNavigateToCurrentAffairs() }
                                )
                                DropdownMenuItem(
                                    text = { Text("🎯 Grand Mock Tests") },
                                    onClick = { showMoreMenu = false; onNavigateToMockTests() }
                                )
                                DropdownMenuItem(
                                    text = { Text("⚡ Daily Quiz") },
                                    onClick = { showMoreMenu = false; onNavigateToDailyQuiz() }
                                )
                                DropdownMenuItem(
                                    text = { Text("🔖 Saved MCQs") },
                                    onClick = { showMoreMenu = false; onNavigateToSavedQuestions() }
                                )
                                DropdownMenuItem(
                                    text = { Text("💳 Subscriptions & Pro Pass") },
                                    onClick = { showMoreMenu = false; onNavigateToSubscription() }
                                )
                                DropdownMenuItem(
                                    text = { Text("👤 Profile & Account") },
                                    onClick = { showMoreMenu = false; onNavigateToProfile() }
                                )
                            }
                        }
                    }
                }
            }

            // 2. HERO CARD (Compact Topic Dashboard Header)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFFFD8A8)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Compact Topic Header Artwork
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                        ) {
                            TopicFhdCardImage(
                                topic = topic,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFF7ED),
                                            Color(0xFFFEF3C7),
                                            Color(0xFFE0F2FE)
                                        )
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Column {
                                // Compact Badges & MCQs Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFFFFBEB),
                                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                        ) {
                                            Text(
                                                text = "Unit ${unit.unitNumber.takeIf { it > 0 } ?: 1}",
                                                color = Color(0xFFB45309),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (!topic.tagBadge.isNullOrEmpty()) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFFFEDD5),
                                                border = BorderStroke(1.dp, Color(0xFFFED7AA))
                                            ) {
                                                Text(
                                                    text = topic.tagBadge!!,
                                                    color = Color(0xFFEA580C),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                    ) {
                                        Text(
                                            text = "$totalMcqs MCQs",
                                            color = Color(0xFFB45309),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Main Topic Title
                                Text(
                                    text = topic.name,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (!topic.subtitle.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = topic.subtitle!!,
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. TOPIC PRACTICE SETS BANNER (7 Sets Available)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFEDD5),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        tint = Color(0xFFEA580C),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Topic Practice Sets",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "10 MCQs organized sequentially per set",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${practiceSets.size} Sets Available",
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. PRACTICE PROGRESS BAR
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                            Text(
                                text = "PRACTICE PROGRESS",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$completedSetsCount/${practiceSets.size} sets  •  $totalMcqs questions",
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.BarChart,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar with orange/green fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            val progressFrac = if (practiceSets.isNotEmpty()) {
                                (completedSetsCount.toFloat() / practiceSets.size).coerceIn(0.14f, 1f)
                            } else 0.14f

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFrac)
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFEA580C),
                                                Color(0xFF10B981)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // 5. 2-COLUMN GRID OF SETS (Set 1, Set 2, Set 3, Set 4, Set 5, Set 6, Set 7)
            // Note: Exactly matches the layout in user reference image (clean card styling without side image)
            val chunkedSets = practiceSets.chunked(2)
            chunkedSets.forEach { pair ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val setA = pair[0]
                        Box(modifier = Modifier.weight(1f)) {
                            GridSetCard(
                                pSet = setA,
                                onStart = { onStartSet(setA) }
                            )
                        }

                        if (pair.size > 1) {
                            val setB = pair[1]
                            Box(modifier = Modifier.weight(1f)) {
                                GridSetCard(
                                    pSet = setB,
                                    onStart = { onStartSet(setB) }
                                )
                            }
                        } else {
                            // If odd number, fill right half with Premium Quote Card
                            PremiumQuoteCard(
                                quote = "“ A people without knowledge of their past history is like a tree without roots. ”",
                                author = "Dr. B. R. Ambedkar",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 6. LEGEND / STATUS BAR (Not Started, In Progress, Completed)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Not Started
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFEDD5),
                                modifier = Modifier.size(16.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Not Started",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Yet to begin",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // In Progress
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "In Progress",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Keep going!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Completed
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Completed",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Well done!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 7. FOOTER BANNER: Learn History • Build a Better Tomorrow
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Learn History",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "Build a Better Tomorrow",
                        color = Color(0xFFEA580C),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
}

@Composable
fun GridSetCard(
    pSet: PracticeSetModel,
    onStart: () -> Unit
) {
    // Determine card background and theme color according to set index
    val isCompleted = pSet.status == "Completed" || pSet.isCompleted
    val themeColor = when (pSet.setNumber % 3) {
        1 -> Color(0xFF10B981) // Green
        2 -> Color(0xFF2563EB) // Blue
        else -> Color(0xFFEA580C) // Orange
    }

    val cardBg = when (pSet.setNumber % 3) {
        1 -> Color(0xFFF0FDF4)
        2 -> Color(0xFFEFF6FF)
        else -> Color(0xFFFFF7ED)
    }

    val borderColor = when (pSet.setNumber % 3) {
        1 -> Color(0xFFBBF7D0)
        2 -> Color(0xFFBFDBFE)
        else -> Color(0xFFFED7AA)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top row: Circle number + Done checkmark (if completed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = themeColor,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${pSet.setNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }

                if (isCompleted) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Set Title & Subtitle
            Text(
                text = "Set ${pSet.setNumber}",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )

            Text(
                text = pSet.subtitle,
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Button: "✓ Done" or "▶ Start"
            if (isCompleted) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF10B981),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStart() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Done",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = themeColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStart() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Start",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumQuoteCard(
    quote: String = "“ A people without knowledge of their past history is like a tree without roots. ”",
    author: String = "Dr. B. R. Ambedkar",
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF59E0B),
                    Color(0xFFFDE68A),
                    Color(0xFFB45309)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF172554)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(500f, 500f)
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
            }

            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = Color(0xFFFBBF24).copy(alpha = 0.15f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Text(
                        text = "INSIGHT",
                        color = Color(0xFFFBBF24),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = quote,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFBBF24).copy(alpha = 0.15f),
                        border = BorderStroke(0.8.dp, Color(0xFFFBBF24).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "– $author",
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
