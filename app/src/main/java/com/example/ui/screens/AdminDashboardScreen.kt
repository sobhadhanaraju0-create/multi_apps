package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showUniversalJsonImportScreen by remember { mutableStateOf(false) }

    if (showUniversalJsonImportScreen) {
        UniversalJsonImportScreen(
            onBack = { showUniversalJsonImportScreen = false }
        )
        return
    }

    // Current Active Admin Account
    var currentAdmin by remember { SampleData.currentAdminAccount }
    val isSuperAdmin = currentAdmin.role == AdminRole.SUPER_ADMIN

    // Active App Scope (for Super Admin managing a specific app or Content Admin locked to their assigned app)
    var selectedManagedAppId by remember {
        mutableStateOf(currentAdmin.assignedAppId ?: SampleData.currentActiveAppId.value)
    }

    // Ensure Content Admin is always locked to their assigned app
    LaunchedEffect(currentAdmin) {
        if (!isSuperAdmin && currentAdmin.assignedAppId != null) {
            selectedManagedAppId = currentAdmin.assignedAppId!!
        }
    }

    val managedApp = SampleData.appsListState.find { it.id == selectedManagedAppId } ?: SampleData.getActiveApp()

    // Navigation Tabs
    // For Super Admin: 0 = Apps Directory & Provisioning, 1 = Content Admins, 2 = Multi-App Sharing Hub, 3 = App Content Manager, 4 = Audit Logs
    // For Content Admin: 0 = Syllabus Hierarchy, 1 = JSON Importer, 2 = Current Affairs & PDFs, 3 = Posters & Banners, 4 = My Activity Logs
    var selectedSuperAdminTab by remember { mutableIntStateOf(0) }
    var selectedContentAdminTab by remember { mutableIntStateOf(0) }

    // Dialog States
    var showCreateAppDialog by remember { mutableStateOf(false) }
    var showCreateAdminDialog by remember { mutableStateOf(false) }
    var showSwitchAdminDialog by remember { mutableStateOf(false) }

    // Content Management Dialogs
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    var showAddUnitDialog by remember { mutableStateOf(false) }
    var unitToEdit by remember { mutableStateOf<Pair<String, UnitModel>?>(null) }

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var topicToEdit by remember { mutableStateOf<Triple<String, String, TopicModel>?>(null) }

    var showAddCurrentAffairDialog by remember { mutableStateOf(false) }
    var currentAffairToEdit by remember { mutableStateOf<CurrentAffair?>(null) }

    var showAddMonthlyPdfDialog by remember { mutableStateOf(false) }
    var monthlyPdfToEdit by remember { mutableStateOf<MonthlyCurrentAffairsPdf?>(null) }

    var showAddPosterDialog by remember { mutableStateOf(false) }
    var posterToEdit by remember { mutableStateOf<PromotionalPosterModel?>(null) }

    AppLuminousBackground {
        Scaffold(
            topBar = {
            Surface(
                color = if (isSuperAdmin) Color(0xFF1E1B4B) else Color(0xFF0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Row 1: Back + Title + Quick Switch Role
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { onBack() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isSuperAdmin) "GLOBAL SUPER ADMIN" else "CONTENT ADMIN PORTAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = if (isSuperAdmin) Color(0xFFFBBF24) else Color(0xFF60A5FA)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSuperAdmin) Color(0xFFB45309) else Color(0xFF2563EB)
                                    ) {
                                        Text(
                                            text = if (isSuperAdmin) "ALL APPS" else managedApp.code,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${currentAdmin.name} • ${currentAdmin.email}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Switch Admin Button
                        Button(
                            onClick = { showSwitchAdminDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Switch Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role Notice & Active Scope Bar
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSuperAdmin) Color(0xFF312E81) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSuperAdmin) Color(0xFF6366F1) else Color(0xFF3B82F6))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    if (isSuperAdmin) Icons.Default.Shield else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isSuperAdmin) Color(0xFFFBBF24) else Color(0xFF60A5FA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSuperAdmin) {
                                        "Super Admin: Full Platform Governance & Multi-App Provisioning"
                                    } else {
                                        "Data Isolation: Strictly locked to '${managedApp.name}'"
                                    },
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isSuperAdmin) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981)
                                ) {
                                    Text(
                                        text = "${SampleData.appsListState.size} APPS",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Navigation Tabs
                    if (isSuperAdmin) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedSuperAdminTab,
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            edgePadding = 0.dp,
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedSuperAdminTab == 0,
                                onClick = { selectedSuperAdminTab = 0 },
                                text = { Text("🏢 App Tenants", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedSuperAdminTab == 1,
                                onClick = { selectedSuperAdminTab = 1 },
                                text = { Text("👥 Content Admins", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedSuperAdminTab == 2,
                                onClick = { selectedSuperAdminTab = 2 },
                                text = { Text("🚀 Multi-App Sharing", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedSuperAdminTab == 3,
                                onClick = { selectedSuperAdminTab = 3 },
                                text = { Text("📚 App Content (${managedApp.code})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedSuperAdminTab == 4,
                                onClick = { selectedSuperAdminTab = 4 },
                                text = { Text("📋 Audit Logs", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                        }
                    } else {
                        ScrollableTabRow(
                            selectedTabIndex = selectedContentAdminTab,
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            edgePadding = 0.dp,
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedContentAdminTab == 0,
                                onClick = { selectedContentAdminTab = 0 },
                                text = { Text("📖 Syllabus & MCQs", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedContentAdminTab == 1,
                                onClick = { selectedContentAdminTab = 1 },
                                text = { Text("📤 JSON Upload & Duplicate Check", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedContentAdminTab == 2,
                                onClick = { selectedContentAdminTab = 2 },
                                text = { Text("📰 Current Affairs & PDFs", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedContentAdminTab == 3,
                                onClick = { selectedContentAdminTab = 3 },
                                text = { Text("🖼️ Posters & Banners", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedContentAdminTab == 4,
                                onClick = { selectedContentAdminTab = 4 },
                                text = { Text("📝 My Audit Activity", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isSuperAdmin) {
                when (selectedSuperAdminTab) {
                    0 -> SuperAdminAppDirectoryView(
                        onSelectApp = { appId ->
                            selectedManagedAppId = appId
                            selectedSuperAdminTab = 3
                        },
                        onCreateAppClick = { showCreateAppDialog = true }
                    )
                    1 -> SuperAdminContentAdminsView(
                        onCreateAdminClick = { showCreateAdminDialog = true }
                    )
                    2 -> SuperAdminMultiAppSharingHub(
                        onAddCurrentAffair = { showAddCurrentAffairDialog = true },
                        onAddPdf = { showAddMonthlyPdfDialog = true },
                        onAddPoster = { showAddPosterDialog = true }
                    )
                    3 -> ContentAdminManagementHub(
                        activeApp = managedApp,
                        isSuperAdmin = true,
                        onOpenJsonImporter = { showUniversalJsonImportScreen = true },
                        onAddSubject = { showAddSubjectDialog = true },
                        onEditSubject = { subjectToEdit = it },
                        onDeleteSubject = { subjectToDelete = it },
                        onAddUnit = { showAddUnitDialog = true },
                        onAddTopic = { showAddTopicDialog = true },
                        onAddCurrentAffair = { showAddCurrentAffairDialog = true },
                        onAddPdf = { showAddMonthlyPdfDialog = true },
                        onAddPoster = { showAddPosterDialog = true },
                        onSelectDifferentApp = { selectedSuperAdminTab = 0 }
                    )
                    4 -> PlatformAuditLogsView(appFilter = null)
                }
            } else {
                when (selectedContentAdminTab) {
                    0 -> ContentAdminSyllabusView(
                        activeApp = managedApp,
                        onAddSubject = { showAddSubjectDialog = true },
                        onEditSubject = { subjectToEdit = it },
                        onDeleteSubject = { subjectToDelete = it },
                        onAddUnit = { showAddUnitDialog = true },
                        onAddTopic = { showAddTopicDialog = true },
                        onLaunchJsonImport = { showUniversalJsonImportScreen = true }
                    )
                    1 -> ContentAdminJsonUploadLanding(
                        activeApp = managedApp,
                        onLaunchJsonImport = { showUniversalJsonImportScreen = true }
                    )
                    2 -> ContentAdminCurrentAffairsAndPdfView(
                        activeApp = managedApp,
                        onAddCurrentAffair = { showAddCurrentAffairDialog = true },
                        onAddPdf = { showAddMonthlyPdfDialog = true }
                    )
                    3 -> ContentAdminPostersView(
                        activeApp = managedApp,
                        onAddPoster = { showAddPosterDialog = true }
                    )
                    4 -> PlatformAuditLogsView(appFilter = managedApp.id)
                }
            }
        }
    }
}

    // =========================================================================
    // DIALOGS
    // =========================================================================

    // 1. SWITCH ADMIN ACCOUNT DIALOG
    if (showSwitchAdminDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchAdminDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch Admin Session", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Select an administrator account to test Role-Based Access Control (RBAC):",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    items(SampleData.adminAccountsState) { account ->
                        val isCurrent = account.id == currentAdmin.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) Color(0xFFEFF6FF) else Color.White
                            ),
                            border = BorderStroke(
                                if (isCurrent) 2.dp else 1.dp,
                                if (isCurrent) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    SampleData.currentAdminAccount.value = account
                                    SampleData.currentAdminRoleState.value = account.role
                                    currentAdmin = account
                                    if (account.role == AdminRole.CONTENT_ADMIN && account.assignedAppId != null) {
                                        selectedManagedAppId = account.assignedAppId
                                        SampleData.setActiveApp(account.assignedAppId)
                                    }
                                    showSwitchAdminDialog = false
                                    Toast.makeText(context, "Logged in as ${account.name} (${account.role})", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = account.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (account.role == AdminRole.SUPER_ADMIN) Color(0xFFF59E0B) else Color(0xFF3B82F6)
                                        ) {
                                            Text(
                                                text = if (account.role == AdminRole.SUPER_ADMIN) "SUPER ADMIN" else "CONTENT ADMIN",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = account.email,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "Scope: ${account.assignedAppName ?: "Global All Apps"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (account.role == AdminRole.SUPER_ADMIN) Color(0xFF059669) else Color(0xFF2563EB)
                                    )
                                }

                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchAdminDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 2. CREATE NEW APP TENANT DIALOG (SUPER ADMIN ONLY)
    if (showCreateAppDialog) {
        var newAppName by remember { mutableStateOf("") }
        var newAppCode by remember { mutableStateOf("") }
        var newAppCategory by remember { mutableStateOf("State PSC") }
        var newAppTagline by remember { mutableStateOf("") }
        var newAppAdminName by remember { mutableStateOf("") }
        var newAppAdminEmail by remember { mutableStateOf("") }
        var newAppCurrencySymbol by remember { mutableStateOf("₹") }
        var selectedPrimaryColorHex by remember { mutableStateOf(0xFF2563EB) }
        var selectedSecondaryColorHex by remember { mutableStateOf(0xFF7C3AED) }
        var selectedFontPreset by remember { mutableStateOf(FontPreset.MODERN_SANS) }

        AlertDialog(
            onDismissRequest = { showCreateAppDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Application Tenant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "Super Admin Workflow: Create an isolated exam app and assign a dedicated Content Admin.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppName,
                            onValueChange = {
                                newAppName = it
                                if (newAppCode.isBlank()) {
                                    newAppCode = it.replace(" ", "-").uppercase()
                                }
                            },
                            label = { Text("Application Name * (e.g. APPSC Group 4)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppCode,
                            onValueChange = { newAppCode = it },
                            label = { Text("App Code * (e.g. APPSC-G4)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppCategory,
                            onValueChange = { newAppCategory = it },
                            label = { Text("Exam Category (e.g. State PSC, Central SSC, Banking, DSC)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppTagline,
                            onValueChange = { newAppTagline = it },
                            label = { Text("App Tagline / Description") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppCurrencySymbol,
                            onValueChange = { newAppCurrencySymbol = it },
                            label = { Text("Currency Symbol (e.g. ₹ or $)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                        Text("Dynamic Look & Feel (Branding & Typography):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    }

                    item {
                        Text("Primary Brand Color Palette:", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        val colorPresets = listOf(
                            Pair("Royal Blue", Pair(0xFF2563EB, 0xFF7C3AED)),
                            Pair("Emerald Green", Pair(0xFF059669, 0xFF6366F1)),
                            Pair("Vivid Amber", Pair(0xFFEA580C, 0xFF2563EB)),
                            Pair("Teal Ocean", Pair(0xFF0D9488, 0xFF0284C7)),
                            Pair("Royal Violet", Pair(0xFF7C3AED, 0xFF2563EB)),
                            Pair("Sky Blue", Pair(0xFF0284C7, 0xFF4F46E5)),
                            Pair("Deep Indigo", Pair(0xFF4F46E5, 0xFF06B6D4))
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(colorPresets) { preset ->
                                val isSelected = selectedPrimaryColorHex == preset.second.first
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(preset.second.first),
                                    border = BorderStroke(2.dp, if (isSelected) Color(0xFF0F172A) else Color.Transparent),
                                    modifier = Modifier
                                        .clickable {
                                            selectedPrimaryColorHex = preset.second.first
                                            selectedSecondaryColorHex = preset.second.second
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(preset.first, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Font Family & Typography Preset:", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        val fontPresets = listOf(
                            Pair(FontPreset.MODERN_SANS, "Modern Sans (Inter / Jakarta)"),
                            Pair(FontPreset.EDITORIAL_SERIF, "Editorial Serif (Academic / UPSC)"),
                            Pair(FontPreset.TECHNICAL_MONO, "Technical Mono (Banking / Speed)"),
                            Pair(FontPreset.BOLD_GEOMETRIC, "Bold Geometric (SSC / Athletic)"),
                            Pair(FontPreset.ROUNDED_FRIENDLY, "Rounded Friendly (DSC / TET)")
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            fontPresets.forEach { preset ->
                                val isSelected = selectedFontPreset == preset.first
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedFontPreset = preset.first }
                                        .background(
                                            if (isSelected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedFontPreset = preset.first },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(preset.second, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color(0xFF0F172A))
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                        Text("Dedicated Content Admin Provisioning:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    }

                    item {
                        OutlinedTextField(
                            value = newAppAdminName,
                            onValueChange = { newAppAdminName = it },
                            label = { Text("Content Admin Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newAppAdminEmail,
                            onValueChange = { newAppAdminEmail = it },
                            label = { Text("Content Admin Email * (e.g. admin@examportal.com)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAppName.isBlank() || newAppCode.isBlank() || newAppAdminEmail.isBlank() || newAppAdminName.isBlank()) {
                            Toast.makeText(context, "Please fill in all mandatory fields (*)", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val generatedId = newAppName.lowercase().replace(Regex("[^a-z0-9]"), "-").trim('-')
                        val newTenant = AppTenantModel(
                            id = generatedId,
                            name = newAppName.trim(),
                            code = newAppCode.trim(),
                            examCategory = newAppCategory.trim(),
                            tagline = newAppTagline.trim(),
                            currencySymbol = newAppCurrencySymbol.trim(),
                            assignedAdminEmail = newAppAdminEmail.trim(),
                            assignedAdminName = newAppAdminName.trim()
                        )

                        val success = SampleData.createApp(
                            newApp = newTenant,
                            adminName = newAppAdminName.trim(),
                            adminEmail = newAppAdminEmail.trim()
                        )

                        if (success) {
                            Toast.makeText(context, "🎉 Created App '${newTenant.name}' and provisioned Content Admin!", Toast.LENGTH_LONG).show()
                            showCreateAppDialog = false
                        } else {
                            Toast.makeText(context, "❌ An app with this ID or Code already exists.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Create App & Provision Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. CREATE / ASSIGN CONTENT ADMIN DIALOG (SUPER ADMIN ONLY)
    if (showCreateAdminDialog) {
        var adminNameInput by remember { mutableStateOf("") }
        var adminEmailInput by remember { mutableStateOf("") }
        var targetAppIdInput by remember { mutableStateOf(SampleData.appsListState.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showCreateAdminDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Provision Content Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "Each application has 1 dedicated Content Admin with strictly isolated permissions.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = adminNameInput,
                            onValueChange = { adminNameInput = it },
                            label = { Text("Admin Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = adminEmailInput,
                            onValueChange = { adminEmailInput = it },
                            label = { Text("Admin Email Address *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text("Assign to Application Tenant *", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    }

                    items(SampleData.appsListState) { app ->
                        val isSelected = app.id == targetAppIdInput
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                            ),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { targetAppIdInput = app.id }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Current Admin: ${app.assignedAdminName.ifEmpty { "None" }}", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminNameInput.isBlank() || adminEmailInput.isBlank() || targetAppIdInput.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val success = SampleData.createOrAssignContentAdmin(
                            name = adminNameInput.trim(),
                            email = adminEmailInput.trim(),
                            appId = targetAppIdInput
                        )

                        if (success) {
                            Toast.makeText(context, "✅ Content Admin provisioned successfully!", Toast.LENGTH_SHORT).show()
                            showCreateAdminDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Assign Content Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAdminDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. ADD / EDIT CURRENT AFFAIR DIALOG (WITH MULTI-APP TARGET CHECKBOXES)
    if (showAddCurrentAffairDialog) {
        var titleInput by remember { mutableStateOf(currentAffairToEdit?.title ?: "") }
        var categoryInput by remember { mutableStateOf(currentAffairToEdit?.category ?: "AP Current Affairs") }
        var monthInput by remember { mutableStateOf(currentAffairToEdit?.month ?: "September 2026") }
        var contentInput by remember { mutableStateOf(currentAffairToEdit?.content ?: "") }
        var selectedAppIds by remember {
            mutableStateOf(currentAffairToEdit?.mappedAppIds ?: listOf(selectedManagedAppId))
        }

        AlertDialog(
            onDismissRequest = { showAddCurrentAffairDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Article, contentDescription = null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (currentAffairToEdit == null) "Create Current Affair" else "Edit Current Affair", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = categoryInput,
                            onValueChange = { categoryInput = it },
                            label = { Text("Category (e.g. AP State, National, Economy, Schemes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = monthInput,
                            onValueChange = { monthInput = it },
                            label = { Text("Month & Year (e.g. September 2026)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            label = { Text("Content Bullet Points *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    }

                    if (isSuperAdmin) {
                        item {
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Text(
                                "🚀 Super Admin Multi-App Publishing (Select target apps):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E1B4B)
                            )
                        }

                        items(SampleData.appsListState) { app ->
                            val isChecked = selectedAppIds.contains(app.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAppIds = if (isChecked) {
                                            selectedAppIds - app.id
                                        } else {
                                            selectedAppIds + app.id
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedAppIds = if (checked) selectedAppIds + app.id else selectedAppIds - app.id
                                    }
                                )
                                Text(app.name, fontSize = 12.sp, fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isBlank() || contentInput.isBlank()) {
                            Toast.makeText(context, "Please fill in title and content", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val targetIds = if (isSuperAdmin && selectedAppIds.isNotEmpty()) selectedAppIds else listOf(selectedManagedAppId)
                        val newCa = (currentAffairToEdit ?: CurrentAffair(
                            id = "ca-${System.currentTimeMillis()}",
                            appId = selectedManagedAppId,
                            month = monthInput.trim(),
                            category = categoryInput.trim(),
                            title = titleInput.trim(),
                            content = contentInput.trim(),
                            publishedDate = "2026-09-10"
                        )).copy(
                            month = monthInput.trim(),
                            category = categoryInput.trim(),
                            title = titleInput.trim(),
                            content = contentInput.trim()
                        )

                        SampleData.publishCurrentAffairToApps(newCa, targetIds)
                        Toast.makeText(context, "✅ Published Current Affair to ${targetIds.size} apps!", Toast.LENGTH_SHORT).show()
                        showAddCurrentAffairDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Publish Current Affair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCurrentAffairDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. ADD / EDIT MONTHLY PDF DIALOG
    if (showAddMonthlyPdfDialog) {
        var pdfTitle by remember { mutableStateOf(monthlyPdfToEdit?.title ?: "") }
        var pdfMonth by remember { mutableStateOf(monthlyPdfToEdit?.month ?: "September") }
        var pdfYear by remember { mutableStateOf(monthlyPdfToEdit?.year ?: "2026") }
        var pdfUrl by remember { mutableStateOf(monthlyPdfToEdit?.pdfUrl ?: "https://example.com/digest/september-2026.pdf") }
        var pdfPages by remember { mutableStateOf(monthlyPdfToEdit?.pagesCount?.toString() ?: "52") }
        var pdfLanguage by remember { mutableStateOf(monthlyPdfToEdit?.language ?: "Telugu & English") }
        var pdfDescription by remember { mutableStateOf(monthlyPdfToEdit?.description ?: "Complete monthly digest compilation.") }
        var selectedPdfAppIds by remember {
            mutableStateOf(monthlyPdfToEdit?.mappedAppIds ?: listOf(selectedManagedAppId))
        }

        AlertDialog(
            onDismissRequest = { showAddMonthlyPdfDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (monthlyPdfToEdit == null) "Upload Monthly PDF" else "Edit Monthly PDF", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = pdfTitle,
                            onValueChange = { pdfTitle = it },
                            label = { Text("PDF Document Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pdfMonth,
                                onValueChange = { pdfMonth = it },
                                label = { Text("Month") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = pdfYear,
                                onValueChange = { pdfYear = it },
                                label = { Text("Year") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = pdfPages,
                            onValueChange = { pdfPages = it },
                            label = { Text("Number of Pages") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pdfLanguage,
                            onValueChange = { pdfLanguage = it },
                            label = { Text("Language (e.g. Telugu & English)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pdfDescription,
                            onValueChange = { pdfDescription = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    if (isSuperAdmin) {
                        item {
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Text(
                                "🚀 Multi-App PDF Sharing (Check all apps that receive this PDF):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E1B4B)
                            )
                        }

                        items(SampleData.appsListState) { app ->
                            val isChecked = selectedPdfAppIds.contains(app.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPdfAppIds = if (isChecked) selectedPdfAppIds - app.id else selectedPdfAppIds + app.id
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedPdfAppIds = if (checked) selectedPdfAppIds + app.id else selectedPdfAppIds - app.id
                                    }
                                )
                                Text(app.name, fontSize = 12.sp, fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pdfTitle.isBlank()) {
                            Toast.makeText(context, "Please enter PDF Title", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val targetIds = if (isSuperAdmin && selectedPdfAppIds.isNotEmpty()) selectedPdfAppIds else listOf(selectedManagedAppId)
                        val newPdf = (monthlyPdfToEdit ?: MonthlyCurrentAffairsPdf(
                            id = "pdf-${System.currentTimeMillis()}",
                            appId = selectedManagedAppId,
                            month = pdfMonth.trim(),
                            year = pdfYear.trim(),
                            title = pdfTitle.trim(),
                            pdfUrl = pdfUrl.trim()
                        )).copy(
                            month = pdfMonth.trim(),
                            year = pdfYear.trim(),
                            title = pdfTitle.trim(),
                            pagesCount = pdfPages.toIntOrNull() ?: 48,
                            language = pdfLanguage.trim(),
                            description = pdfDescription.trim()
                        )

                        SampleData.publishPdfToApps(newPdf, targetIds)
                        Toast.makeText(context, "✅ Published Monthly PDF to ${targetIds.size} apps!", Toast.LENGTH_SHORT).show()
                        showAddMonthlyPdfDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Publish PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMonthlyPdfDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. ADD / EDIT POSTER DIALOG
    if (showAddPosterDialog) {
        var posterTitle by remember { mutableStateOf(posterToEdit?.title ?: "") }
        var posterType by remember { mutableStateOf(posterToEdit?.posterType ?: "Home Screen Poster") }
        var promoText by remember { mutableStateOf(posterToEdit?.promotionalText ?: "SPECIAL OFFER • ALL ACCESS") }
        var ctaText by remember { mutableStateOf(posterToEdit?.ctaText ?: "CLAIM NOW") }
        var offerDetails by remember { mutableStateOf(posterToEdit?.offerDetails ?: "") }
        var imageUrl by remember { mutableStateOf(posterToEdit?.imageUrl ?: "https://images.unsplash.com/photo-1577083552431-6e5fd01aa342?auto=format&fit=crop&w=1200&q=80") }
        var selectedPosterAppIds by remember {
            mutableStateOf(posterToEdit?.mappedAppIds ?: listOf(selectedManagedAppId))
        }

        AlertDialog(
            onDismissRequest = { showAddPosterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (posterToEdit == null) "Create Promotional Poster" else "Edit Poster", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = posterTitle,
                            onValueChange = { posterTitle = it },
                            label = { Text("Poster Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = posterType,
                            onValueChange = { posterType = it },
                            label = { Text("Poster Type (e.g. Festival, Exam Update, Home Screen)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = promoText,
                            onValueChange = { promoText = it },
                            label = { Text("Promotional Headline") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = ctaText,
                            onValueChange = { ctaText = it },
                            label = { Text("Call to Action Button Text") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = offerDetails,
                            onValueChange = { offerDetails = it },
                            label = { Text("Offer Details & Coupon Info") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    if (isSuperAdmin) {
                        item {
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Text(
                                "🚀 Multi-App Poster Publishing (Select Target Apps):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E1B4B)
                            )
                        }

                        items(SampleData.appsListState) { app ->
                            val isChecked = selectedPosterAppIds.contains(app.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPosterAppIds = if (isChecked) selectedPosterAppIds - app.id else selectedPosterAppIds + app.id
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedPosterAppIds = if (checked) selectedPosterAppIds + app.id else selectedPosterAppIds - app.id
                                    }
                                )
                                Text(app.name, fontSize = 12.sp, fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (posterTitle.isBlank()) {
                            Toast.makeText(context, "Please enter Poster Title", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val targetIds = if (isSuperAdmin && selectedPosterAppIds.isNotEmpty()) selectedPosterAppIds else listOf(selectedManagedAppId)
                        val newPoster = (posterToEdit ?: PromotionalPosterModel(
                            id = "post-${System.currentTimeMillis()}",
                            appId = selectedManagedAppId,
                            title = posterTitle.trim(),
                            imageUrl = imageUrl.trim(),
                            promotionalText = promoText.trim(),
                            ctaText = ctaText.trim()
                        )).copy(
                            title = posterTitle.trim(),
                            posterType = posterType.trim(),
                            promotionalText = promoText.trim(),
                            ctaText = ctaText.trim(),
                            offerDetails = offerDetails.trim()
                        )

                        SampleData.publishPosterToApps(newPoster, targetIds)
                        Toast.makeText(context, "✅ Published Poster to ${targetIds.size} apps!", Toast.LENGTH_SHORT).show()
                        showAddPosterDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    Text("Publish Poster")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPosterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// =========================================================================
// SUB-VIEWS FOR SUPER ADMIN & CONTENT ADMIN
// =========================================================================

@Composable
private fun SuperAdminAppDirectoryView(
    onSelectApp: (String) -> Unit,
    onCreateAppClick: () -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Platform Multi-App Directory",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1 Platform • 1 Codebase • ${SampleData.appsListState.size} Active Exam Tenants",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = onCreateAppClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create App", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(SampleData.appsListState) { app ->
            val subjectsCount = SampleData.getSubjectsForApp(app.id).size
            val questionsCount = SampleData.questionsState.count { it.appId == app.id }
            val currentAffairsCount = SampleData.currentAffairsState.count { it.mappedAppIds.contains(app.id) }
            val pdfsCount = SampleData.monthlyPdfsState.count { it.mappedAppIds.contains(app.id) }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(app.primaryColorHex).copy(alpha = 0.15f),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Color(app.primaryColorHex),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = app.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = app.code,
                                            color = Color(0xFF2563EB),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${app.examCategory} • Currency: ${app.currencySymbol} (${app.currencyCode})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Toggle Status Switch
                        Switch(
                            checked = app.isActive,
                            onCheckedChange = { SampleData.toggleAppStatus(app.id) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(app.tagline, fontSize = 12.sp, color = Color(0xFF334155))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metrics Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Subjects", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("$subjectsCount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MCQs", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("$questionsCount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current Affairs", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("$currentAffairsCount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PDFs", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("$pdfsCount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content Admin & Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dedicated Content Admin:", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(
                                text = "${app.assignedAdminName.ifEmpty { "Unassigned" }} (${app.assignedAdminEmail.ifEmpty { "N/A" }})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2563EB),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = { onSelectApp(app.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F172A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Manage App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperAdminContentAdminsView(
    onCreateAdminClick: () -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Content Administrators", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("1 Dedicated Admin per App • Role-Based Isolation", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Button(
                        onClick = onCreateAdminClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        items(SampleData.adminAccountsState) { admin ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = if (admin.role == AdminRole.SUPER_ADMIN) Color(0xFFFEF3C7) else Color(0xFFDBEAFE),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (admin.role == AdminRole.SUPER_ADMIN) Icons.Default.Shield else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (admin.role == AdminRole.SUPER_ADMIN) Color(0xFFD97706) else Color(0xFF2563EB),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(admin.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (admin.status == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                ) {
                                    Text(
                                        text = admin.status,
                                        color = if (admin.status == "ACTIVE") Color(0xFF166534) else Color(0xFF991B1B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(admin.email, fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "Assigned App: ${admin.assignedAppName ?: "Global All Apps"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    if (admin.role != AdminRole.SUPER_ADMIN) {
                        Row {
                            IconButton(onClick = {
                                SampleData.resetContentAdminAccess(admin.id)
                                Toast.makeText(context, "Password reset link sent to ${admin.email}", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.LockReset, contentDescription = "Reset Credentials", tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = {
                                SampleData.disableContentAdmin(admin.id)
                                Toast.makeText(context, "Disabled admin account ${admin.email}", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Block, contentDescription = "Disable Admin", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperAdminMultiAppSharingHub(
    onAddCurrentAffair: () -> Unit,
    onAddPdf: () -> Unit,
    onAddPoster: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF312E81)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🚀 Super Admin Multi-App Sharing Hub",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Publish Current Affairs, Monthly PDFs, and Promotional Posters to MULTIPLE applications simultaneously using ONE single master record!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAddCurrentAffair,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share CA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onAddPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onAddPoster,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share Poster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("Shared Across Multiple Apps:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
        }

        // Shared Current Affairs
        items(SampleData.currentAffairsState.filter { it.isSharedMultiApp }) { ca ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "Shared to ${ca.mappedAppIds.size} Apps",
                                color = Color(0xFF2563EB),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(ca.publishedDate, fontSize = 10.sp, color = Color(0xFF64748B))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ca.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Target Apps: ${ca.mappedAppIds.joinToString(", ")}",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentAdminManagementHub(
    activeApp: AppTenantModel,
    isSuperAdmin: Boolean,
    onOpenJsonImporter: () -> Unit,
    onAddSubject: () -> Unit,
    onEditSubject: (Subject) -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onAddUnit: () -> Unit,
    onAddTopic: () -> Unit,
    onAddCurrentAffair: () -> Unit,
    onAddPdf: () -> Unit,
    onAddPoster: () -> Unit,
    onSelectDifferentApp: () -> Unit
) {
    ContentAdminSyllabusView(
        activeApp = activeApp,
        onAddSubject = onAddSubject,
        onEditSubject = onEditSubject,
        onDeleteSubject = onDeleteSubject,
        onAddUnit = onAddUnit,
        onAddTopic = onAddTopic,
        onLaunchJsonImport = onOpenJsonImporter
    )
}

@Composable
private fun ContentAdminSyllabusView(
    activeApp: AppTenantModel,
    onAddSubject: () -> Unit,
    onEditSubject: (Subject) -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onAddUnit: () -> Unit,
    onAddTopic: () -> Unit,
    onLaunchJsonImport: () -> Unit
) {
    val subjects = SampleData.getSubjectsForApp(activeApp.id)
    var selectedSubjectId by remember(subjects) { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
    val currentSubject = subjects.find { it.id == selectedSubjectId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(activeApp.primaryColorHex)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${activeApp.name} Syllabus Hierarchy",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Subject → Unit → Topic → Subtopic → MCQ Set",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onLaunchJsonImport,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = Color(activeApp.primaryColorHex)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Subject Selector Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subjects (${subjects.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                TextButton(onClick = onAddSubject) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Subject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subjects) { subject ->
                    val isSelected = subject.id == selectedSubjectId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectId = subject.id },
                        label = { Text(subject.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Units & Topics under Current Subject
        if (currentSubject != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Units in ${currentSubject.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                    TextButton(onClick = onAddUnit) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Unit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            items(currentSubject.units) { unit ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(unit.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF)) {
                                Text(
                                    "${unit.topics.size} Topics",
                                    color = Color(0xFF2563EB),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (!unit.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(unit.description, fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Topics List
                        unit.topics.forEach { topic ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowRight, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(topic.name, fontSize = 12.sp, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                                Text("${topic.subtopics.size} Subtopics", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No subjects created yet for ${activeApp.name}", color = Color(0xFF64748B), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onAddSubject, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
                            Text("Create First Subject")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentAdminJsonUploadLanding(
    activeApp: AppTenantModel,
    onLaunchJsonImport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFF3B82F6), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Universal JSON Content Upload", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Scoped strictly to: ${activeApp.name}", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Upload and validate any JSON structure containing Subject, Unit, Topic, Subtopic, or MCQ set data. Automatically checks duplicate IDs and question texts with instant rollback protection.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onLaunchJsonImport,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Launch JSON Import Wizard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Recent Import Batches on ${activeApp.name}:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
        }

        val appBatches = SampleData.importHistoryState.filter { it.appId == activeApp.id }
        if (appBatches.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No import batches recorded yet for this application.", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(appBatches) { batch ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(batch.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${batch.importedCount} MCQs Imported", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text(batch.destinationPath, fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentAdminCurrentAffairsAndPdfView(
    activeApp: AppTenantModel,
    onAddCurrentAffair: () -> Unit,
    onAddPdf: () -> Unit
) {
    val caList = SampleData.getCurrentAffairsForApp(activeApp.id)
    val pdfList = SampleData.getPdfsForApp(activeApp.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAddCurrentAffair,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Current Affair", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAddPdf,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Text("Monthly PDF Compilations (${pdfList.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
        }

        items(pdfList) { pdf ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFFEE2E2), modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pdf.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        Text("${pdf.pagesCount} Pages • ${pdf.language} • ${pdf.fileSizeBytes}", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }

        item {
            Text("Current Affairs Feed (${caList.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
        }

        items(caList) { ca ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEFF6FF)) {
                            Text(ca.category, color = Color(0xFF2563EB), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text(ca.publishedDate, fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ca.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(ca.content, fontSize = 11.sp, color = Color(0xFF475569), maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun ContentAdminPostersView(
    activeApp: AppTenantModel,
    onAddPoster: () -> Unit
) {
    val posters = SampleData.getPostersForApp(activeApp.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Promotional Posters (${posters.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                Button(
                    onClick = onAddPoster,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Poster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(posters) { poster ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                            Text(poster.posterType, color = Color(0xFFB45309), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = if (poster.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)) {
                            Text(if (poster.isActive) "ACTIVE" else "INACTIVE", color = if (poster.isActive) Color(0xFF166534) else Color(0xFF991B1B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(poster.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(poster.promotionalText, fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PlatformAuditLogsView(
    appFilter: String?
) {
    val logs = remember(SampleData.auditLogsState.size, appFilter) {
        if (appFilter == null) {
            SampleData.auditLogsState
        } else {
            SampleData.auditLogsState.filter { it.appId == appFilter || it.appId == "global" }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Platform Audit & Activity Log", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        if (appFilter == null) "Complete chronological record of all administrative actions across the platform." else "Activity logs for $appFilter",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }

        items(logs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                log.actionType,
                                color = Color(0xFF2563EB),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(log.timestamp, fontSize = 10.sp, color = Color(0xFF64748B))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(log.details, fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Admin: ${log.adminName} (${log.adminRole}) • App: ${log.appName}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
