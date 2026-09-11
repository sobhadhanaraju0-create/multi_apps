package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.PracticeSetModel
import com.example.model.Subject
import com.example.model.TopicModel
import com.example.model.UnitModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

private fun getDisplayUnitName(name: String, number: Int): String {
    val clean = name.replace(Regex("(?i)^unit\\s*\\d+\\s*:?\\s*"), "").trim()
    return if (clean.isNotBlank()) clean else "Unit $number"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusExplorerScreen(
    subject: Subject,
    onBack: () -> Unit,
    onSelectTopic: (UnitModel, TopicModel) -> Unit,
    onNavigateToHome: () -> Unit = onBack,
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToCurrentAffairs: () -> Unit = {},
    onNavigateToMockTests: () -> Unit = {},
    onNavigateToDailyQuiz: () -> Unit = {},
    onNavigateToSavedQuestions: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToPerformance: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State for selected unit (null = Subject overview with Units, non-null = Unit's Topics view)
    var selectedUnit by remember { mutableStateOf<UnitModel?>(null) }
    var sortBy by remember { mutableStateOf("default") } // "default", "topics", "progress"
    var showSortMenu by remember { mutableStateOf(false) }
    var showPdfDialog by remember { mutableStateOf(false) }
    var showUnitPdfDialog by remember { mutableStateOf(false) }
    var selectedTopicForNotes by remember { mutableStateOf<TopicModel?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val totalTopicsCount = subject.units.sumOf { it.topics.size }
    val subjectMcqs = SampleData.getQuestionsCountForSubject(subject.id)
    val totalMcqsDisplay = if (subjectMcqs > 0) "$subjectMcqs+" else "${(totalTopicsCount * 25).coerceAtLeast(100)}+"

    // Subject Hero Image Fallback
    val heroImageUrl = if (subject.imageUrl.isNotBlank()) subject.imageUrl
    else when (subject.id) {
        "subj-indian-history" -> "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80"
        "subj-geography" -> "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80"
        "subj-society" -> "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1200&q=80"
        "subj-mental-ability" -> "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=1200&q=80"
        "subj-ap-history" -> "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80"
        "subj-constitution" -> "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=1200&q=80"
        "subj-economy" -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80"
        "subj-science-tech" -> "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80"
    }

    // Default Fallback Unit Images
    val defaultUnitImages = listOf(
        "https://images.unsplash.com/photo-1600100397608-f40b2e3e9d89?auto=format&fit=crop&w=800&q=80", // Ancient Hampi Chariot
        "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=800&q=80", // Red Fort Delhi
        "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80", // India Gate
        "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"  // Amaravati
    )

    // Sorted Units for overview
    val displayedUnits = remember(subject.units, sortBy, searchQuery) {
        var list = subject.units
        if (searchQuery.isNotBlank()) {
            list = list.filter { u ->
                u.name.contains(searchQuery, ignoreCase = true) ||
                (u.subtitle ?: "").contains(searchQuery, ignoreCase = true) ||
                u.topics.any { t -> t.name.contains(searchQuery, ignoreCase = true) }
            }
        }
        when (sortBy) {
            "topics" -> list.sortedByDescending { it.topics.size }
            "progress" -> list.sortedByDescending { it.completionPercentage ?: 0 }
            else -> list
        }
    }

    // Direct Subject Sets (No Unit, No Topic)
    val subjectSets = remember(subject.id) {
        SampleData.getSetsForHierarchyLevel(subject.id, "", "")
    }

    // PDF Syllabus Dialog (Subject level)
    if (showPdfDialog) {
        SyllabusPdfViewerDialog(
            subject = subject,
            totalTopics = totalTopicsCount,
            totalMcqs = totalMcqsDisplay,
            onDismiss = { showPdfDialog = false },
            onStartPracticeFirst = {
                showPdfDialog = false
                val firstUnit = subject.units.firstOrNull()
                val firstTopic = firstUnit?.topics?.firstOrNull()
                if (firstUnit != null && firstTopic != null) {
                    onSelectTopic(firstUnit, firstTopic)
                } else {
                    Toast.makeText(context, "Practice questions are loading...", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text(
                            text = "APPSC PREPARATION PORTAL",
                            color = Color(0xFF2563EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "APPSC Group 2 (Executive & Non-Exec)",
                            color = AppTextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "👑 Your Success Starts Here",
                            color = Color(0xFFD97706),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        item {
                            DrawerRowItem(Icons.Default.Home, "Home Dashboard", tint = Color(0xFF2563EB)) {
                                scope.launch { drawerState.close() }
                                onNavigateToHome()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.AdminPanelSettings, "Admin Portal", tint = Color(0xFFD97706)) {
                                scope.launch { drawerState.close() }
                                onNavigateToAdmin()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.Article, "Current Affairs Center", tint = Color(0xFFE11D48)) {
                                scope.launch { drawerState.close() }
                                onNavigateToCurrentAffairs()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.EmojiEvents, "Grand Mock Tests", tint = Color(0xFF1D4ED8)) {
                                scope.launch { drawerState.close() }
                                onNavigateToMockTests()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.ElectricBolt, "Daily Quiz Challenge", tint = Color(0xFF059669)) {
                                scope.launch { drawerState.close() }
                                onNavigateToDailyQuiz()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.Bookmark, "Saved MCQs", tint = Color(0xFF7C3AED)) {
                                scope.launch { drawerState.close() }
                                onNavigateToSavedQuestions()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.Leaderboard, "Leaderboard State Ranks", tint = Color(0xFF2563EB)) {
                                scope.launch { drawerState.close() }
                                onNavigateToLeaderboard()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.Analytics, "Performance Board", tint = Color(0xFF0891B2)) {
                                scope.launch { drawerState.close() }
                                onNavigateToPerformance()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.CreditCard, "Subscriptions & Pro Pass", tint = Color(0xFF7C3AED)) {
                                scope.launch { drawerState.close() }
                                onNavigateToSubscription()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.Person, "Profile & Account", tint = AppTextSecondary) {
                                scope.launch { drawerState.close() }
                                onNavigateToProfile()
                            }
                        }
                        item {
                            DrawerRowItem(Icons.Default.PictureAsPdf, "Official Syllabus PDF", tint = Color(0xFFEF4444)) {
                                scope.launch { drawerState.close() }
                                showPdfDialog = true
                            }
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder) }
                        item { DrawerRowItem(Icons.Default.HelpOutline, "FAQ & Help", tint = AppTextSecondary) {} }
                        item { DrawerRowItem(Icons.Default.MailOutline, "Contact Support", tint = AppGreenAccent) {} }
                    }
                }
            }
        }
    ) {
        AppLuminousBackground(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    // TOP BAR MATCHING SCREENSHOT EXACTLY
                    Surface(
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left Section: Hamburger + AP Circle + Title/Subtitle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Round AP Blue Circle Badge
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "AP",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Title & Subtitle
                                    Column {
                                        Text(
                                            text = "APPSC Group 2",
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            lineHeight = 18.sp
                                        )
                                        Text(
                                            text = "Executive & Non-Executive",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                            lineHeight = 14.sp
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "👑 Your Success Starts Here",
                                                color = Color(0xFFD97706),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Right Section: Search, Notification Bell with red dot, Profile "T"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { isSearchActive = !isSearchActive },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color(0xFF334155),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    // Notification with red unread dot
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clickable {
                                                Toast.makeText(context, "You have active practice modules available!", Toast.LENGTH_SHORT).show()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color(0xFF334155),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-6).dp, y = 6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEF4444))
                                        )
                                    }

                                    // Initial Avatar "T"
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.5.dp, Color(0xFFBFDBFE)),
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable {
                                                onNavigateToProfile()
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "T",
                                                color = Color(0xFF2563EB),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Horizontal Quick Feature Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.clickable { onNavigateToHome() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Home", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.clickable { onNavigateToAdmin() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Admin Portal", color = Color(0xFF92400E), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFFFE4E6),
                                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                                    modifier = Modifier.clickable { onNavigateToCurrentAffairs() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Article, contentDescription = "Current Affairs", tint = Color(0xFFE11D48), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Current Affairs", color = Color(0xFF9F1239), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFEEF2FF),
                                    border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
                                    modifier = Modifier.clickable { onNavigateToMockTests() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = "Grand Mocks", tint = Color(0xFF1D4ED8), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Grand Mocks", color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFECFDF5),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                    modifier = Modifier.clickable { onNavigateToDailyQuiz() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ElectricBolt, contentDescription = "Daily Quiz", tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Daily Quiz", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF3E8FF),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                                    modifier = Modifier.clickable { onNavigateToSavedQuestions() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = "Saved MCQs", tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Saved MCQs", color = Color(0xFF6B21A8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Search bar expand
                            AnimatedVisibility(visible = isSearchActive) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search units, topics or chapters...", fontSize = 13.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2563EB)) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF64748B))
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF2563EB),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->

                // SWITCH BETWEEN: 1. SUBJECT OVERVIEW (Screenshot 1) vs 2. UNIT'S TOPICS VIEW (Screenshot 2)
                AnimatedContent(
                    targetState = selectedUnit,
                    transitionSpec = {
                        if (targetState != null) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "UnitDetailTransition",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) { currentUnit ->
                    if (currentUnit == null) {
                        // =========================================================================
                        // VIEW A: SUBJECT UNITS OVERVIEW (SCREENSHOT 1)
                        // =========================================================================
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // 1. HERO BANNER CARD MATCHING SCREENSHOT 1
                            item {
                                Card(
                                    shape = RoundedCornerShape(22.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(210.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Background Monument/Scenic FHD Image
                                        AppFhdImage(
                                            imageUrl = heroImageUrl,
                                            contentDescription = subject.name,
                                            contentScale = ContentScale.Crop,
                                            themeCategory = subject.name,
                                            title = subject.name,
                                            subtitle = subject.heroQuote.replace("\n", " • "),
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Multi-stop Gradient overlay for dramatic contrast & clarity
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xE60F172A),
                                                            Color(0xB30F172A),
                                                            Color(0x660F172A),
                                                            Color(0x33000000)
                                                        )
                                                    )
                                                )
                                        )

                                        // Content Overlays
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Top Row: PRELIMS SYLLABUS tag & Learn Compete Achieve cursive badge
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                // Pill tag: PRELIMS SYLLABUS
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xCC0F172A),
                                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                                                ) {
                                                    Text(
                                                        text = "${subject.category.uppercase()} SYLLABUS",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        letterSpacing = 0.8.sp,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                    )
                                                }

                                                // Cursive Branding: Learn Compete Achieve
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = "Learn\nCompete\nAchieve",
                                                        color = Color.White,
                                                        fontSize = 15.sp,
                                                        fontStyle = FontStyle.Italic,
                                                        fontWeight = FontWeight.Black,
                                                        lineHeight = 16.sp,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                    // Golden curved underline arc
                                                    Box(
                                                        modifier = Modifier
                                                            .width(64.dp)
                                                            .height(3.dp)
                                                            .clip(RoundedCornerShape(2.dp))
                                                            .background(Color(0xFFFBBF24))
                                                    )
                                                }
                                            }

                                            // Bottom Area: Subject Title & Quote with vertical yellow bar
                                            Column {
                                                Text(
                                                    text = subject.name,
                                                    color = Color.White,
                                                    fontSize = 32.sp,
                                                    fontWeight = FontWeight.Black,
                                                    letterSpacing = (-0.5).sp,
                                                    lineHeight = 34.sp
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Quote with yellow vertical line
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(3.5.dp)
                                                            .height(34.dp)
                                                            .clip(RoundedCornerShape(2.dp))
                                                            .background(Color(0xFFFBBF24))
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    val quoteLines = if (subject.heroQuote.isNotBlank()) {
                                                        subject.heroQuote.split("\n")
                                                    } else {
                                                        listOf("Explore the Past", "Build a Better Future")
                                                    }

                                                    Column {
                                                        quoteLines.forEach { line ->
                                                            Text(
                                                                text = line,
                                                                color = Color.White,
                                                                fontSize = 13.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                lineHeight = 16.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. BREADCRUMBS & SYLLABUS PDF ROW
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left Breadcrumbs: Dashboard > Indian History
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onBack() }
                                    ) {
                                        Text(
                                            text = "Dashboard",
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = subject.name,
                                            color = Color(0xFF2563EB),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Right Pill: Syllabus PDF >
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                        modifier = Modifier.clickable { showPdfDialog = true }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PictureAsPdf,
                                                contentDescription = "PDF",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "Syllabus PDF",
                                                color = Color(0xFF2563EB),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. 4 METRIC QUICK-STAT CARDS
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricQuickStatCard(
                                        icon = Icons.Default.MenuBook,
                                        iconBg = Color(0xFFDBEAFE),
                                        iconTint = Color(0xFF2563EB),
                                        label = "Units",
                                        value = "${subject.units.size}",
                                        subtitle = "Study in Parts",
                                        cardBg = Color(0xFFF0F9FF),
                                        borderColor = Color(0xFFBAE6FD),
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricQuickStatCard(
                                        icon = Icons.Default.Description,
                                        iconBg = Color(0xFFDCFCE7),
                                        iconTint = Color(0xFF16A34A),
                                        label = "Topics",
                                        value = "$totalTopicsCount",
                                        subtitle = "Chapter-wise",
                                        cardBg = Color(0xFFF0FDF4),
                                        borderColor = Color(0xFFBBF7D0),
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricQuickStatCard(
                                        icon = Icons.Default.Adjust,
                                        iconBg = Color(0xFFFFE4E6),
                                        iconTint = Color(0xFFE11D48),
                                        label = "MCQs",
                                        value = totalMcqsDisplay,
                                        subtitle = "Practice Sets",
                                        cardBg = Color(0xFFFFF1F2),
                                        borderColor = Color(0xFFFECDD3),
                                        modifier = Modifier.weight(1f)
                                    )

                                    MetricQuickStatCard(
                                        icon = Icons.Default.EmojiEvents,
                                        iconBg = Color(0xFFFEF3C7),
                                        iconTint = Color(0xFFD97706),
                                        label = "Prelims",
                                        value = "Target",
                                        subtitle = "Exam Focus",
                                        cardBg = Color(0xFFFFFBEB),
                                        borderColor = Color(0xFFFDE68A),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }



                            // 5. SECTION HEADER WITH SORT BUTTON
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFEFF6FF),
                                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Layers,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Units",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 17.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "Explore units and strengthen your preparation",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    // Sort Button
                                    Box {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                            modifier = Modifier.clickable { showSortMenu = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SwapVert,
                                                    contentDescription = "Sort",
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Sort",
                                                    color = Color(0xFF2563EB),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Default Order") },
                                                onClick = { sortBy = "default"; showSortMenu = false },
                                                leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Most Topics") },
                                                onClick = { sortBy = "topics"; showSortMenu = false },
                                                leadingIcon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Highest Completion") },
                                                onClick = { sortBy = "progress"; showSortMenu = false },
                                                leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null) }
                                            )
                                        }
                                    }
                                }
                            }

                            // 5b. DIRECT SUBJECT SETS (From JSON Importer without Unit)
                            if (subjectSets.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF59E0B),
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Subject-Level Sets",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Imported practice sets directly linked to ${subject.name}.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // We can show them in chunks of 2 for grid style
                                val chunkedSets = subjectSets.chunked(2)
                                items(chunkedSets.size) { chunkIndex ->
                                    val rowItems = chunkedSets[chunkIndex]
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            InlineGridSetCard(rowItems[0]) {
                                                // Normally navigates to QuizScreen, but we don't have it mapped yet
                                                // So we can show a toast
                                                Toast.makeText(context, "Starting ${rowItems[0].title}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        if (rowItems.size > 1) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                InlineGridSetCard(rowItems[1]) {
                                                    Toast.makeText(context, "Starting ${rowItems[1].title}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                item {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))
                                }
                            }

                            // 6. UNITS LIST VIEW (SUBJECT-STYLE FHD CARDS)
                            itemsIndexed(displayedUnits) { index, unit ->
                                val topicCount = unit.topics.size
                                val questionCount = SampleData.getQuestionsCountForUnit(subject.id, unit.id)
                                val unitMcqTotal = if (questionCount > 0) questionCount else (topicCount * 25).coerceAtLeast(100)

                                 val animProgress = remember { androidx.compose.animation.core.Animatable(0f) }
                                 LaunchedEffect(Unit) {
                                     kotlinx.coroutines.delay((index * 60).toLong().coerceAtMost(300L))
                                     animProgress.animateTo(
                                         targetValue = 1f,
                                         animationSpec = androidx.compose.animation.core.tween(
                                             durationMillis = 400,
                                             easing = androidx.compose.animation.core.FastOutSlowInEasing
                                         )
                                     )
                                 }

                                Card(
                                    shape = RoundedCornerShape(22.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(138.dp)
                                        .graphicsLayer {
                                             alpha = animProgress.value
                                             translationY = (1f - animProgress.value) * 40f
                                         }
                                         .clickable {
                                            selectedUnit = unit
                                        },
                                    border = BorderStroke(1.8.dp, Color(0xFF2563EB)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Full Bleed FHD Unit Image
                                        UnitFhdCardImage(
                                            unit = unit,
                                            subjectName = subject.name,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Dark gradient overlay at the bottom for crystal-clear readability
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Black.copy(alpha = 0.15f),
                                                            Color.Transparent,
                                                            Color(0x990A192F),
                                                            Color(0xF00A192F)
                                                        )
                                                    )
                                                )
                                        )

                                        // Top Badges Row over image
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp)
                                                .align(Alignment.TopCenter),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xDD0F172A),
                                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Unit ${unit.unitNumber}",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xDD0F172A),
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Bottom Information Row
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                                .align(Alignment.BottomStart)
                                        ) {
                                            Text(
                                                text = getDisplayUnitName(unit.name, unit.unitNumber),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color.White.copy(alpha = 0.2f)
                                                    ) {
                                                        Text(
                                                            text = "$topicCount Topics",
                                                            fontSize = 11.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFFEF3C7).copy(alpha = 0.95f)
                                                    ) {
                                                        Text(
                                                            text = "$unitMcqTotal+ MCQs",
                                                            fontSize = 11.sp,
                                                            color = Color(0xFFB45309),
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Explore",
                                                        color = Color(0xFFFBBF24),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 7. BOTTOM MOTIVATIONAL CARD WITH ORANGE BUTTON
                            item {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Glowing Lightbulb Icon
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFFEF3C7),
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lightbulb,
                                                        contentDescription = null,
                                                        tint = Color(0xFFF59E0B),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = "History is not just about the past,",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                                Text(
                                                    text = "it prepares you for a better tomorrow!",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Orange Start Practice Button
                                        Button(
                                            onClick = {
                                                val firstU = subject.units.firstOrNull()
                                                val firstT = firstU?.topics?.firstOrNull()
                                                if (firstU != null && firstT != null) {
                                                    onSelectTopic(firstU, firstT)
                                                }
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFEA580C)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Start Practice",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 8. WATERMARK FOOTER ARTWORK
                            item {
                                FooterWatermarkArtwork()
                            }
                        }
                    } else {
                        // =========================================================================
                        // VIEW B: UNIT'S TOPICS DETAIL VIEW (MATCHING SCREENSHOT 2 EXACTLY!)
                        // =========================================================================
                        val unit = currentUnit
                        val unitTopicsCount = unit.topics.size
                        val unitMcqsDisplay = unit.estimatedMcqs ?: "${(unitTopicsCount * 25).coerceAtLeast(80)}+"
                        val unitProgressPct = unit.completionPercentage ?: 40

                        val unitHeroImage = unit.imageUrl?.ifBlank { null }
                            ?: defaultUnitImages.getOrElse((unit.unitNumber - 1).coerceAtLeast(0) % defaultUnitImages.size) { defaultUnitImages[0] }

                        val unitTagline = unit.tagline ?: "Roots of Our Great Civilization"
                        val unitQuote = unit.quote ?: "“Know Your Past, Shape a Brighter Future”"
                        val unitSlogan = unit.slogan ?: "History\nBuilds\nWisdom"

                        // 4b. DIRECT UNIT SETS (From JSON Importer without Topic)
                        val unitSets = remember(unit.id) { SampleData.getSetsForHierarchyLevel(subject.id, unit.id, "") }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // 1. HERO BANNER CARD FOR SELECTED UNIT MATCHING SCREENSHOT 2
                            item {
                                Card(
                                    shape = RoundedCornerShape(22.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(210.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Background Monument/Scenic Ruins FHD Image
                                        UnitFhdCardImage(
                                            unit = unit,
                                            subjectName = subject.name,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // High-contrast Gradient Overlay
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xEE0F172A),
                                                            Color(0xB30F172A),
                                                            Color(0x660F172A),
                                                            Color(0x33000000)
                                                        )
                                                    )
                                                )
                                        )

                                        // Inner Overlays
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Top Row: Circular Back Arrow `<` + UNIT 1 Pill + Right Slogan
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    // Circular Back Button
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color.White.copy(alpha = 0.9f),
                                                        shadowElevation = 2.dp,
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clickable { selectedUnit = null }
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                                contentDescription = "Back to Units",
                                                                tint = Color(0xFF0F172A),
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }

                                                    // Pill: UNIT 1
                                                    Surface(
                                                        shape = RoundedCornerShape(14.dp),
                                                        color = Color(0xCCFEF3C7),
                                                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                                    ) {
                                                        Text(
                                                            text = "UNIT ${unit.unitNumber}",
                                                            color = Color(0xFF92400E),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Black,
                                                            letterSpacing = 0.8.sp,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }

                                                // Top Right Slogan: History Builds Wisdom
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = unitSlogan,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontStyle = FontStyle.Italic,
                                                        fontWeight = FontWeight.Black,
                                                        lineHeight = 15.sp,
                                                        letterSpacing = 0.4.sp
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .width(54.dp)
                                                            .height(3.dp)
                                                            .clip(RoundedCornerShape(2.dp))
                                                            .background(Color(0xFFFBBF24))
                                                    )
                                                }
                                            }

                                            // Bottom Section: Title, Subtitle, Quote, Accent Bar
                                            Column {
                                                Text(
                                                    text = unit.name,
                                                    color = Color.White,
                                                    fontSize = 26.sp,
                                                    fontWeight = FontWeight.Black,
                                                    letterSpacing = (-0.5).sp,
                                                    lineHeight = 28.sp
                                                )

                                                Spacer(modifier = Modifier.height(2.dp))

                                                Text(
                                                    text = unitTagline,
                                                    color = Color(0xFFE2E8F0),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = unitQuote,
                                                    color = Color(0xFFCBD5E1),
                                                    fontSize = 12.sp,
                                                    fontStyle = FontStyle.Italic
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Golden Accent underline
                                                Box(
                                                    modifier = Modifier
                                                        .width(90.dp)
                                                        .height(3.5.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(
                                                            Brush.horizontalGradient(
                                                                colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B), Color.Transparent)
                                                            )
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. BREADCRUMBS: Dashboard > Indian History > Unit 1 & UNIT NOTES PDF
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Breadcrumb links
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Text(
                                            text = "Dashboard",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.clickable { onBack() }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = subject.name,
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.clickable { selectedUnit = null }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Unit ${unit.unitNumber}",
                                            color = Color(0xFF2563EB),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                 }
                             }

                             // 3. 4 METRIC QUICK-STAT CARDS FOR UNIT
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // 1. Topics
                                    MetricQuickStatCard(
                                        icon = Icons.Default.MenuBook,
                                        iconBg = Color(0xFFDBEAFE),
                                        iconTint = Color(0xFF2563EB),
                                        label = "Topics",
                                        value = "$unitTopicsCount",
                                        subtitle = "In this Unit",
                                        cardBg = Color(0xFFF0F9FF),
                                        borderColor = Color(0xFFBAE6FD),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // 2. Total MCQs
                                    MetricQuickStatCard(
                                        icon = Icons.Default.Description,
                                        iconBg = Color(0xFFDCFCE7),
                                        iconTint = Color(0xFF16A34A),
                                        label = "Total MCQs",
                                        value = unitMcqsDisplay,
                                        subtitle = "Practice Questions",
                                        cardBg = Color(0xFFF0FDF4),
                                        borderColor = Color(0xFFBBF7D0),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // 3. Your Progress (with mini progress bar)
                                    ProgressMetricCard(
                                        icon = Icons.Default.Adjust,
                                        iconBg = Color(0xFFFFE4E6),
                                        iconTint = Color(0xFFE11D48),
                                        label = "Your Progress",
                                        progressPct = unitProgressPct,
                                        cardBg = Color(0xFFFFF1F2),
                                        borderColor = Color(0xFFFECDD3),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // 4. Target Prelims
                                    MetricQuickStatCard(
                                        icon = Icons.Default.EmojiEvents,
                                        iconBg = Color(0xFFFEF3C7),
                                        iconTint = Color(0xFFD97706),
                                        label = "Target",
                                        value = "Prelims",
                                        subtitle = "Score High",
                                        cardBg = Color(0xFFFFFBEB),
                                        borderColor = Color(0xFFFDE68A),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // 4. SECTION HEADER: "Topics in this Unit"
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFEFF6FF),
                                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Layers,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Topics in this Unit",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 17.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "Tap on a topic to start practicing",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }

                            // 4b. DIRECT UNIT SETS (From JSON Importer without Topic)
                            if (unitSets.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF59E0B),
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Unit-Level Sets",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Imported practice sets directly linked to ${unit.name}.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                val chunkedUnitSets = unitSets.chunked(2)
                                items(chunkedUnitSets.size) { chunkIndex ->
                                    val rowItems = chunkedUnitSets[chunkIndex]
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            InlineGridSetCard(rowItems[0]) {
                                                Toast.makeText(context, "Starting ${rowItems[0].title}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        if (rowItems.size > 1) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                InlineGridSetCard(rowItems[1]) {
                                                    Toast.makeText(context, "Starting ${rowItems[1].title}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                item {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))
                                }
                            }

                            // 5. TOPIC CARDS MATCHING SCREENSHOT 2 EXACTLY
                            itemsIndexed(unit.topics) { index, topic ->
                                val (badgeColor, progressColor, defaultTopicPct, defaultDifficulty) = when (index % 3) {
                                    0 -> listOf(Color(0xFF2563EB), Color(0xFF10B981), 60, "Medium") // Blue, Green 60%, Medium
                                    1 -> listOf(Color(0xFF8B5CF6), Color(0xFFF59E0B), 30, "Medium") // Purple, Amber 30%, Medium
                                    else -> listOf(Color(0xFF10B981), Color(0xFFEF4444), 20, "Easy") // Green, Coral/Red 20%, Easy
                                }

                                val bColor = badgeColor as Color
                                val pColor = progressColor as Color
                                val compPct = if (topic.completionPercentage > 0) topic.completionPercentage else (defaultTopicPct as Int)
                                val difficulty = topic.difficulty.ifBlank { defaultDifficulty as String }
                                val mcqsTag = topic.estimatedMcqs ?: "${(index + 2) * 10}+ MCQs"
                                val setsCount = topic.practiceSetsCount.coerceAtLeast(2)

                                // Fallback topic images matching screenshot (Mohenjo-daro, Vedic sage, Buddha statue)
                                val topicImage = topic.imageUrl?.ifBlank { null }
                                    ?: when (index % 3) {
                                        0 -> "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=800&q=80"
                                        1 -> "https://images.unsplash.com/photo-1544717305-2782549b5136?auto=format&fit=crop&w=800&q=80"
                                        else -> "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=800&q=80"
                                    }

                                val topicTagBadge = topic.tagBadge ?: when (index % 3) {
                                    0 -> "Harappan Culture"
                                    1 -> "Vedic Age"
                                    else -> "Spread of Buddhism"
                                }

                                 val topicAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                                 LaunchedEffect(Unit) {
                                     kotlinx.coroutines.delay((index * 60).toLong().coerceAtMost(300L))
                                     topicAnim.animateTo(
                                         targetValue = 1f,
                                         animationSpec = androidx.compose.animation.core.tween(
                                             durationMillis = 400,
                                             easing = androidx.compose.animation.core.FastOutSlowInEasing
                                         )
                                     )
                                 }

                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(125.dp)
                                        .graphicsLayer {
                                             alpha = topicAnim.value
                                             translationY = (1f - topicAnim.value) * 40f
                                         }
                                         .clickable {
                                            onSelectTopic(unit, topic)
                                        },
                                    border = BorderStroke(1.5.dp, bColor.copy(alpha = 0.5f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Full Bleed FHD Topic Image
                                        TopicFhdCardImage(
                                            topic = topic,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Dark gradient overlay at the bottom for crystal-clear readability
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Black.copy(alpha = 0.15f),
                                                            Color.Transparent,
                                                            Color(0x990A192F),
                                                            Color(0xF00A192F)
                                                        )
                                                    )
                                                )
                                        )

                                        // Top Badges Row over image
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                                .align(Alignment.TopCenter),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xDD0F172A),
                                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                            ) {
                                                Text(
                                                    text = "Chapter ${index + 1} • $topicTagBadge",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = bColor,
                                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                            ) {
                                                Text(
                                                    text = mcqsTag,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        // Bottom Information Row
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                                .align(Alignment.BottomStart)
                                        ) {
                                            Text(
                                                text = topic.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "$setsCount Practice Sets • $difficulty",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFE2E8F0)
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Practice",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFBBF24)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 6. BOTTOM MOTIVATIONAL CARD: "Small Steps Today, Big Success Tomorrow!"
                            item {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Glowing Bulb
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFFEF3C7),
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lightbulb,
                                                        contentDescription = null,
                                                        tint = Color(0xFFF59E0B),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = "Small Steps Today, Big Success Tomorrow!",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                                Text(
                                                    text = "Keep practicing and make history with your score.",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Orange Continue Learning Button
                                        Button(
                                            onClick = {
                                                val firstT = unit.topics.firstOrNull()
                                                if (firstT != null) {
                                                    onSelectTopic(unit, firstT)
                                                }
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFEA580C)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Continue Learning",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 7. FOOTER WATERMARK WITH "Proud to be Indian" TRICOLOR EMBLEM
                            item {
                                FooterWatermarkWithTricolor()
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// STAT CARD COMPONENT WITH METRICS
// =========================================================================
@Composable
private fun MetricQuickStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    subtitle: String,
    cardBg: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                lineHeight = 18.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                maxLines = 1
            )
        }
    }
}

// Progress Stat Card (e.g. "Your Progress 40%" with mini progress bar)
@Composable
private fun ProgressMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    progressPct: Int,
    cardBg: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$progressPct%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                lineHeight = 18.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mini progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFCBD5E1))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressPct / 100f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF10B981))
                )
            }
        }
    }
}

// =========================================================================
// FOOTER WATERMARK ARTWORKS
// =========================================================================
@Composable
private fun FooterWatermarkArtwork() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color(0xFF93C5FD).copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Learn  Compete  Achieve",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E3A8A).copy(alpha = 0.65f)
                )
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFFBBF24))
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "For a Better\nAndhra Pradesh",
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A).copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun FooterWatermarkWithTricolor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Cursive Learn Practice Compete Achieve
            Text(
                text = "Learn  •  Practice  •  Compete  •  Achieve",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E3A8A).copy(alpha = 0.65f)
            )

            // Right Proud to be Indian badge with tricolor
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Proud to be\nIndian",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E3A8A)
                )
                // Tricolor gradient line (Saffron, White, Green)
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF9933), Color(0xFFFFFFFF), Color(0xFF138808))
                            )
                        )
                )
            }
        }
    }
}

