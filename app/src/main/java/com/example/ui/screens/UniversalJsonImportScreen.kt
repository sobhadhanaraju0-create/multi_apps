package com.example.ui.screens

import android.widget.Toast
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.ImportBatchRecord
import com.example.model.Question
import com.example.model.Subject
import com.example.util.FieldMappingConfig
import com.example.util.JsonParseResult
import com.example.util.ParsedMcqItem
import com.example.util.UniversalMcqParser
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalJsonImportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Navigation / View mode: 0 = Import Wizard, 1 = Import History & Rollback
    var activeViewTab by remember { mutableIntStateOf(0) }

    // Step state in wizard: 1 = Input & Destination, 2 = Mapping & Auto-Detect, 3 = Preview & Validate, 4 = Success
    var currentStep by remember { mutableIntStateOf(1) }

    // Active Admin & App Context
    val currentAdmin = SampleData.currentAdminAccount.value
    var selectedAppId by remember { mutableStateOf(currentAdmin.assignedAppId ?: SampleData.currentActiveAppId.value) }
    val activeApp = SampleData.appsListState.find { it.id == selectedAppId } ?: SampleData.getActiveApp()

    // Destination State
    val subjects = remember(selectedAppId, SampleData.subjectsState.size) {
        SampleData.getSubjectsForApp(selectedAppId).ifEmpty { SampleData.subjectsState }
    }
    var selectedSubjectId by remember(subjects) { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
    val currentSubject = subjects.find { it.id == selectedSubjectId }

    var selectedUnitId by remember(currentSubject) { mutableStateOf(currentSubject?.units?.firstOrNull()?.id ?: "") }
    val currentUnit = currentSubject?.units?.find { it.id == selectedUnitId }

    var selectedTopicId by remember(currentUnit) { mutableStateOf(currentUnit?.topics?.firstOrNull()?.id ?: "") }
    val currentTopic = currentUnit?.topics?.find { it.id == selectedTopicId }

    var selectedSubtopic by remember(currentTopic) { mutableStateOf(currentTopic?.subtopics?.firstOrNull() ?: "General Practice") }
    var targetSetName by remember { mutableStateOf("Set 01 - General Practice") }
    var overrideWithAdminDestination by remember { mutableStateOf(true) }

    // JSON Input State
    var jsonInputText by remember { mutableStateOf(UniversalMcqParser.PRESET_FLAT_ARRAY) }
    var uploadedFileName by remember { mutableStateOf("mcq_import_sample.json") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                    reader.readText()
                }
                if (content != null) {
                    jsonInputText = content
                    uploadedFileName = "uploaded_file.json"
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Custom Mapping Config
    var customConfig by remember { mutableStateOf(FieldMappingConfig()) }

    // Parse Result State
    var parseResult by remember { mutableStateOf<JsonParseResult?>(null) }

    // Duplicate Handling Mode: "SKIP", "REPLACE", "KEEP_ALL"
    var duplicateHandlingMode by remember { mutableStateOf("SKIP") }

    // Import Execution State
    var isImporting by remember { mutableStateOf(false) }
    var importProgress by remember { mutableFloatStateOf(0f) }

    // Filter preview state
    var previewFilter by remember { mutableStateOf("ALL") } // "ALL", "VALID", "DUPLICATES", "INVALID"
    var previewSearchQuery by remember { mutableStateOf("") }
    var showErrorReportDialog by remember { mutableStateOf(false) }

    // Parse JSON whenever input or config changes
    fun executeParse() {
        if (jsonInputText.isBlank()) return
        val currentBatchId = "batch-${UUID.randomUUID().toString().take(8)}"
        parseResult = UniversalMcqParser.parseJsonString(
            jsonString = jsonInputText,
            appId = selectedAppId,
            targetSubjectId = selectedSubjectId,
            targetUnitId = selectedUnitId,
            targetTopicId = selectedTopicId,
            targetSubtopic = selectedSubtopic,
            targetSetName = targetSetName,
            overrideWithAdminDestination = overrideWithAdminDestination,
            existingQuestions = SampleData.questionsState.filter { it.appId == selectedAppId },
            importBatchId = currentBatchId,
            customConfig = customConfig
        )
    }

    LaunchedEffect(jsonInputText, selectedAppId, selectedSubjectId, selectedUnitId, selectedTopicId, selectedSubtopic, targetSetName, overrideWithAdminDestination, customConfig) {
        executeParse()
    }

    Scaffold(
        topBar = {
            Surface(
                color = AppPurpleAppBar,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { onBack() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = AppTextDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Universal MCQ JSON Import Engine",
                                    color = AppTextDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Auto-Detect Keys • Upload Anywhere • Auto-Mapping • Rollback",
                                    color = AppTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Active View Selector (Wizard vs History)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Row(modifier = Modifier.padding(3.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (activeViewTab == 0) Color(0xFF2563EB) else Color.Transparent,
                                    modifier = Modifier.clickable { activeViewTab = 0 }
                                ) {
                                    Text(
                                        text = "📥 Import Wizard",
                                        color = if (activeViewTab == 0) Color.White else AppTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (activeViewTab == 1) Color(0xFF2563EB) else Color.Transparent,
                                    modifier = Modifier.clickable { activeViewTab = 1 }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = "📜 History (${SampleData.importHistoryState.size})", color = if (activeViewTab == 1) Color.White else AppTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    if (activeViewTab == 0) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Wizard Step Breadcrumbs
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WizardStepBadge(stepNumber = 1, title = "Upload & Destination", isActive = currentStep == 1, isCompleted = currentStep > 1) { currentStep = 1 }
                            WizardStepDivider()
                            WizardStepBadge(stepNumber = 2, title = "Auto-Mapping", isActive = currentStep == 2, isCompleted = currentStep > 2) { if (parseResult != null) currentStep = 2 }
                            WizardStepDivider()
                            WizardStepBadge(stepNumber = 3, title = "Preview & Validate", isActive = currentStep == 3, isCompleted = currentStep > 3) { if (parseResult != null) currentStep = 3 }
                        }
                    }
                }
            }
        },
        containerColor = AppPurpleBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activeViewTab == 1) {
                // VIEW 1: IMPORT HISTORY & ROLLBACK SCREEN
                ImportHistoryView(
                    historyRecords = SampleData.importHistoryState,
                    onRollback = { batchId ->
                        val success = SampleData.rollbackImportBatch(batchId)
                        if (success) {
                            Toast.makeText(context, "🔄 Import Batch Successfully Rolled Back!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Unable to rollback batch.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } else {
                // VIEW 0: IMPORT WIZARD STEPS
                when (currentStep) {
                    1 -> Step1DestinationAndUpload(
                        subjects = subjects,
                        selectedSubjectId = selectedSubjectId,
                        onSubjectSelect = { selectedSubjectId = it },
                        selectedUnitId = selectedUnitId,
                        onUnitSelect = { selectedUnitId = it },
                        selectedTopicId = selectedTopicId,
                        onTopicSelect = { selectedTopicId = it },
                        selectedSubtopic = selectedSubtopic,
                        onSubtopicSelect = { selectedSubtopic = it },
                        targetSetName = targetSetName,
                        onSetNameChange = { targetSetName = it },
                        overrideWithAdminDestination = overrideWithAdminDestination,
                        onOverrideToggle = { overrideWithAdminDestination = it },
                        jsonInputText = jsonInputText,
                        onJsonInputChange = { jsonInputText = it },
                        uploadedFileName = uploadedFileName,
                        onFilePickerLaunch = { filePickerLauncher.launch("*/*") },
                        onPresetSelect = { presetName, presetCode ->
                            jsonInputText = presetCode
                            uploadedFileName = "$presetName.json"
                            Toast.makeText(context, "Loaded $presetName Preset JSON!", Toast.LENGTH_SHORT).show()
                        },
                        parseResult = parseResult,
                        onNext = {
                            if (parseResult != null && parseResult!!.totalFound > 0) {
                                currentStep = 2
                            } else {
                                Toast.makeText(context, "Please enter valid JSON content with at least 1 MCQ.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    2 -> Step2FieldMappingView(
                        parseResult = parseResult,
                        customConfig = customConfig,
                        onConfigChange = { newConfig -> customConfig = newConfig },
                        onBackStep = { currentStep = 1 },
                        onNextStep = { currentStep = 3 }
                    )

                    3 -> Step3PreviewAndValidateView(
                        parseResult = parseResult,
                        duplicateHandlingMode = duplicateHandlingMode,
                        onDuplicateModeChange = { duplicateHandlingMode = it },
                        previewFilter = previewFilter,
                        onFilterChange = { previewFilter = it },
                        searchQuery = previewSearchQuery,
                        onSearchChange = { previewSearchQuery = it },
                        onShowErrorReport = { showErrorReportDialog = true },
                        isImporting = isImporting,
                        importProgress = importProgress,
                        onConfirmImport = {
                            if (parseResult != null && parseResult!!.parsedItems.isNotEmpty()) {
                                isImporting = true
                                importProgress = 0.2f

                                // Candidates for import based on duplicateHandlingMode
                                val candidates = parseResult!!.parsedItems.filter { it.isValid }
                                val toImport = when (duplicateHandlingMode) {
                                    "SKIP" -> candidates.filterNot { it.isDuplicate }
                                    "REPLACE" -> candidates
                                    else -> candidates
                                }

                                val finalQuestions = toImport.map { it.question }

                                val batchRecord = ImportBatchRecord(
                                    id = "batch-${UUID.randomUUID().toString().take(8)}",
                                    appId = selectedAppId,
                                    fileName = uploadedFileName,
                                    adminEmail = currentAdmin.email,
                                    timestamp = "2026-09-10 11:45",
                                    destinationPath = "[${activeApp.name}] ${currentSubject?.name ?: selectedSubjectId} > ${currentUnit?.name ?: selectedUnitId} > ${currentTopic?.name ?: selectedTopicId} > $selectedSubtopic > $targetSetName",
                                    totalFound = parseResult!!.totalFound,
                                    importedCount = finalQuestions.size,
                                    duplicateCount = parseResult!!.duplicateCount,
                                    errorCount = parseResult!!.errorCount,
                                    importedQuestionIds = finalQuestions.map { it.id }
                                )

                                SampleData.addQuestionsBatch(
                                    questionsToImport = finalQuestions,
                                    batchRecord = batchRecord,
                                    replaceDuplicates = duplicateHandlingMode == "REPLACE"
                                )

                                importProgress = 1.0f
                                isImporting = false
                                currentStep = 4
                                Toast.makeText(context, "🎉 Successfully Imported ${finalQuestions.size} MCQs!", Toast.LENGTH_LONG).show()
                            }
                        },
                        onBackStep = { currentStep = 2 }
                    )

                    4 -> Step4SuccessConfirmation(
                        parseResult = parseResult,
                        destinationPath = "${currentSubject?.name ?: selectedSubjectId} > ${currentUnit?.name ?: selectedUnitId} > $targetSetName",
                        onImportAnother = {
                            currentStep = 1
                            jsonInputText = UniversalMcqParser.PRESET_FLAT_ARRAY
                        },
                        onViewHistory = {
                            activeViewTab = 1
                        },
                        onFinish = onBack
                    )
                }
            }
        }
    }

    // ERROR REPORT DIALOG
    if (showErrorReportDialog && parseResult != null) {
        val errorItems = parseResult!!.parsedItems.filter { !it.isValid }
        AlertDialog(
            onDismissRequest = { showErrorReportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Error Report (${errorItems.size} Failed MCQs)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("The following items failed validation and were skipped during import:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                        items(errorItems) { item ->
                            Surface(
                                color = Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Item #${item.rawIndex}", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), fontSize = 12.sp)
                                    item.errorMessages.forEach { msg ->
                                        Text("• $msg", color = Color(0xFFDC2626), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reportText = errorItems.joinToString("\n\n") { "Item #${it.rawIndex}:\n" + it.errorMessages.joinToString("\n") }
                        clipboardManager.setText(AnnotatedString(reportText))
                        Toast.makeText(context, "Copied error report to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Error Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showErrorReportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// ==================== STEP 1: DESTINATION & UPLOAD ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1DestinationAndUpload(
    subjects: List<Subject>,
    selectedSubjectId: String,
    onSubjectSelect: (String) -> Unit,
    selectedUnitId: String,
    onUnitSelect: (String) -> Unit,
    selectedTopicId: String,
    onTopicSelect: (String) -> Unit,
    selectedSubtopic: String,
    onSubtopicSelect: (String) -> Unit,
    targetSetName: String,
    onSetNameChange: (String) -> Unit,
    overrideWithAdminDestination: Boolean,
    onOverrideToggle: (Boolean) -> Unit,
    jsonInputText: String,
    onJsonInputChange: (String) -> Unit,
    uploadedFileName: String,
    onFilePickerLaunch: () -> Unit,
    onPresetSelect: (String, String) -> Unit,
    parseResult: JsonParseResult?,
    onNext: () -> Unit
) {
    val currentSubject = subjects.find { it.id == selectedSubjectId }
    val currentUnit = currentSubject?.units?.find { it.id == selectedUnitId }
    val currentTopic = currentUnit?.topics?.find { it.id == selectedTopicId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // CARD 1: TARGET DESTINATION SELECTION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFDBEAFE), modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("1. Select Destination Hierarchy", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                Text("Pick Subject, Unit, Topic, Subtopic & MCQ Set", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }

                        // Override Priority Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (overrideWithAdminDestination) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = if (overrideWithAdminDestination) "📌 Admin Selected Priority" else "🌳 JSON Hierarchy Priority",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (overrideWithAdminDestination) Color(0xFF166534) else Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: Subject Selector
                    AdminDropdownSelector(
                        label = "Subject",
                        items = subjects.map { it.id to it.name },
                        selectedId = selectedSubjectId,
                        onItemSelected = { onSubjectSelect(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: Unit Selector
                    if (currentSubject != null) {
                        val unitItems = listOf("" to "None (Direct to Subject)") + currentSubject.units.map { it.id to it.name }
                        AdminDropdownSelector(
                            label = "Unit",
                            items = unitItems,
                            selectedId = selectedUnitId,
                            onItemSelected = { onUnitSelect(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Row 3: Topic Selector
                    if (currentUnit != null) {
                        val topicItems = listOf("" to "None (Direct to Unit)") + currentUnit.topics.map { it.id to it.name }
                        AdminDropdownSelector(
                            label = "Topic",
                            items = topicItems,
                            selectedId = selectedTopicId,
                            onItemSelected = { onTopicSelect(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Row 4: Subtopic & MCQ Set Name Inputs
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedSubtopic,
                            onValueChange = { onSubtopicSelect(it) },
                            label = { Text("Subtopic Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetSetName,
                            onValueChange = { onSetNameChange(it) },
                            label = { Text("MCQ Set Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Override Checkbox
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = overrideWithAdminDestination,
                            onCheckedChange = { onOverrideToggle(it) }
                        )
                        Text(
                            "Force Admin destination override (Ignore hierarchy embedded in JSON)",
                            fontSize = 11.sp,
                            color = Color(0xFF334155)
                        )
                    }

                    // Current Destination Breadcrumb Banner
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Target Path: ${currentSubject?.name ?: selectedSubjectId} ➔ ${currentUnit?.name ?: "Unit"} ➔ $targetSetName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }

        // CARD 2: JSON INPUT & PRESETS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFE0E7FF), modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("2. Universal JSON Source Input", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                Text("Paste JSON string or select a pre-formatted preset template", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Templates Bar
                    Text("⚡ Quick Preset Templates (1-Click Test):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            PresetChip("Flat Array JSON", Icons.Default.ViewList) {
                                onPresetSelect("Flat_Array_MCQs", UniversalMcqParser.PRESET_FLAT_ARRAY)
                            }
                        }
                        item {
                            PresetChip("Single MCQ", Icons.Default.Description) {
                                onPresetSelect("Single_MCQ_Object", UniversalMcqParser.PRESET_SINGLE_OBJECT)
                            }
                        }
                        item {
                            PresetChip("MCQ Set JSON", Icons.Default.FolderZip) {
                                onPresetSelect("Set_Based_MCQs", UniversalMcqParser.PRESET_SET_BASED)
                            }
                        }
                        item {
                            PresetChip("Nested Hierarchy", Icons.Default.AccountTree) {
                                onPresetSelect("Nested_Syllabus_Hierarchy", UniversalMcqParser.PRESET_NESTED_HIERARCHY)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("JSON Source:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        Button(
                            onClick = { onFilePickerLaunch() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload .JSON File", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // JSON Code Editor
                    OutlinedTextField(
                        value = jsonInputText,
                        onValueChange = { onJsonInputChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        label = { Text("Raw JSON Code / Text", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A)
                        ),
                        placeholder = { Text("Paste JSON content here...", fontSize = 11.sp) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Indicator Box
                    if (parseResult != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (parseResult.totalFound > 0) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, if (parseResult.totalFound > 0) Color(0xFF86EFAC) else Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (parseResult.totalFound > 0) Icons.Default.CheckCircle else Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (parseResult.totalFound > 0) Color(0xFF16A34A) else Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Structure: ${parseResult.detectedStructure}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Found ${parseResult.totalFound} MCQs (${parseResult.validCount} Valid, ${parseResult.duplicateCount} Duplicates, ${parseResult.errorCount} Errors)",
                                            fontSize = 10.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next Button
                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = parseResult != null && parseResult.totalFound > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Proceed to Auto-Mapping", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ==================== STEP 2: FIELD MAPPING ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2FieldMappingView(
    parseResult: JsonParseResult?,
    customConfig: FieldMappingConfig,
    onConfigChange: (FieldMappingConfig) -> Unit,
    onBackStep: () -> Unit,
    onNextStep: () -> Unit
) {
    if (parseResult == null) return

    val availableKeys = parseResult.detectedKeys

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Auto-Detected Keys & Field Mapper", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                    Text("The parser automatically identified keys in your JSON. You can override mappings below if required.", fontSize = 11.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detected Keys Tags
                    Text("Detected JSON Keys in Source File:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableKeys) { key ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(key, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mapping Selectors
                    Text("Map JSON Keys to Target Database Schema:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(10.dp))

                    KeyMappingDropdownRow(
                        label = "Question Text Key",
                        selectedKey = customConfig.questionTextKey,
                        availableKeys = availableKeys,
                        onSelect = { onConfigChange(customConfig.copy(questionTextKey = it)) }
                    )

                    KeyMappingDropdownRow(
                        label = "Correct Answer Key",
                        selectedKey = customConfig.correctAnswerKey,
                        availableKeys = availableKeys,
                        onSelect = { onConfigChange(customConfig.copy(correctAnswerKey = it)) }
                    )

                    KeyMappingDropdownRow(
                        label = "Explanation Key",
                        selectedKey = customConfig.explanationKey,
                        availableKeys = availableKeys,
                        onSelect = { onConfigChange(customConfig.copy(explanationKey = it)) }
                    )

                    KeyMappingDropdownRow(
                        label = "MCQ Set Name Key",
                        selectedKey = customConfig.setNameKey,
                        availableKeys = availableKeys,
                        onSelect = { onConfigChange(customConfig.copy(setNameKey = it)) }
                    )

                    KeyMappingDropdownRow(
                        label = "Difficulty Key",
                        selectedKey = customConfig.difficultyKey,
                        availableKeys = availableKeys,
                        onSelect = { onConfigChange(customConfig.copy(difficultyKey = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sample Parsed Item Preview Card
                    if (parseResult.parsedItems.isNotEmpty()) {
                        val sampleItem = parseResult.parsedItems.first().question
                        Text("Live Mapped Sample Preview (Question #1):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Q: ${sampleItem.questionText}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.height(6.dp))
                                sampleItem.options.forEach { (key, value) ->
                                    val isCorrect = key == sampleItem.correctOption
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text(
                                            "$key: $value",
                                            fontSize = 11.sp,
                                            color = if (isCorrect) Color(0xFF16A34A) else Color(0xFF475569),
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isCorrect) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                                if (!sampleItem.explanation.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Explanation: ${sampleItem.explanation}", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onBackStep) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back")
                        }

                        Button(
                            onClick = onNextStep,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Confirm Mapping & Validate")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==================== STEP 3: PREVIEW & VALIDATE ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step3PreviewAndValidateView(
    parseResult: JsonParseResult?,
    duplicateHandlingMode: String,
    onDuplicateModeChange: (String) -> Unit,
    previewFilter: String,
    onFilterChange: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onShowErrorReport: () -> Unit,
    isImporting: Boolean,
    importProgress: Float,
    onConfirmImport: () -> Unit,
    onBackStep: () -> Unit
) {
    if (parseResult == null) return

    val filteredItems = remember(parseResult, previewFilter, searchQuery) {
        parseResult.parsedItems.filter { item ->
            val matchesFilter = when (previewFilter) {
                "VALID" -> item.isValid && !item.isDuplicate
                "DUPLICATES" -> item.isDuplicate
                "INVALID" -> !item.isValid
                else -> true
            }
            val matchesSearch = searchQuery.isEmpty() || item.question.questionText.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // STATS BAR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pre-Import Validation & Duplicate Check", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatTile(label = "Total Found", count = parseResult.totalFound, color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                        StatTile(label = "Ready / New", count = parseResult.validCount, color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                        StatTile(label = "Duplicates", count = parseResult.duplicateCount, color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                        StatTile(label = "Invalid Items", count = parseResult.errorCount, color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
                    }

                    if (parseResult.errorCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = onShowErrorReport,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Error Report (${parseResult.errorCount} failed)", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // DUPLICATE RESOLUTION MODE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Duplicate Action Strategy:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = duplicateHandlingMode == "SKIP",
                            onClick = { onDuplicateModeChange("SKIP") },
                            label = { Text("Skip Duplicates", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = duplicateHandlingMode == "REPLACE",
                            onClick = { onDuplicateModeChange("REPLACE") },
                            label = { Text("Replace / Overwrite", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = duplicateHandlingMode == "KEEP_ALL",
                            onClick = { onDuplicateModeChange("KEEP_ALL") },
                            label = { Text("Import All as New", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // SEARCH & FILTER TABS
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search parsed questions...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = previewFilter == "ALL", onClick = { onFilterChange("ALL") }, label = { Text("All (${parseResult.totalFound})", fontSize = 11.sp) })
                    FilterChip(selected = previewFilter == "VALID", onClick = { onFilterChange("VALID") }, label = { Text("Valid (${parseResult.validCount})", fontSize = 11.sp) })
                    FilterChip(selected = previewFilter == "DUPLICATES", onClick = { onFilterChange("DUPLICATES") }, label = { Text("Duplicates (${parseResult.duplicateCount})", fontSize = 11.sp) })
                    FilterChip(selected = previewFilter == "INVALID", onClick = { onFilterChange("INVALID") }, label = { Text("Invalid (${parseResult.errorCount})", fontSize = 11.sp) })
                }
            }
        }

        // PARSED MCQS LIST
        items(filteredItems) { parsed ->
            ParsedMcqCard(parsed = parsed)
        }

        // IMPORT BUTTON
        item {
            if (isImporting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(progress = { importProgress }, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Importing MCQs into system database...", fontSize = 12.sp, color = Color(0xFF2563EB))
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onBackStep) {
                        Text("Back")
                    }

                    Button(
                        onClick = onConfirmImport,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm & Import MCQs Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ==================== STEP 4: SUCCESS ====================

@Composable
private fun Step4SuccessConfirmation(
    parseResult: JsonParseResult?,
    destinationPath: String,
    onImportAnother: () -> Unit,
    onViewHistory: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFDCFCE7),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Import Batch Completed!", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Questions have been successfully published to the live system.", fontSize = 12.sp, color = Color(0xFF64748B))

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📌 Destination Path:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF475569))
                Text(destinationPath, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Total Questions Processed:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text("${parseResult?.totalFound ?: 0}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Imported / Live:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text("${parseResult?.validCount ?: 0}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Text("Go to Admin Dashboard", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onImportAnother, modifier = Modifier.weight(1f)) {
                Text("Import Another JSON", fontSize = 11.sp)
            }
            OutlinedButton(onClick = onViewHistory, modifier = Modifier.weight(1f)) {
                Text("View Import History", fontSize = 11.sp)
            }
        }
    }
}

// ==================== VIEW 1: HISTORY & ROLLBACK ====================

@Composable
private fun ImportHistoryView(
    historyRecords: List<ImportBatchRecord>,
    onRollback: (String) -> Unit
) {
    if (historyRecords.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("No past JSON import batches recorded.", color = Color(0xFF64748B), fontSize = 13.sp)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Import History & Version Rollback Logs", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                Text("View past bulk JSON import batches and perform one-click rollbacks.", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            items(historyRecords) { batch ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(batch.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text("Batch ID: ${batch.id} • ${batch.timestamp}", fontSize = 10.sp, color = Color(0xFF64748B))
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (batch.status == "Completed") Color(0xFFDCFCE7) else Color(0xFFFEF2F2)
                            ) {
                                Text(
                                    text = batch.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (batch.status == "Completed") Color(0xFF166534) else Color(0xFF991B1B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Target: ${batch.destinationPath}", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Imported MCQs: ${batch.importedCount}", fontSize = 11.sp, color = Color(0xFF334155))
                            Text("Duplicates: ${batch.duplicateCount}", fontSize = 11.sp, color = Color(0xFF334155))
                            Text("Errors: ${batch.errorCount}", fontSize = 11.sp, color = Color(0xFF334155))
                        }

                        if (batch.status == "Completed") {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { onRollback(batch.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rollback Import Batch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== HELPER COMPOSABLES ====================

@Composable
private fun WizardStepBadge(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            isActive -> Color(0xFF2563EB)
            isCompleted -> Color(0xFF16A34A)
            else -> Color(0xFFF1F5F9)
        },
        border = BorderStroke(1.dp, if (isActive || isCompleted) Color.Transparent else Color(0xFFE2E8F0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("$stepNumber", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (isActive || isCompleted) Color.White else AppTextDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isActive || isCompleted) Color.White else AppTextSecondary)
        }
    }
}

@Composable
private fun WizardStepDivider() {
    Box(
        modifier = Modifier
            .width(12.dp)
            .height(1.dp)
            .background(Color(0xFFCBD5E1))
    )
}

@Composable
private fun PresetChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEEF2FF),
        border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
        }
    }
}

@Composable
private fun KeyMappingDropdownRow(
    label: String,
    selectedKey: String,
    availableKeys: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)

        Box {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.clickable { expanded = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (selectedKey.isEmpty()) "Auto-Detected" else selectedKey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Auto-Detect", fontSize = 12.sp) },
                    onClick = {
                        onSelect("")
                        expanded = false
                    }
                )
                availableKeys.forEach { key ->
                    DropdownMenuItem(
                        text = { Text(key, fontSize = 12.sp) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("$count", fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
        }
    }
}

@Composable
private fun ParsedMcqCard(parsed: ParsedMcqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        border = BorderStroke(
            1.dp,
            when {
                !parsed.isValid -> Color(0xFFFCA5A5)
                parsed.isDuplicate -> Color(0xFFFDE68A)
                else -> Color(0xFFE2E8F0)
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("#${parsed.rawIndex}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.width(6.dp))

                    StatusBadge(
                        label = when {
                            !parsed.isValid -> "INVALID"
                            parsed.isDuplicate -> "DUPLICATE"
                            else -> "READY"
                        },
                        bgColor = when {
                            !parsed.isValid -> Color(0xFFFEF2F2)
                            parsed.isDuplicate -> Color(0xFFFEF3C7)
                            else -> Color(0xFFDCFCE7)
                        },
                        textColor = when {
                            !parsed.isValid -> Color(0xFFDC2626)
                            parsed.isDuplicate -> Color(0xFFD97706)
                            else -> Color(0xFF16A34A)
                        }
                    )
                }

                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide Details" else "View Options", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                parsed.question.questionText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0F172A),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!parsed.isValid) {
                Spacer(modifier = Modifier.height(4.dp))
                parsed.errorMessages.forEach { err ->
                    Text("❌ $err", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            }

            if (parsed.isDuplicate && parsed.duplicateReason != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("⚠️ ${parsed.duplicateReason}", fontSize = 10.sp, color = Color(0xFFD97706))
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                parsed.question.options.forEach { (key, valText) ->
                    val isAns = key == parsed.question.correctOption
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text("$key: $valText", fontSize = 11.sp, fontWeight = if (isAns) FontWeight.Bold else FontWeight.Normal)
                        if (isAns) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                        }
                    }
                }
                if (!parsed.question.explanation.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Exp: ${parsed.question.explanation}", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, bgColor: Color, textColor: Color) {
    Surface(shape = RoundedCornerShape(6.dp), color = bgColor) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDropdownSelector(
    label: String,
    items: List<Pair<String, String>>,
    selectedId: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.find { it.first == selectedId }?.second ?: "Select $label"
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.second, fontSize = 13.sp) },
                    onClick = {
                        onItemSelected(item.first)
                        expanded = false
                    }
                )
            }
        }
    }
}
