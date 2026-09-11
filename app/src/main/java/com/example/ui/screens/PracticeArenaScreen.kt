package com.example.ui.screens

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeArenaScreen(
    questions: List<Question>,
    title: String,
    onBack: () -> Unit,
    onSubmitTest: (Int, Int, Double, Map<String, String>, Set<String>) -> Unit,
    initialBookmarkedIds: Set<String> = emptySet(),
    onToggleBookmark: ((Question) -> Unit)? = null
) {
    var isDarkMode by remember { mutableStateOf(false) }
    val appConfig = LocalAppConfig.current
    DynamicAppTheme(config = appConfig, darkTheme = isDarkMode) {
        val context = LocalContext.current
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
    
        var currentIndex by remember { mutableIntStateOf(0) }
        var userAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
        var markedForReview by remember { mutableStateOf<Set<String>>(emptySet()) }
        var bookmarkedIds by remember(initialBookmarkedIds) { mutableStateOf(initialBookmarkedIds) }
        var flaggedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
        var showPaletteDrawer by remember { mutableStateOf(false) }
        var showExplanation by remember { mutableStateOf(true) }
        var textScaleFactor by remember { mutableFloatStateOf(1.0f) }
        var showFontSettingsDialog by remember { mutableStateOf(false) }
        var timerSeconds by remember { mutableIntStateOf(20) }
        var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
        var showCelebration by remember { mutableStateOf(false) }
        LaunchedEffect(currentIndex) { showCelebration = false }
    
        // Live countdown timer loop
        LaunchedEffect(key1 = timerSeconds) {
            if (timerSeconds > 0) {
                delay(1000L)
                timerSeconds--
            }
        }
    
        DisposableEffect(Unit) {
            var tts: TextToSpeech? = null
            try {
                tts = TextToSpeech(context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        try { tts?.language = Locale("te", "IN") } catch (_: Exception) {}
                    }
                }
                ttsEngine = tts
            } catch (_: Exception) {}
    
            onDispose {
                try {
                    tts?.stop()
                    tts?.shutdown()
                } catch (_: Exception) {}
            }
        }
    
        val currentQ = questions.getOrNull(currentIndex) ?: questions.firstOrNull()
    
        // Font Settings Dialog
        if (showFontSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showFontSettingsDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.TextFields, contentDescription = null, tint = Color(0xFF2563EB))
                        Text("Font & Text Settings", color = AppTextDark, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Customize question and option text size for comfortable reading during practice.", color = AppTextSecondary, fontSize = 13.sp)
                        
                        Text("Select Font Size Scale:", fontWeight = FontWeight.Bold, color = AppTextDark, fontSize = 13.sp)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(0.9f to "Small", 1.0f to "Normal", 1.15f to "Large", 1.3f to "XL").forEach { (scale, label) ->
                                val isSelected = textScaleFactor == scale
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF1D4ED8) else AppLightBorder),
                                    modifier = Modifier
                                        .clickable { textScaleFactor = scale }
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(label, color = if (isSelected) Color.White else AppTextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
    
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Live Preview:", fontWeight = FontWeight.Bold, color = AppTextSecondary, fontSize = 12.sp)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = currentQ?.questionText ?: "Sample question preview text...",
                                    color = AppTextDark,
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
                        // Drawer Header
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
                                text = if (title.contains("APPSC")) title else "APPSC Group 2 (Executive & Non-Exec)",
                                color = AppTextDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
    
                        Spacer(modifier = Modifier.height(10.dp))
    
                        // Drawer Items List
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            item {
                                DrawerRowItem(Icons.Default.Home, "Home", tint = Color(0xFF2563EB)) {
                                    scope.launch { drawerState.close() }
                                    onBack()
                                }
                            }
                            item {
                                DrawerRowItem(Icons.Default.Person, "Profile", tint = AppTextSecondary) {
                                    scope.launch { drawerState.close() }
                                }
                            }
                            item {
                                DrawerRowItem(Icons.Default.CreditCard, "Subscriptions", tint = Color(0xFF7C3AED)) {
                                    scope.launch { drawerState.close() }
                                }
                            }
                            item {
                                DrawerRowItem(Icons.Default.BarChart, "Performance Board", tint = Color(0xFF2563EB)) {
                                    scope.launch { drawerState.close() }
                                }
                            }
                            item {
                                DrawerRowItem(Icons.Default.EmojiEvents, "Leaderboard & Ranks", tint = AppOrangePrimary) {
                                    scope.launch { drawerState.close() }
                                }
                            }
                            item {
                                // Active Highlighted State
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Text("Syllabus & Subtopics", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                            item {
                                DrawerRowItem(Icons.Default.Article, "Current Affairs Center", tint = AppTextSecondary) {
                                    scope.launch { drawerState.close() }
                                }
                            }
                            item {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder)
                            }
                            item { DrawerRowItem(Icons.Default.HelpOutline, "FAQ & Help", tint = AppTextSecondary) {} }
                            item { DrawerRowItem(Icons.Default.MailOutline, "Contact Support", tint = AppGreenAccent) {} }
                            item { DrawerRowItem(Icons.Default.ReportProblem, "Report an Issue", tint = Color(0xFFEF4444)) {} }
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onBack() }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text("Log Out / Switch Account", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppLuminousBackground(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Hamburger Menu button opens Navigation Drawer
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, AppLightBorder),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable { scope.launch { drawerState.open() } }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "Menu",
                                                tint = AppTextDark,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
    
                                    Spacer(modifier = Modifier.width(10.dp))
    
                                    // Avatar badge "AP"
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "AP",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
    
                                    Spacer(modifier = Modifier.width(10.dp))
    
                                    Column {
                                        Text(
                                            text = if (title.contains("APPSC")) title else "APPSC Group 2 (Executive & Non-Exec)",
                                            color = AppTextDark,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            maxLines = 1
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "⚡ Practice Arena",
                                                color = Color(0xFF2563EB),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
    
                                // Right Action Controls: Format 'T' button & Refresh
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                        color = Color(0xFFEFF6FF),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                isDarkMode = !isDarkMode
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = "Theme",
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
    
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                        color = Color(0xFFEFF6FF),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                showFontSettingsDialog = true
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "T",
                                                color = Color(0xFF2563EB),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
    
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, AppLightBorder),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                timerSeconds = 20
                                                userAnswers = emptyMap()
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset",
                                                tint = AppTextDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    containerColor = Color.Transparent
                ) { padding ->
                    if (currentQ == null) {
                        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("No questions available.", color = AppTextDark)
                        }
                        return@Scaffold
                    }
    
                    val selectedOption = userAnswers[currentQ.id]
                    val isCurrentBookmarked = bookmarkedIds.contains(currentQ.id)
                    val isCurrentFlagged = flaggedIds.contains(currentQ.id)
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sub-header Bar / Floating Tool Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, AppLightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Clock Timer 00:20
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Timer",
                                        tint = AppOrangePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val mins = timerSeconds / 60
                                    val secs = timerSeconds % 60
                                    Text(
                                        text = "%02d:%02d".format(mins, secs),
                                        color = AppTextDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AppOrangePrimary.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, AppOrangePrimary.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "${currentIndex + 1}/${questions.size}",
                                            color = AppOrangePrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
    
                                // Marks indicators (+2.0 green pill, -0.5 pink pill)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFDCFCE7),
                                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                    ) {
                                        Text(
                                            text = "+ 2.0",
                                            color = Color(0xFF16A34A),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
    
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEE2E2),
                                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                                    ) {
                                        Text(
                                            text = "- 0.5",
                                            color = Color(0xFFDC2626),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
    
                                // Right icons (Bookmark, Palette)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isCurrentBookmarked) AppOrangePrimary else AppTextDark,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                bookmarkedIds = if (isCurrentBookmarked) bookmarkedIds - currentQ.id else bookmarkedIds + currentQ.id
                                                onToggleBookmark?.invoke(currentQ)
                                                Toast.makeText(context, if (!isCurrentBookmarked) "Saved Bookmark" else "Removed Bookmark", Toast.LENGTH_SHORT).show()
                                            }
                                    )
    
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = "Palette Grid",
                                        tint = AppTextDark,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { showPaletteDrawer = !showPaletteDrawer }
                                    )
                                }
                            }
                        }
    
                        Spacer(modifier = Modifier.height(10.dp))
    
                        // Question Palette Drawer Grid if toggled
                        if (showPaletteDrawer) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Question Drawer (${questions.size} Questions)", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        TextButton(onClick = { showPaletteDrawer = false }) { Text("Close", fontSize = 12.sp, color = Color(0xFF2563EB)) }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(questions.size) { idx ->
                                            val qId = questions[idx].id
                                            val isAns = userAnswers.containsKey(qId)
                                            val isRev = markedForReview.contains(qId)
                                            val btnColor = when {
                                                isRev -> Color(0xFFF59E0B)
                                                isAns -> Color(0xFF10B981)
                                                else -> Color(0xFFE2E8F0)
                                            }
                                            val textColor = if (isAns || isRev) Color.White else AppTextDark
    
                                            Surface(
                                                shape = CircleShape,
                                                color = btnColor,
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable {
                                                        currentIndex = idx
                                                        showPaletteDrawer = false
                                                    }
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("${idx + 1}", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
    
                        // Main Scrollable Card Area
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = BorderStroke(1.2.dp, Color(0xFFFED7AA)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            AnimatedContent(
                                targetState = currentIndex,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                            slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300))
                                        )
                                    } else {
                                        (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                            slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300))
                                        )
                                    }
                                },
                                label = "questionSlide",
                                modifier = Modifier.fillMaxSize()
                            ) { targetIdx ->
                                val currentQ = questions.getOrNull(targetIdx) ?: questions.firstOrNull()
                                if (currentQ == null) {
                                    Box(modifier = Modifier.fillMaxSize())
                                    return@AnimatedContent
                                }
                                val selectedOption = userAnswers[currentQ.id]
                                val isCurrentFlagged = flaggedIds.contains(currentQ.id)
    
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                // Question Box with Royal Blue Gradient Container
                                item {
                                    Card(
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFFFB923C),
                                                            Color(0xFFEF4444)
                                                        )
                                                    )
                                                )
                                                .padding(20.dp)
                                        ) {
                                            Column {
    
                                                Text(
                                                    text = currentQ.questionText,
                                                    color = Color.White,
                                                    fontSize = (16 * textScaleFactor).sp,
                                                    fontWeight = FontWeight.Bold,
                                                    lineHeight = (24 * textScaleFactor).sp,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
    
                                // Options List (A, B, C, D)
                                val optionsMap = listOf(
                                    "A" to (currentQ.options["A"] ?: "Option A"),
                                    "B" to (currentQ.options["B"] ?: "Option B"),
                                    "C" to (currentQ.options["C"] ?: "Option C"),
                                    "D" to (currentQ.options["D"] ?: "Option D")
                                )
    
                                items(optionsMap) { (key, optionText) ->
                                    val isSelected = selectedOption == key
                                    val isCorrectOption = key.equals(currentQ.correctOption, ignoreCase = true)
                                    
                                    val isWrongSelection = isSelected && !isCorrectOption
                                    val isGreenOption = (isSelected && isCorrectOption) || (selectedOption != null && isCorrectOption)
    
                                    val cardBgColor = when {
                                        isWrongSelection -> Color(0xFFEF4444)
                                        isGreenOption -> Color(0xFF10B981)
                                        else -> Color(0xFFE0F2FE)
                                    }
    
                                    val textColor = when {
                                        isWrongSelection || isGreenOption -> Color.White
                                        else -> AppTextDark
                                    }
    
                                    val borderColor = when {
                                        isWrongSelection -> Color(0xFFDC2626)
                                        isGreenOption -> Color(0xFF059669)
                                        else -> Color(0xFFBAE6FD)
                                    }
    
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                        border = BorderStroke(width = 1.8.dp, color = borderColor),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (isWrongSelection || isGreenOption) 4.dp else 2.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (selectedOption == null) {
                                                     userAnswers = userAnswers + (currentQ.id to key)
                                                     if (key.equals(currentQ.correctOption, ignoreCase = true)) {
                                                         showCelebration = true
                                                     }
                                                 }
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        ) {
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
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = if (isWrongSelection || isGreenOption) Color.White.copy(alpha = 0.3f) else Color(0xFFBAE6FD),
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "$key)",
                                                                color = textColor,
                                                                fontWeight = FontWeight.Black,
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(14.dp))
                                                    Text(
                                                        text = optionText,
                                                        color = textColor,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (15 * textScaleFactor).sp
                                                    )
                                                }
    
                                                // Badge Icon
                                                if (isWrongSelection) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color.White,
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Default.Close,
                                                                contentDescription = "Incorrect",
                                                                tint = Color(0xFFEF4444),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                } else if (isGreenOption) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color.White,
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Correct",
                                                                tint = Color(0xFF10B981),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
    
                                // Explanation Box (Visible only after user selected an option)
                                item {
                                    if (selectedOption != null) {
                                        Card(
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F8EE)),
                                            border = BorderStroke(1.5.dp, Color(0xFFA7F3D0)),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(18.dp)
                                            ) {
                                                Text(
                                                    text = "EXPLANATION",
                                                    color = Color(0xFF047857),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
    
                                                Spacer(modifier = Modifier.height(10.dp))
    
                                                Text(
                                                    text = currentQ.explanation?.ifBlank { null }
                                                        ?: "• Magma is primarily sourced from the Asthenosphere in the upper mantle.\n• Partial melting of rocks in this zone creates magma.\n• This magma rises through the lithosphere to erupt as lava.\n• 💡 Why not the others:\n– Lower Mantle = deeper, more rigid mantle zone, not the magma source\n– Inner Core = solid iron zone, not where magma forms\n– Outer Core = liquid metal layer, unrelated to volcanic magma source",
                                                    color = Color(0xFF065F46),
                                                    fontSize = 14.sp,
                                                    lineHeight = 22.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
    
                                // Bottom Links inside Card: "← Back to Subtopics" & "⚐ Flag Question"
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { onBack() }
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Back to Subtopics", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
    
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                flaggedIds = if (isCurrentFlagged) flaggedIds - currentQ.id else flaggedIds + currentQ.id
                                                Toast.makeText(context, if (!isCurrentFlagged) "Flagged question for review" else "Unflagged question", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Flag,
                                                contentDescription = null,
                                                tint = if (isCurrentFlagged) Color(0xFFEF4444) else AppTextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isCurrentFlagged) "Flagged" else "Flag Question",
                                                color = if (isCurrentFlagged) Color(0xFFEF4444) else AppTextMuted,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
    
                        Spacer(modifier = Modifier.height(10.dp))
    
                        // Fixed Bottom Navigation Pill Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Previous Pill Button
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, AppLightBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable(enabled = currentIndex > 0) { currentIndex-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = if (currentIndex > 0) AppTextDark else AppTextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Previous",
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentIndex > 0) AppTextDark else AppTextMuted,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
    
                             // Next Pill Button (AppOrangePrimary)
                             val isAtSubmissionPoint = currentIndex >= 9 || currentIndex >= questions.size - 1
                             Surface(
                                 shape = RoundedCornerShape(16.dp),
                                 color = AppOrangePrimary,
                                 modifier = Modifier
                                     .weight(1f)
                                     .height(48.dp)
                                     .clickable {
                                         if (isAtSubmissionPoint) {
                                             var correct = 0
                                             var wrong = 0
                                             questions.forEach { q ->
                                                 val ans = userAnswers[q.id]
                                                 if (ans != null) {
                                                     if (ans.equals(q.correctOption, ignoreCase = true)) correct++ else wrong++
                                                 }
                                             }
                                             val score = (correct * 2.0) - (wrong * 0.5)
                                             onSubmitTest(correct, wrong, score, userAnswers, bookmarkedIds)
                                         } else {
                                             currentIndex++
                                         }
                                     }
                             ) {
                                 Box(contentAlignment = Alignment.Center) {
                                     Row(verticalAlignment = Alignment.CenterVertically) {
                                         Text(
                                             text = if (isAtSubmissionPoint) "Submit" else "Next",
                                             fontWeight = FontWeight.Black,
                                             color = Color.White,
                                             fontSize = 14.sp
                                         )
                                         Spacer(modifier = Modifier.width(6.dp))
                                         Icon(
                                             imageVector = Icons.Default.ArrowForward,
                                             contentDescription = null,
                                             tint = Color.White,
                                             modifier = Modifier.size(16.dp)
                                         )
                                     }
                                 }
                             }
                        }
                    }
                }
            }
                BalloonBlastingAnimation(trigger = showCelebration)
            }
        }
    }
    
    
    
}





