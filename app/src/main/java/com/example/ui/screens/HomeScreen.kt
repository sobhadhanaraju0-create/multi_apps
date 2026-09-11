package com.example.ui.screens

import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.data.SampleData
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSubject: (Subject) -> Unit,
    onNavigateToCurrentAffairs: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToScratchpad: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToMockTests: () -> Unit,
    onNavigateToDailyQuiz: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSavedQuestions: () -> Unit = {}
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentAppId by remember { SampleData.currentActiveAppId }
    val activeApp = SampleData.appsListState.find { it.id == currentAppId } ?: SampleData.getActiveApp()

    val activePosters = remember(currentAppId, SampleData.postersState.size, SampleData.postersState.map { it.isActive }) {
        SampleData.getPostersForApp(currentAppId)
            .filter { it.isActive && it.getStatus() == PromotionStatus.ACTIVE }
            .sortedBy { it.priorityOrder }
    }

    val displaySubjects = remember(currentAppId, SampleData.subjectsState.size) {
        val appSubjs = SampleData.getSubjectsForApp(currentAppId)
        if (appSubjs.isNotEmpty()) appSubjs else SampleData.subjectsState
    }

    var showAppSwitcherDialog by remember { mutableStateOf(false) }
    var textScaleFactor by remember { mutableFloatStateOf(1.0f) }
    var showFontSettingsDialog by remember { mutableStateOf(false) }

    // Font Settings Dialog
    if (showFontSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showFontSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.TextFields, contentDescription = null, tint = Color(0xFF2563EB))
                    Text("Font & Text Settings", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Customize text size for comfortable reading across the app.", color = Color(0xFF64748B), fontSize = 13.sp)
                    
                    Text("Select Font Size Scale:", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 13.sp)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf(0.9f to "Small", 1.0f to "Normal", 1.15f to "Large", 1.3f to "XL").forEach { (scale, label) ->
                            val isSelected = textScaleFactor == scale
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF1D4ED8) else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .clickable { textScaleFactor = scale }
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(label, color = if (isSelected) Color.White else Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Live Preview:", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 12.sp)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Sample interface preview text with scale ${(textScaleFactor * 100).toInt()}%",
                                color = Color(0xFF0F172A),
                                fontSize = (14 * textScaleFactor).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFontSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Apply Settings", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFFFFFBEB)
        )
    }

    if (showAppSwitcherDialog) {
        AlertDialog(
            onDismissRequest = { showAppSwitcherDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Exam App", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleData.appsListState) { app ->
                        val isSelected = app.id == currentAppId
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    SampleData.setActiveApp(app.id)
                                    showAppSwitcherDialog = false
                                    Toast.makeText(context, "Switched to ${app.name}", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${app.examCategory} • ${app.code}", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppSwitcherDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PremiumNavyBgDeep,
                modifier = Modifier.width(310.dp)
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIconBadge(size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "APPSC MCQ Portal",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Group 2 & State PSC Prep",
                                    color = PremiumGoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PremiumGoldPrimary.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, PremiumGoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "⚡ Full Native Suite Active",
                                color = PremiumGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = { Text("Syllabus Home", color = AppTextDark, fontWeight = FontWeight.Bold) },
                            selected = true,
                            onClick = { scope.launch { drawerState.close() } }
                        )

                        // Highlighted features moved to top-left menu
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = AppLightBorder)
                        Text(
                            text = "EXAM HUB & PRACTICE",
                            color = Color(0xFF2563EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.SportsScore, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Grand Mock Tests", color = AppTextDark, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = PremiumGoldPrimary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("HOT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToMockTests()
                            }
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Newspaper, contentDescription = null, tint = Color(0xFF2563EB)) },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Current Affairs", color = AppTextDark, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = Color(0xFF2563EB),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("DAILY", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToCurrentAffairs()
                            }
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.BarChart, contentDescription = null, tint = AppGreenAccent) },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Performance & Analytics", color = AppTextDark, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = AppGreenAccent,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToPerformance()
                            }
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = { Text("Daily Quiz Sprint", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToDailyQuiz()
                            }
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = AppOrangePrimary) },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Saved Questions", color = AppTextDark, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = Color(0xFFFFEDE5),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("REVISION", color = AppOrangePrimary, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToSavedQuestions()
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = AppLightBorder)
                        Text(
                            text = "COMMUNITY & TOOLS",
                            color = Color(0xFF2563EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = { Text("State Leaderboard & Ranks", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToLeaderboard()
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8)) },
                            label = { Text("My Profile & Settings", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToProfile()
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = { Text("Pro Subscriptions (4 Plans)", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToSubscription()
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF2563EB)) },
                            label = { Text("Scratchpad & Calculator", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToScratchpad()
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AppGreenAccent) },
                            label = { Text("Admin Panel (Manage Content)", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToAdmin()
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = PremiumGoldPrimary) },
                            label = { Text("Sign In / Switch User", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToLogin()
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder)
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = AppTextSecondary) },
                            label = { Text("Privacy Policy & Terms", color = AppTextDark) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToPrivacy()
                            }
                        )
                    }
                }
            }
        }
    ) {
        AppLuminousBackground {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showAppSwitcherDialog = true }
                            ) {
                                AppIconBadge(size = 38.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeApp.name,
                                            color = Color(0xFF0F172A),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Switch App",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = activeApp.tagline.ifEmpty { "Competitive Exam MCQ Portal" },
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFFF97316), modifier = Modifier.size(26.dp))
                            }
                        },
                        actions = {
                            // Font & Text Settings Button (T)
                            IconButton(onClick = { showFontSettingsDialog = true }) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "T",
                                            color = Color(0xFF2563EB),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = onNavigateToAdmin) {
                                Icon(Icons.Default.ChatBubble, contentDescription = "Support & Help", tint = Color(0xFF22C55E), modifier = Modifier.size(22.dp))
                            }
                            IconButton(onClick = onNavigateToProfile) {
                                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF0EA5E9), modifier = Modifier.size(24.dp))
                            }
                            IconButton(onClick = onNavigateToSubscription) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = "Pro Plans", tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Promotional & Festival Posters Single Auto-sliding Banner (Top of Home Screen)
                    if (activePosters.isNotEmpty()) {
                        item {
                            val pagerState = rememberPagerState(pageCount = { activePosters.size })

                            // Auto-slide every 5 seconds with smooth cross-fade animation
                            LaunchedEffect(pagerState, activePosters.size) {
                                while (true) {
                                    delay(5000L)
                                    if (activePosters.isNotEmpty()) {
                                        val nextPage = (pagerState.currentPage + 1) % activePosters.size
                                        pagerState.animateScrollToPage(
                                            page = nextPage,
                                            animationSpec = tween(
                                                durationMillis = 800,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Single Poster View with Smooth Cross-Fade Animation
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(185.dp)
                                ) { page ->
                                    val poster = activePosters[page]
                                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)

                                    PromotionalPosterCard(
                                        poster = poster,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .graphicsLayer {
                                                // Smooth cross-fade alpha
                                                alpha = 1f - absOffset
                                                // Keep banner stationary so it cross-fades in-place instead of horizontal slide
                                                translationX = pageOffset * size.width
                                                // Subtle micro-scale for added depth during fade
                                                val scale = 1f - (absOffset * 0.04f)
                                                scaleX = scale
                                                scaleY = scale
                                            },
                                        onPosterClick = { destination ->
                                            handlePosterRedirection(
                                                destination = destination,
                                                posterTitle = poster.title,
                                                offerDetails = poster.offerDetails,
                                                context = context,
                                                onNavigateToSubject = onNavigateToSubject,
                                                onNavigateToSubscription = onNavigateToSubscription,
                                                onNavigateToMockTests = onNavigateToMockTests,
                                                onNavigateToDailyQuiz = onNavigateToDailyQuiz,
                                                onNavigateToCurrentAffairs = onNavigateToCurrentAffairs,
                                                onNavigateToPerformance = onNavigateToPerformance,
                                                onNavigateToLeaderboard = onNavigateToLeaderboard,
                                                onNavigateToProfile = onNavigateToProfile
                                            )
                                        }
                                    )
                                }

                                // Auto-slide Indicator Dots
                                if (activePosters.size > 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        activePosters.forEachIndexed { index, _ ->
                                            val isCurrent = pagerState.currentPage == index
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 3.dp)
                                                    .height(5.dp)
                                                    .width(if (isCurrent) 22.dp else 6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (isCurrent) Color(0xFFFF7A00)
                                                        else Color(0xFFCBD5E1)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Subject Section Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Subject",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${displaySubjects.size} Subjects Available",
                                fontSize = 13.sp,
                                color = Color(0xFF7C3AED),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 3. Dynamic Subject Cards List
                    items(displaySubjects) { subject ->
                    val questionCount = SampleData.getQuestionsCountForSubject(subject.id)
                    val isZeroMcq = questionCount == 0 || subject.id == "subj-geography"
                    val topicCount = subject.units.sumOf { it.topics.size }

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable {
                                if (isZeroMcq) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ ${subject.name} question bank is updating soon! Exploring syllabus outline...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                onNavigateToSubject(subject)
                            },
                        border = BorderStroke(1.8.dp, Color(0xFF2563EB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Full Bleed FHD Subject Image
                            SubjectFhdCardImage(
                                subject = subject,
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
                                    color = if (isZeroMcq) Color(0xFFD97706) else Color(0xDD0F172A),
                                    border = BorderStroke(1.dp, if (isZeroMcq) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isZeroMcq) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "0 MCQs (Updating Soon)",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "${subject.category.uppercase()} SYLLABUS",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
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
                                    text = subject.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isZeroMcq) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${subject.name} currently has 0 MCQs. Updating soon!",
                                                fontSize = 12.sp,
                                                color = Color(0xFFFFE082),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "${subject.units.size} Units",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "$topicCount Topics",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            if (questionCount > 0) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0xFFFEF3C7).copy(alpha = 0.95f)
                                                ) {
                                                    Text(
                                                        text = "$questionCount+ MCQs",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFB45309),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
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
                                        Spacer(modifier = Modifier.width(3.dp))
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
            }
        }
    }
}
}

@Composable
fun HeroPracticeArenaHeaderCard(
    onNavigateToDailyQuiz: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFAF5),
                            Color(0xFFF3E8FF),
                            Color(0xFFE0E7FF),
                            Color(0xFFE0F2FE),
                            Color(0xFFDBEAFE)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF5722)
                    ) {
                        Text(
                            text = "4 PRO PLANS AVAILABLE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "APPSC Group 2 & State PSC",
                        color = Color(0xFF1E293B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Middle Row: Text on Left + 3D Graduation Cap & Books Art on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    ) {
                        Text(
                            text = "Syllabus Wise MCQ Practice &\nExam Arena",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.5.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 23.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "4,500+ Live MCQs ready for practice across all subjects",
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    GraduationCapBooksArt(
                        modifier = Modifier
                            .size(width = 112.dp, height = 96.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row (Daily Quiz & 4 Pro Plans)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button 1: Daily Quiz
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { onNavigateToDailyQuiz() },
                        color = Color.Transparent,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFF8A00), Color(0xFFFF5722))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daily Quiz",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Button 2: 4 Pro Plans
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFEDE9FE),
                        border = BorderStroke(1.5.dp, Color(0xFF8B5CF6)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { onNavigateToSubscription() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "4 Pro Plans",
                                color = Color(0xFF6D28D9),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GraduationCapBooksArt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Book 1 (Bottom book - Navy Blue)
            val pathBook1 = Path().apply {
                moveTo(w * 0.12f, h * 0.72f)
                lineTo(w * 0.82f, h * 0.65f)
                lineTo(w * 0.85f, h * 0.82f)
                lineTo(w * 0.15f, h * 0.90f)
                close()
            }
            drawPath(
                path = pathBook1,
                brush = Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF1D4ED8)))
            )
            // Book 1 Pages (White/Cream side)
            val pathBook1Pages = Path().apply {
                moveTo(w * 0.82f, h * 0.65f)
                lineTo(w * 0.92f, h * 0.70f)
                lineTo(w * 0.85f, h * 0.82f)
                close()
            }
            drawPath(path = pathBook1Pages, color = Color(0xFFFEF3C7))

            // Book 2 (Middle book - Cyan / Sky Blue)
            val pathBook2 = Path().apply {
                moveTo(w * 0.16f, h * 0.58f)
                lineTo(w * 0.80f, h * 0.52f)
                lineTo(w * 0.83f, h * 0.68f)
                lineTo(w * 0.18f, h * 0.75f)
                close()
            }
            drawPath(
                path = pathBook2,
                brush = Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF0369A1)))
            )
            // Book 2 Pages
            val pathBook2Pages = Path().apply {
                moveTo(w * 0.80f, h * 0.52f)
                lineTo(w * 0.88f, h * 0.57f)
                lineTo(w * 0.83f, h * 0.68f)
                close()
            }
            drawPath(path = pathBook2Pages, color = Color(0xFFFFFBEB))

            // Book 3 (Top book - Vibrant Orange / Coral)
            val pathBook3 = Path().apply {
                moveTo(w * 0.20f, h * 0.44f)
                lineTo(w * 0.78f, h * 0.40f)
                lineTo(w * 0.81f, h * 0.55f)
                lineTo(w * 0.22f, h * 0.60f)
                close()
            }
            drawPath(
                path = pathBook3,
                brush = Brush.linearGradient(listOf(Color(0xFFEA580C), Color(0xFFF97316), Color(0xFFFB923C)))
            )
            // Book 3 Pages
            val pathBook3Pages = Path().apply {
                moveTo(w * 0.78f, h * 0.40f)
                lineTo(w * 0.86f, h * 0.44f)
                lineTo(w * 0.81f, h * 0.55f)
                close()
            }
            drawPath(path = pathBook3Pages, color = Color(0xFFFEF9C3))

            // Graduation Cap (Mortarboard rhombus diamond)
            val pathCap = Path().apply {
                moveTo(w * 0.50f, h * 0.10f)
                lineTo(w * 0.88f, h * 0.22f)
                lineTo(w * 0.50f, h * 0.35f)
                lineTo(w * 0.12f, h * 0.22f)
                close()
            }
            drawPath(
                path = pathCap,
                brush = Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF1E293B)))
            )

            // Cap Skull Cap Base underneath
            val pathSkull = Path().apply {
                moveTo(w * 0.32f, h * 0.28f)
                lineTo(w * 0.68f, h * 0.28f)
                lineTo(w * 0.64f, h * 0.40f)
                lineTo(w * 0.36f, h * 0.40f)
                close()
            }
            drawPath(
                path = pathSkull,
                brush = Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF020617)))
            )

            // Center Golden Button on Cap
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = w * 0.035f,
                center = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.225f)
            )

            // Golden Tassel Ribbon hanging down to the right
            val pathTassel = Path().apply {
                moveTo(w * 0.50f, h * 0.225f)
                cubicTo(w * 0.65f, h * 0.22f, w * 0.78f, h * 0.28f, w * 0.82f, h * 0.40f)
            }
            drawPath(
                path = pathTassel,
                color = Color(0xFFFBBF24),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
            )

            // Tassel Fringe / Pom-pom at end
            val pathFringe = Path().apply {
                moveTo(w * 0.80f, h * 0.39f)
                lineTo(w * 0.84f, h * 0.39f)
                lineTo(w * 0.86f, h * 0.52f)
                lineTo(w * 0.78f, h * 0.52f)
                close()
            }
            drawPath(
                path = pathFringe,
                brush = Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PremiumNavyCard,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppIconBadge(size: androidx.compose.ui.unit.Dp = 34.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(9.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4A044E), Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(size * 0.45f)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "APPSC",
                color = Color(0xFFFBBF24),
                fontSize = 7.5.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

fun handlePosterRedirection(
    destination: String,
    posterTitle: String,
    offerDetails: String = "",
    context: android.content.Context,
    onNavigateToSubject: (Subject) -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToMockTests: () -> Unit,
    onNavigateToDailyQuiz: () -> Unit,
    onNavigateToCurrentAffairs: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val cleanDest = destination.trim().lowercase()
    val combinedContext = "$cleanDest $posterTitle $offerDetails".lowercase()

    // 1. Direct or fuzzy match against known Subjects in SampleData
    val matchedSubject: Subject? = SampleData.defaultSubjects.firstOrNull { subject ->
        val sId = subject.id.lowercase()
        val sName = subject.name.lowercase()
        cleanDest == sId ||
        cleanDest == "subj-$sId" ||
        cleanDest == sName ||
        (cleanDest.contains("ap-hist") && sId.contains("ap-history")) ||
        (cleanDest.contains("ap_hist") && sId.contains("ap-history")) ||
        (cleanDest.contains("indian-hist") && sId.contains("indian-history")) ||
        (cleanDest.contains("indian_hist") && sId.contains("indian-history")) ||
        ((cleanDest.contains("economy") || cleanDest.contains("economic")) && sId.contains("economy")) ||
        ((cleanDest.contains("polity") || cleanDest.contains("constitution")) && sId.contains("constitution")) ||
        (cleanDest.contains("geography") && sId.contains("geography")) ||
        (cleanDest.contains("society") && sId.contains("society")) ||
        ((cleanDest.contains("mental") || cleanDest.contains("reasoning") || cleanDest.contains("aptitude")) && sId.contains("mental-ability")) ||
        ((cleanDest.contains("science") || cleanDest.contains("tech")) && sId.contains("science-tech"))
    }

    if (matchedSubject != null) {
        Toast.makeText(context, "📚 Opening ${matchedSubject.name} Module & Syllabus...", Toast.LENGTH_SHORT).show()
        onNavigateToSubject(matchedSubject)
        return
    }

    // 2. Primary Promotional & Feature Screen Routes
    when {
        cleanDest.contains("sub") || cleanDest.contains("plan") || cleanDest.contains("price") || cleanDest.contains("pay") || cleanDest.contains("offer") -> {
            Toast.makeText(context, "🎉 Opening Special Subscription Plans & Offers...", Toast.LENGTH_SHORT).show()
            onNavigateToSubscription()
        }
        cleanDest.contains("mock") || cleanDest.contains("test") || cleanDest.contains("exam") || cleanDest.contains("marathon") -> {
            Toast.makeText(context, "🎯 Opening Grand Mock Test Series...", Toast.LENGTH_SHORT).show()
            onNavigateToMockTests()
        }
        cleanDest.contains("daily") || cleanDest.contains("quiz") || cleanDest.contains("speed") -> {
            Toast.makeText(context, "⚡ Loading Daily Speed Quiz Arena...", Toast.LENGTH_SHORT).show()
            onNavigateToDailyQuiz()
        }
        cleanDest.contains("current") || cleanDest.contains("affair") || cleanDest.contains("news") || cleanDest.contains("capsule") -> {
            Toast.makeText(context, "📰 Opening 2026 Current Affairs Capsule...", Toast.LENGTH_SHORT).show()
            onNavigateToCurrentAffairs()
        }
        cleanDest.contains("perform") || cleanDest.contains("score") || cleanDest.contains("analytic") || cleanDest.contains("accuracy") || cleanDest.contains("weak") -> {
            Toast.makeText(context, "📊 Opening Performance Diagnostics & Accuracy Heatmap...", Toast.LENGTH_SHORT).show()
            onNavigateToPerformance()
        }
        cleanDest.contains("leader") || cleanDest.contains("rank") || cleanDest.contains("merit") -> {
            Toast.makeText(context, "🏆 Opening State Rank Leaderboard...", Toast.LENGTH_SHORT).show()
            onNavigateToLeaderboard()
        }
        cleanDest.contains("profile") || cleanDest.contains("account") -> {
            onNavigateToProfile()
        }
        // 3. Fallback: contextual subject matching from poster text
        combinedContext.contains("ap history") || combinedContext.contains("andhra pradesh history") || combinedContext.contains("satavahana") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-ap-history" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "📚 Opening AP History Subject Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        combinedContext.contains("economy") || combinedContext.contains("socio-economic survey") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-economy" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "📈 Opening Indian & AP Economy Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        combinedContext.contains("polity") || combinedContext.contains("constitution") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-constitution" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "⚖️ Opening Indian Constitution & Polity Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        combinedContext.contains("geography") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-geography" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "🌍 Opening Geography Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        combinedContext.contains("mental ability") || combinedContext.contains("reasoning") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-mental-ability" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "🧠 Opening Mental Ability & Reasoning Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        combinedContext.contains("science") -> {
            val sub = SampleData.defaultSubjects.firstOrNull { it.id == "subj-science-tech" } ?: SampleData.defaultSubjects[0]
            Toast.makeText(context, "🔬 Opening Science & Technology Module...", Toast.LENGTH_SHORT).show()
            onNavigateToSubject(sub)
        }
        else -> {
            Toast.makeText(context, "✨ Opening Offer: $posterTitle", Toast.LENGTH_SHORT).show()
            onNavigateToSubscription()
        }
    }
}

@Composable
fun PromotionalPosterCard(
    poster: PromotionalPosterModel,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(185.dp),
    onPosterClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .clickable { onPosterClick(poster.destinationPage) },
        border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Full Bleed Background Poster FHD Image
            PosterFhdCardImage(
                poster = poster,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Multi-stop Gradient Overlay for Crystal Clear Typography
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // 3. Top Badges & Festive Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "FESTIVAL SPECIAL",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Priority #${poster.priorityOrder}",
                        color = Color(0xFFFDE68A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 4. Bottom Offer Text & Call-To-Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = poster.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = poster.promotionalText,
                    color = Color(0xFFFDE047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (poster.offerDetails.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = poster.offerDetails,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Valid: ${poster.startDate.takeLast(5)} - ${poster.endDate.takeLast(5)}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFF7A00),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { onPosterClick(poster.destinationPage) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = poster.ctaText.ifBlank { "CLAIM OFFER" },
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