// =========================================================================
// SYLLABUS PDF VIEWER DIALOG (SUBJECT LEVEL)
// =========================================================================
@Composable
private fun SyllabusPdfViewerDialog(
    subject: Subject,
    totalTopics: Int,
    totalMcqs: String,
    onDismiss: () -> Unit,
    onStartPracticeFirst: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFE4E6),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OFFICIAL SYLLABUS PDF",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = subject.name,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📋 Document Highlights:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Standard: APPSC Group 2 Syllabus (Latest Notification)", fontSize = 12.sp, color = Color(0xFF475569))
                        Text("• Total Units: ${subject.units.size} Units (${totalTopics} Chapters)", fontSize = 12.sp, color = Color(0xFF475569))
                        Text("• Practice Questions: $totalMcqs Verified MCQs", fontSize = 12.sp, color = Color(0xFF475569))
                        Text("• Medium: Telugu & English Medium", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Downloading Official Syllabus PDF...", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Drawer Row Item
@Composable
fun DrawerRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = AppTextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InlineGridSetCard(
    pSet: PracticeSetModel,
    onStart: () -> Unit
) {
    val isCompleted = pSet.status == "Completed" || pSet.isCompleted
    val themeColor = when (pSet.setNumber % 3) {
        1 -> Color(0xFF10B981)
        2 -> Color(0xFF2563EB)
        else -> Color(0xFFEA580C)
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
        modifier = Modifier.fillMaxWidth().clickable { onStart() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = themeColor, modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("${pSet.setNumber}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
                if (isCompleted) {
                    Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(20.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp)) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(pSet.title, color = Color(0xFF0F172A), fontWeight = FontWeight.Black, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(pSet.subtitle, color = Color(0xFF64748B), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = if (isCompleted) Color(0xFF10B981) else themeColor, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(vertical = 7.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isCompleted) Icons.Default.Check else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isCompleted) "Done" else "Start", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
