package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedQuestionsScreen(
    savedQuestions: List<Question>,
    onBack: () -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onPracticeSaved: (List<Question>, String) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf("all") }
    var expandedQuestionIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var questionToDelete by remember { mutableStateOf<Question?>(null) }

    val distinctSubjectIds = remember(savedQuestions) {
        listOf("all") + savedQuestions.map { it.subjectId }.distinct().filter { it.isNotBlank() }
    }

    val filteredQuestions = remember(savedQuestions, searchQuery, selectedSubjectId) {
        savedQuestions.filter { q ->
            val matchesSubject = selectedSubjectId == "all" || q.subjectId.equals(selectedSubjectId, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    q.questionText.contains(searchQuery, ignoreCase = true) ||
                    q.subtopic.contains(searchQuery, ignoreCase = true) ||
                    q.topicId.contains(searchQuery, ignoreCase = true) ||
                    q.options.values.any { it.contains(searchQuery, ignoreCase = true) }
            matchesSubject && matchesSearch
        }
    }

    // Delete Confirmation Dialog
    if (questionToDelete != null) {
        val target = questionToDelete!!
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = {
                Text(
                    text = "Remove from Saved?",
                    fontWeight = FontWeight.Bold,
                    color = AppTextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this question from your saved bookmarks?",
                    color = AppTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveBookmark(target.id)
                        questionToDelete = null
                        Toast.makeText(context, "Question removed from Saved Questions", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Remove", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { questionToDelete = null }) {
                    Text("Cancel", color = AppTextDark)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    AppLuminousBackground {
        Scaffold(
            topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = AppOrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Saved Questions",
                                color = AppTextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "${savedQuestions.size} Bookmarked for Revision",
                            color = Color(0xFF2563EB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppTextDark
                        )
                    }
                },
                actions = {
                    if (filteredQuestions.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = {
                                onPracticeSaved(filteredQuestions, "Saved Questions Revision Test")
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFEFF6FF),
                                contentColor = Color(0xFF2563EB)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Practice (${filteredQuestions.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (filteredQuestions.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, AppLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${filteredQuestions.size} Questions Selected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppTextDark
                            )
                            Text(
                                text = "Timed practice mode with solutions",
                                fontSize = 11.sp,
                                color = AppTextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                onPracticeSaved(filteredQuestions, "Saved Questions Practice Sprint")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Revision Test", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search saved questions, topics, keywords...", color = AppTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = AppTextMuted, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppTextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = AppLightBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Subject Filter Chips
            if (distinctSubjectIds.size > 1) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(distinctSubjectIds) { subjectId ->
                            val isSelected = selectedSubjectId == subjectId
                            val label = when (subjectId) {
                                "all" -> "All Saved (${savedQuestions.size})"
                                "subj-history" -> "History"
                                "subj-society" -> "Society"
                                "subj-geography" -> "Geography"
                                "subj-current-affairs" -> "Current Affairs"
                                "subj-polity" -> "Polity"
                                "subj-economy" -> "Economy"
                                "subj-science" -> "Science & Tech"
                                else -> subjectId.replace("subj-", "").replaceFirstChar { it.uppercase() }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubjectId = subjectId },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2563EB),
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = AppTextDark
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF2563EB) else AppLightBorder
                                )
                            )
                        }
                    }
                }
            }

            // Empty State
            if (filteredQuestions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AppLightBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFEDE5),
                                border = BorderStroke(1.dp, Color(0xFFFFD4C2)),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = AppOrangePrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedSubjectId != "all")
                                    "No matching saved questions found"
                                else
                                    "No Saved Questions Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = AppTextDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedSubjectId != "all")
                                    "Try adjusting your search query or subject filters to find your bookmarks."
                                else
                                    "While practicing MCQs in the Practice Arena, Daily Quiz, or reviewing Mock Tests, tap the bookmark icon (🔖) to save important questions for later revision.",
                                fontSize = 13.sp,
                                color = AppTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onNavigateToExplore,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explore Syllabus & Practice MCQs", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // List of Saved Question Cards
                items(filteredQuestions, key = { it.id }) { question ->
                    val isExpanded = expandedQuestionIds.contains(question.id)

                    SavedQuestionCard(
                        question = question,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedQuestionIds = if (isExpanded) {
                                expandedQuestionIds - question.id
                            } else {
                                expandedQuestionIds + question.id
                            }
                        },
                        onPracticeSingle = {
                            onPracticeSaved(listOf(question), "Saved Question Practice")
                        },
                        onDeleteClick = {
                            questionToDelete = question
                        },
                        onShareClick = {
                            val shareBody = buildString {
                                append("📌 APPSC Group 2 MCQ Practice\n\n")
                                append(question.questionText)
                                append("\n\n")
                                append("A) ${question.options["A"]}\n")
                                append("B) ${question.options["B"]}\n")
                                append("C) ${question.options["C"]}\n")
                                append("D) ${question.options["D"]}\n\n")
                                append("✅ Correct Answer: Option ${question.correctOption}\n")
                                if (!question.explanation.isNullOrBlank()) {
                                    append("📖 Explanation: ${question.explanation}\n")
                                }
                            }
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "APPSC Group 2 Saved MCQ")
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Question"))
                        }
                    )
                }
            }
        }
    }
}
}

@Composable
private fun SavedQuestionCard(
    question: Question,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPracticeSingle: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AppLightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject Badge, Subtopic, and Delete Bookmark button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = when (question.subjectId) {
                                "subj-history" -> "History"
                                "subj-society" -> "Society"
                                "subj-geography" -> "Geography"
                                "subj-current-affairs" -> "Current Affairs"
                                "subj-polity" -> "Polity"
                                "subj-economy" -> "Economy"
                                "subj-science" -> "Science & Tech"
                                else -> "General"
                            },
                            color = Color(0xFF2563EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (question.subtopic.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•  ${question.subtopic}",
                            color = AppTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = AppTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkRemove,
                            contentDescription = "Remove Bookmark",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                color = AppTextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options with Correct Answer Highlighted
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("A", "B", "C", "D").forEach { optKey ->
                    val optText = question.options[optKey] ?: ""
                    if (optText.isNotBlank()) {
                        val isCorrect = optKey.equals(question.correctOption, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCorrect) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isCorrect) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = optKey,
                                            color = if (isCorrect) Color.White else AppTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = optText,
                                    color = if (isCorrect) Color(0xFF15803D) else AppTextDark,
                                    fontSize = 13.sp,
                                    fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct Answer",
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Collapsible Explanation Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F9FF))
                        .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detailed Solution & Explanation / వివరణ",
                            color = Color(0xFF0369A1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (!question.explanation.isNullOrBlank()) {
                            question.explanation
                        } else {
                            "Option ${question.correctOption} is the correct answer based on official APPSC key standards."
                        },
                        color = Color(0xFF0F172A),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Bottom Actions: Toggle Explanation & Practice This Question
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleExpand,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Hide Solution" else "View Solution / వివరణ",
                        fontSize = 12.sp,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalButton(
                    onClick = onPracticeSingle,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFFFEDE5),
                        contentColor = AppOrangePrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