data class ParticleData(
    val xOffset: Float,
    val yOffset: Float,
    val color: Color,
    val size: Float,
    val rotation: Float = 0f,
    val speed: Float = 1f
)

@Composable
fun BalloonBlastingAnimation(trigger: Boolean) {
    if (!trigger) return

    val particles = remember {
        val rnd = kotlin.random.Random
        List(100) {
            val colors = listOf(
                Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981),
                Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
                Color(0xFF2DD4BF), Color(0xFFF43F5E)
            )
            ParticleData(
                xOffset = rnd.nextInt(-900, 900).toFloat(),
                yOffset = rnd.nextInt(-1200, 400).toFloat(),
                color = colors[rnd.nextInt(colors.size)],
                size = rnd.nextInt(14, 38).toFloat(),
                rotation = rnd.nextInt(0, 360).toFloat(),
                speed = rnd.nextDouble(0.6, 1.8).toFloat()
            )
        }
    }

    val progress = remember { androidx.compose.animation.core.Animatable(0f) }
    
    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 2000, 
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    if (progress.value < 1f) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                particles.forEach { p ->
                    val currentProgress = progress.value
                    
                    // Explosion force + Gravity fall
                    val outX = center.x + (p.xOffset * currentProgress * p.speed)
                    val outY = center.y + (p.yOffset * currentProgress * p.speed) + (currentProgress * currentProgress * 1200f)
                    
                    val alpha = (1.2f - currentProgress).coerceIn(0f, 1f)
                    val currentScale = (1f - (currentProgress * 0.3f)).coerceAtLeast(0.1f)
                    
                    withTransform({
                        translate(outX, outY)
                        rotate(p.rotation + (currentProgress * 500f * p.speed))
                        scale(currentScale, currentScale)
                    }) {
                        if (p.size.toInt() % 2 == 0) {
                            // Draw Balloon/Circle
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.size / 2
                            )
                        } else {
                            // Draw Confetti Rect
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = androidx.compose.ui.geometry.Offset(-p.size / 2, -p.size),
                                size = androidx.compose.ui.geometry.Size(p.size, p.size * 1.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
