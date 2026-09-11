package com.example.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.model.*
import kotlinx.coroutines.launch

/**
 * Multi-App Competitive Exam Platform Data Core
 * Single Codebase powering multiple exam tenants (APPSC, SSC, Banking, DSC, UPSC, etc.)
 */
object SampleData {

    // =========================================================================
    // 1. MULTI-APP TENANT CONFIGURATIONS
    // =========================================================================

    private val initialAppsList = listOf(
        AppTenantModel(
            id = "appsc-group2",
            name = "APPSC Group 2",
            code = "APPSC-G2",
            examCategory = "State PSC",
            tagline = "Executive & Non-Executive State Services",
            iconName = "AccountBalance",
            primaryColorHex = 0xFF2563EB,
            secondaryColorHex = 0xFF1D4ED8,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_group2@examportal.com",
            assignedAdminName = "Ravi Teja (APPSC Group 2 Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
            description = "Complete syllabus MCQs, PYQs & Practice Arena for APPSC Group 2 Prelims and Mains."
        ),
        AppTenantModel(
            id = "appsc-group1",
            name = "APPSC Group 1",
            code = "APPSC-G1",
            examCategory = "State PSC",
            tagline = "Deputy Collector, DSP & Commercial Tax Officer",
            iconName = "EmojiEvents",
            primaryColorHex = 0xFF7C3AED,
            secondaryColorHex = 0xFF6D28D9,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_group1@examportal.com",
            assignedAdminName = "Kavitha Reddy (APPSC Group 1 Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
            description = "High-yield administrative questions, General Studies & state policy analysis."
        ),
        AppTenantModel(
            id = "appsc-group3",
            name = "APPSC Group 3",
            code = "APPSC-G3",
            examCategory = "State PSC",
            tagline = "Panchayat Secretary & Rural Development",
            iconName = "Agriculture",
            primaryColorHex = 0xFF059669,
            secondaryColorHex = 0xFF047857,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_group3@examportal.com",
            assignedAdminName = "Suresh Babu (Group 3 Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "Panchayat Raj systems, rural economy and AP governance."
        ),
        AppTenantModel(
            id = "appsc-group4",
            name = "APPSC Group 4",
            code = "APPSC-G4",
            examCategory = "State PSC",
            tagline = "Junior Assistant cum Computer Assistant",
            iconName = "Computer",
            primaryColorHex = 0xFFEA580C,
            secondaryColorHex = 0xFFC2410C,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_group4@examportal.com",
            assignedAdminName = "Manoj Kumar (Group 4 Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "General Studies & Computer Proficiency Test focus."
        ),
        AppTenantModel(
            id = "ap-dsc",
            name = "AP DSC / TET",
            code = "AP-DSC",
            examCategory = "Teaching (DSC/TET)",
            tagline = "SGT, School Assistant & Teacher Recruitment",
            iconName = "School",
            primaryColorHex = 0xFF0891B2,
            secondaryColorHex = 0xFF0E7490,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_apdsc@examportal.com",
            assignedAdminName = "Nageswara Rao (AP DSC Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "Pedagogy, Child Development, Telugu, English, Maths, Science & Social."
        ),
        AppTenantModel(
            id = "ap-police",
            name = "AP Police SI & Constable",
            code = "AP-POLICE",
            examCategory = "Police & Defence",
            tagline = "Sub-Inspector & Police Constable Recruitment",
            iconName = "Shield",
            primaryColorHex = 0xFFDC2626,
            secondaryColorHex = 0xFFB91C1C,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_appolice@examportal.com",
            assignedAdminName = "Prashanth Naidu (Police Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "Arithmetic, Reasoning, Indian Penal Code basics & General Studies."
        ),
        AppTenantModel(
            id = "ssc-cgl",
            name = "SSC CGL & CHSL",
            code = "SSC-ALL",
            examCategory = "Central SSC",
            tagline = "Staff Selection Commission Combined Graduate Level",
            iconName = "Work",
            primaryColorHex = 0xFF4F46E5,
            secondaryColorHex = 0xFF4338CA,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_ssc@examportal.com",
            assignedAdminName = "Vikram Singh (SSC Content Admin)",
            allowedLanguages = listOf("English", "Hindi"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
            description = "Quantitative Aptitude, General Intelligence, English Comprehension & GA."
        ),
        AppTenantModel(
            id = "banking-ibps",
            name = "Banking IBPS & SBI",
            code = "BANK-PO-CLERK",
            examCategory = "Banking & Insurance",
            tagline = "IBPS PO/Clerk, SBI PO/Clerk & RBI Grade B",
            iconName = "Payments",
            primaryColorHex = 0xFF0D9488,
            secondaryColorHex = 0xFF0F766E,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_banking@examportal.com",
            assignedAdminName = "Ananya Roy (Banking Content Admin)",
            allowedLanguages = listOf("English", "Hindi"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
            description = "Banking Awareness, Financial News, Reasoning Puzzles & Data Interpretation."
        ),
        AppTenantModel(
            id = "railway-rrb",
            name = "Railway RRB NTPC",
            code = "RRB-NTPC",
            examCategory = "Railway RRB",
            tagline = "Non-Technical Popular Categories & Group D",
            iconName = "Train",
            primaryColorHex = 0xFFD97706,
            secondaryColorHex = 0xFFB45309,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_railway@examportal.com",
            assignedAdminName = "Rajesh Verma (Railway Admin)",
            allowedLanguages = listOf("English", "Telugu", "Hindi"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "General Science, Mathematics & Current Events for Indian Railways."
        ),
        AppTenantModel(
            id = "upsc-cse",
            name = "UPSC Civil Services (IAS)",
            code = "UPSC-CSE",
            examCategory = "UPSC Civil Services",
            tagline = "IAS, IPS, IFS & Central Civil Services",
            iconName = "MenuBook",
            primaryColorHex = 0xFF831843,
            secondaryColorHex = 0xFF701A75,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_upsc@examportal.com",
            assignedAdminName = "Dr. Amit Sharma (UPSC Admin)",
            allowedLanguages = listOf("English", "Hindi"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
            description = "In-depth analytical Prelims GS Paper I and CSAT Paper II question sets."
        ),
        AppTenantModel(
            id = "tspsc-all",
            name = "TSPSC Group 1 & 2",
            code = "TSPSC-EXAMS",
            examCategory = "State PSC",
            tagline = "Telangana State Public Service Commission",
            iconName = "LocationCity",
            primaryColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF0369A1,
            currencyCode = "INR",
            currencySymbol = "₹",
            isActive = true,
            assignedAdminEmail = "contentadmin_tspsc@examportal.com",
            assignedAdminName = "Kalyan Rao (TSPSC Admin)",
            allowedLanguages = listOf("Telugu", "English"),
            featuresEnabled = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions"),
            description = "Telangana History, Movement, Society, Geography & Economy."
        )
    )

    val appsListState = mutableStateListOf<AppTenantModel>().apply {
        addAll(initialAppsList)
    }

    // Active App Selected for Student Aspirant Mode
    val currentActiveAppId = mutableStateOf("appsc-group2")

    fun getActiveApp(): AppTenantModel {
        return appsListState.find { it.id == currentActiveAppId.value } ?: appsListState.first()
    }

    fun setActiveApp(appId: String) {
        if (appsListState.any { it.id == appId }) {
            currentActiveAppId.value = appId
            // Update sample profile
            sampleUserProfile = sampleUserProfile.copy(
                targetAppId = appId,
                targetExam = appsListState.find { it.id == appId }?.name ?: "APPSC Group 2"
            )
            // Synchronize dynamic app configuration
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                com.example.repository.AppConfigRepository.getInstance().switchActiveAppTenant(appId)
            }
        }
    }

    // =========================================================================
    // 2. ADMIN ACCOUNTS & ROLE-BASED ACCESS CONTROL (RBAC)
    // =========================================================================

    private val initialAdminAccounts = listOf(
        AdminUserAccount(
            id = "SA-001",
            email = "sobhadhanaraju0@gmail.com",
            name = "Sobha Dhanaraju",
            role = AdminRole.SUPER_ADMIN,
            assignedAppId = null, // Global Super Admin
            assignedAppName = "All Applications (Global)",
            status = "ACTIVE",
            lastLogin = "2026-09-10 11:45",
            allowedPermissions = listOf("ALL_GLOBAL_PERMISSIONS", "APP_MANAGEMENT", "ADMIN_MANAGEMENT", "MULTI_APP_SHARING", "AUDIT_LOGS")
        ),
        AdminUserAccount(
            id = "CA-002",
            email = "contentadmin_group2@examportal.com",
            name = "Ravi Teja",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "appsc-group2",
            assignedAppName = "APPSC Group 2",
            status = "ACTIVE",
            lastLogin = "2026-09-10 10:15",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        ),
        AdminUserAccount(
            id = "CA-001",
            email = "contentadmin_group1@examportal.com",
            name = "Kavitha Reddy",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "appsc-group1",
            assignedAppName = "APPSC Group 1",
            status = "ACTIVE",
            lastLogin = "2026-09-09 16:30",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        ),
        AdminUserAccount(
            id = "CA-005",
            email = "contentadmin_apdsc@examportal.com",
            name = "Nageswara Rao",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "ap-dsc",
            assignedAppName = "AP DSC / TET",
            status = "ACTIVE",
            lastLogin = "2026-09-08 14:20",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        ),
        AdminUserAccount(
            id = "CA-006",
            email = "contentadmin_ssc@examportal.com",
            name = "Vikram Singh",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "ssc-cgl",
            assignedAppName = "SSC CGL & CHSL",
            status = "ACTIVE",
            lastLogin = "2026-09-10 09:10",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        ),
        AdminUserAccount(
            id = "CA-007",
            email = "contentadmin_banking@examportal.com",
            name = "Ananya Roy",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "banking-ibps",
            assignedAppName = "Banking IBPS & SBI",
            status = "ACTIVE",
            lastLogin = "2026-09-07 18:40",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        ),
        AdminUserAccount(
            id = "CA-008",
            email = "contentadmin_upsc@examportal.com",
            name = "Dr. Amit Sharma",
            role = AdminRole.CONTENT_ADMIN,
            assignedAppId = "upsc-cse",
            assignedAppName = "UPSC Civil Services (IAS)",
            status = "ACTIVE",
            lastLogin = "2026-09-10 08:00",
            allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
        )
    )

    val adminAccountsState = mutableStateListOf<AdminUserAccount>().apply {
        addAll(initialAdminAccounts)
    }

    // Active Admin Session (Defaults to Super Admin, switchable for testing single-app Content Admins)
    val currentAdminAccount = mutableStateOf<AdminUserAccount>(initialAdminAccounts[0])
    val currentAdminRoleState = mutableStateOf(AdminRole.SUPER_ADMIN)

    // Backend-Style Authorization Guard
    fun canAdminAccessApp(admin: AdminUserAccount, targetAppId: String): Boolean {
        if (admin.role == AdminRole.SUPER_ADMIN) return true
        return admin.assignedAppId == targetAppId && admin.status == "ACTIVE"
    }

    fun canAdminPerformAction(admin: AdminUserAccount, targetAppId: String, permission: String): Boolean {
        if (admin.status != "ACTIVE") return false
        if (admin.role == AdminRole.SUPER_ADMIN) return true
        if (admin.assignedAppId != targetAppId) return false
        return admin.allowedPermissions.contains(permission)
    }

    // Super Admin: Create New App
    fun createApp(
        newApp: AppTenantModel,
        adminName: String,
        adminEmail: String
    ): Boolean {
        if (appsListState.any { it.id == newApp.id || it.code == newApp.code }) return false
        val finalizedApp = newApp.copy(
            assignedAdminEmail = adminEmail,
            assignedAdminName = adminName
        )
        appsListState.add(finalizedApp)

        // Create or assign Content Admin
        val existingAdminIdx = adminAccountsState.indexOfFirst { it.email.equals(adminEmail, true) }
        if (existingAdminIdx != -1) {
            val existing = adminAccountsState[existingAdminIdx]
            adminAccountsState[existingAdminIdx] = existing.copy(
                assignedAppId = newApp.id,
                assignedAppName = newApp.name,
                status = "ACTIVE"
            )
        } else {
            val newAdmin = AdminUserAccount(
                id = "CA-${(adminAccountsState.size + 1).toString().padStart(3, '0')}",
                email = adminEmail,
                name = adminName,
                role = AdminRole.CONTENT_ADMIN,
                assignedAppId = newApp.id,
                assignedAppName = newApp.name,
                status = "ACTIVE",
                lastLogin = "Never",
                allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
            )
            adminAccountsState.add(newAdmin)
        }

        // Add default subscription plans for this app
        addDefaultSubscriptionPlansForApp(newApp.id, newApp.currencySymbol)

        // Register Dynamic App Config
        val dynamicConfig = DynamicAppConfig(
            appId = newApp.id,
            appName = newApp.name,
            appCode = newApp.code,
            examCategory = newApp.examCategory,
            tagline = newApp.tagline,
            description = newApp.tagline,
            assignedAdminEmail = adminEmail,
            assignedAdminName = adminName,
            currency = DynamicCurrencyConfig(
                currencySymbol = newApp.currencySymbol,
                currencyCode = if (newApp.currencySymbol == "$") "USD" else "INR",
                currencyPosition = CurrencyPosition.PREFIX
            )
        )
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            com.example.repository.AppConfigRepository.getInstance().updateAppConfig(dynamicConfig)
        }

        addAuditLog(
            actionType = "APP_CREATED",
            details = "Super Admin created new application '${newApp.name}' (${newApp.code}) and assigned Content Admin '${adminName}' (${adminEmail})",
            category = "App Management",
            appId = newApp.id,
            appName = newApp.name
        )
        return true
    }

    fun updateApp(updatedApp: AppTenantModel) {
        val idx = appsListState.indexOfFirst { it.id == updatedApp.id }
        if (idx != -1) {
            appsListState[idx] = updatedApp
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                val existing = com.example.repository.AppConfigRepository.getInstance().fetchAppConfig(updatedApp.id).getOrNull()
                if (existing != null) {
                    val updated = existing.copy(
                        appName = updatedApp.name,
                        appCode = updatedApp.code,
                        examCategory = updatedApp.examCategory,
                        tagline = updatedApp.tagline,
                        currency = existing.currency.copy(currencySymbol = updatedApp.currencySymbol)
                    )
                    com.example.repository.AppConfigRepository.getInstance().updateAppConfig(updated)
                }
            }
            addAuditLog(
                actionType = "APP_UPDATED",
                details = "Updated configuration for app '${updatedApp.name}' (${updatedApp.code})",
                category = "App Management",
                appId = updatedApp.id,
                appName = updatedApp.name
            )
        }
    }

    fun toggleAppStatus(appId: String) {
        val idx = appsListState.indexOfFirst { it.id == appId }
        if (idx != -1) {
            val old = appsListState[idx]
            val updated = old.copy(isActive = !old.isActive)
            appsListState[idx] = updated
            addAuditLog(
                actionType = if (updated.isActive) "APP_ACTIVATED" else "APP_DEACTIVATED",
                details = "Toggled status of app '${old.name}' to ${if (updated.isActive) "ACTIVE" else "INACTIVE"}",
                category = "App Management",
                appId = appId,
                appName = old.name
            )
        }
    }

    // Super Admin: Content Admin Management
    fun createOrAssignContentAdmin(name: String, email: String, appId: String): Boolean {
        val app = appsListState.find { it.id == appId } ?: return false
        val idx = adminAccountsState.indexOfFirst { it.email.equals(email, true) }
        if (idx != -1) {
            val old = adminAccountsState[idx]
            adminAccountsState[idx] = old.copy(
                name = name,
                assignedAppId = appId,
                assignedAppName = app.name,
                status = "ACTIVE"
            )
        } else {
            val newAdmin = AdminUserAccount(
                id = "CA-${(adminAccountsState.size + 1).toString().padStart(3, '0')}",
                email = email,
                name = name,
                role = AdminRole.CONTENT_ADMIN,
                assignedAppId = appId,
                assignedAppName = app.name,
                status = "ACTIVE",
                lastLogin = "Never",
                allowedPermissions = listOf("SUBJECT_MANAGEMENT", "JSON_UPLOAD", "CURRENT_AFFAIRS_MANAGEMENT", "POSTER_MANAGEMENT")
            )
            adminAccountsState.add(newAdmin)
        }

        // Update App's assigned admin record
        val appIdx = appsListState.indexOfFirst { it.id == appId }
        if (appIdx != -1) {
            appsListState[appIdx] = app.copy(
                assignedAdminEmail = email,
                assignedAdminName = name
            )
        }

        addAuditLog(
            actionType = "CONTENT_ADMIN_ASSIGNED",
            details = "Assigned Content Admin '$name' ($email) to app '${app.name}'",
            category = "Admin Management",
            appId = appId,
            appName = app.name
        )
        return true
    }

    fun disableContentAdmin(adminId: String) {
        val idx = adminAccountsState.indexOfFirst { it.id == adminId }
        if (idx != -1) {
            val admin = adminAccountsState[idx]
            adminAccountsState[idx] = admin.copy(status = "DISABLED")
            addAuditLog(
                actionType = "CONTENT_ADMIN_DISABLED",
                details = "Disabled Content Admin '${admin.name}' (${admin.email}) for app '${admin.assignedAppName}'",
                category = "Admin Management",
                appId = admin.assignedAppId ?: "global",
                appName = admin.assignedAppName ?: "Global"
            )
        }
    }

    fun resetContentAdminAccess(adminId: String) {
        val idx = adminAccountsState.indexOfFirst { it.id == adminId }
        if (idx != -1) {
            val admin = adminAccountsState[idx]
            adminAccountsState[idx] = admin.copy(status = "PENDING_RESET")
            addAuditLog(
                actionType = "CONTENT_ADMIN_RESET",
                details = "Issued security credentials reset for '${admin.name}' (${admin.email})",
                category = "Admin Management",
                appId = admin.assignedAppId ?: "global",
                appName = admin.assignedAppName ?: "Global"
            )
        }
    }

    // =========================================================================
    // 3. SUBJECT & SYLLABUS HIERARCHY (MULTI-APP TENANTS)
    // =========================================================================

    private val initialSubjectsList = listOf(
        // APPSC Group 2 Subjects
        Subject(
            id = "subj-indian-history",
            appId = "appsc-group2",
            name = "Indian History",
            category = "prelims",
            icon = "Landmark",
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80",
            heroQuote = "Explore the Past\nBuild a Better Future",
            color = "from-amber-600 to-orange-700",
            units = listOf(
                UnitModel(
                    id = "unit-ih-ancient",
                    appId = "appsc-group2",
                    unitNumber = 1,
                    name = "Ancient Indian History",
                    subtitle = "From Indus Valley to Gupta Empire",
                    tagline = "Roots of Our Great Civilization",
                    quote = "“Know Your Past, Shape a Brighter Future”",
                    slogan = "History\nBuilds\nWisdom",
                    description = "Indus Valley Civilization, Vedic Culture, Religious Movements, Mauryas & Guptas",
                    imageUrl = "https://images.unsplash.com/photo-1600100397608-f40b2e3e9d89?auto=format&fit=crop&w=1200&q=80",
                    estimatedMcqs = "80+",
                    completionPercentage = 40,
                    topics = listOf(
                        TopicModel(
                            id = "topic-ih-ancient-civ",
                            appId = "appsc-group2",
                            name = "Indus Valley Civilization",
                            subtitle = "Urban Life, Town Planning & Culture",
                            tagBadge = "Harappan Culture",
                            imageUrl = "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "25+ MCQs",
                            practiceSetsCount = 2,
                            difficulty = "Medium",
                            completionPercentage = 60,
                            subtopics = listOf("General Practice", "Harappan Sites, Town Planning & Economy", "Drainage, Trade & Seals"),
                            notesContent = "Key Indus Valley sites include Harappa (Ravi), Mohenjo-daro (Indus - Great Bath), Lothal (Dockyard), Kalibangan (Ploughed field), and Dholavira (Water reservoir system). Urban planning featured grid layout and burnt bricks."
                        ),
                        TopicModel(
                            id = "topic-ih-vedic",
                            appId = "appsc-group2",
                            name = "Vedic Period",
                            subtitle = "Vedic Literature, Society, Economy & Culture",
                            tagBadge = "Vedic Age",
                            imageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "20+ MCQs",
                            practiceSetsCount = 2,
                            difficulty = "Medium",
                            completionPercentage = 30,
                            subtopics = listOf("General Practice", "Rig Vedic Era & Social Structure", "Later Vedic Polity, Upanishads & Epics"),
                            notesContent = "Early Vedic society was pastoral with tribal assemblies (Sabha, Samiti). Later Vedic age saw the rise of Janapadas, iron technology, Varna stratification, and Vedic literature (Rig, Sama, Yajur, Atharva)."
                        ),
                        TopicModel(
                            id = "topic-ih-religions-empires",
                            appId = "appsc-group2",
                            name = "Buddhism",
                            subtitle = "Life of Buddha, Teachings & Spread",
                            tagBadge = "Spread of Buddhism",
                            imageUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "35+ MCQs",
                            practiceSetsCount = 3,
                            difficulty = "Easy",
                            completionPercentage = 20,
                            subtopics = listOf("General Practice", "Four Noble Truths & Eightfold Path", "Buddhist Councils & Royal Patronage"),
                            notesContent = "Gautama Buddha attained Enlightenment at Bodh Gaya. First sermon at Sarnath (Dharmachakrapravartana). Four Buddhist Councils shaped the Tripitakas and sects (Hinayana, Mahayana)."
                        )
                    )
                ),
                UnitModel(
                    id = "unit-ih-medieval",
                    appId = "appsc-group2",
                    unitNumber = 2,
                    name = "Medieval Indian History",
                    subtitle = "Delhi Sultanate to Mughal Empire",
                    tagline = "Era of Architectural & Cultural Synthesis",
                    quote = "“Monuments of Valor and Cultural Harmony”",
                    slogan = "Heritage\n& Legacy\nForever",
                    description = "Delhi Sultanate, Mughal Empire, Bhakti & Sufi Movements",
                    imageUrl = "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=1200&q=80",
                    estimatedMcqs = "90+",
                    completionPercentage = 20,
                    topics = listOf(
                        TopicModel(
                            id = "topic-ih-sultanate-mughal",
                            appId = "appsc-group2",
                            name = "Delhi Sultanate",
                            subtitle = "Slave, Khalji, Tughlaq & Lodi Dynasties",
                            tagBadge = "Sultanate Era",
                            imageUrl = "https://images.unsplash.com/photo-1545129139-1beb780cf337?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "30+ MCQs",
                            practiceSetsCount = 3,
                            difficulty = "Medium",
                            completionPercentage = 45,
                            subtopics = listOf("General Practice", "Sultanate Dynasties & Architecture", "Iqta System, Market Reforms of Alauddin"),
                            notesContent = "Established in 1206 by Qutb-ud-din Aibak. Key rulers: Iltutmish (silver Tanka), Alauddin Khalji (market control reforms), Muhammad bin Tughlaq (capital shift), and Sikandar Lodi."
                        ),
                        TopicModel(
                            id = "topic-ih-mughal-culture",
                            appId = "appsc-group2",
                            name = "Mughal Empire & Administration",
                            subtitle = "Akbar to Aurangzeb & Mansabdari System",
                            tagBadge = "Mughal Era",
                            imageUrl = "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "40+ MCQs",
                            practiceSetsCount = 3,
                            difficulty = "Hard",
                            completionPercentage = 25,
                            subtopics = listOf("General Practice", "Mughal Administration & Mansabdari", "Akbar's Religious Policy & Sulh-i-Kul"),
                            notesContent = "Founded by Babur in 1526 (Battle of Panipat). Akbar consolidated the empire with Mansabdari system, Todar Mal's Dahsala land revenue, and Din-i-Ilahi."
                        ),
                        TopicModel(
                            id = "topic-ih-bhakti-sufi",
                            appId = "appsc-group2",
                            name = "Bhakti & Sufi Movements",
                            subtitle = "Spiritual Renaissance & Regional Literature",
                            tagBadge = "Spiritual Age",
                            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "20+ MCQs",
                            practiceSetsCount = 2,
                            difficulty = "Easy",
                            completionPercentage = 15,
                            subtopics = listOf("General Practice", "Sufi Silsilahs (Chishti, Suhrawardi)", "Bhakti Saints (Kabir, Nanak, Mirabai)"),
                            notesContent = "Bhakti movement emphasized devotion over rituals. Alvars and Nayanars in South, Kabir and Guru Nanak in North. Sufi saints promoted universal love and composite culture."
                        )
                    )
                ),
                UnitModel(
                    id = "unit-ih-modern",
                    appId = "appsc-group2",
                    unitNumber = 3,
                    name = "Modern Indian History",
                    subtitle = "British Rule to Independence",
                    tagline = "The Struggle for Freedom & Sovereignty",
                    quote = "“Courage That Won Us Our Freedom”",
                    slogan = "Freedom\nThrough\nSacrifice",
                    description = "Advent of Europeans, 1857 Revolt, Socio-Religious Reforms & National Freedom Struggle",
                    imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                    estimatedMcqs = "100+",
                    completionPercentage = 10,
                    topics = listOf(
                        TopicModel(
                            id = "topic-ih-european-advent",
                            appId = "appsc-group2",
                            name = "Advent of Europeans & British Rule",
                            subtitle = "East India Company Expansion & Battles",
                            tagBadge = "Colonial Era",
                            imageUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "25+ MCQs",
                            practiceSetsCount = 2,
                            difficulty = "Medium",
                            completionPercentage = 30,
                            subtopics = listOf("General Practice", "Carnatic Wars & Battle of Plassey 1757", "Subsidiary Alliance & Doctrine of Lapse"),
                            notesContent = "Portuguese (Vasco da Gama 1498), Dutch, English, and French. British East India Company gained political supremacy through Battle of Plassey (1757) and Buxar (1764)."
                        ),
                        TopicModel(
                            id = "topic-ih-revolt-reforms",
                            appId = "appsc-group2",
                            name = "1857 Revolt & Socio-Religious Reforms",
                            subtitle = "Great Uprising & Renaissance Thinkers",
                            tagBadge = "Renaissance",
                            imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "30+ MCQs",
                            practiceSetsCount = 3,
                            difficulty = "Medium",
                            completionPercentage = 15,
                            subtopics = listOf("General Practice", "1857 Revolt Causes & Leaders", "Raja Ram Mohan Roy, Arya Samaj, Phule"),
                            notesContent = "1857 Revolt led by Rani Lakshmibai, Nana Saheb, Tantia Tope. Socio-religious reforms championed by Brahmo Samaj (Raja Ram Mohan Roy), Arya Samaj (Dayanand Saraswati), and Satyashodhak Samaj."
                        ),
                        TopicModel(
                            id = "topic-ih-freedom-struggle",
                            appId = "appsc-group2",
                            name = "Indian National Movement (1885-1947)",
                            subtitle = "Gandhian Movements & Independence",
                            tagBadge = "Freedom Movement",
                            imageUrl = "https://images.unsplash.com/photo-1532375810709-75b1da00537c?auto=format&fit=crop&w=800&q=80",
                            estimatedMcqs = "45+ MCQs",
                            practiceSetsCount = 4,
                            difficulty = "Hard",
                            completionPercentage = 10,
                            subtopics = listOf("General Practice", "Moderate & Extremist Phase (1885-1919)", "Non-Cooperation, Civil Disobedience & Quit India"),
                            notesContent = "Formation of INC in 1885. Partition of Bengal (1905). Mahatma Gandhi led Non-Cooperation (1920), Dandi Salt March (1930), and Quit India (1942), culminating in Independence in 1947."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-geography",
            appId = "appsc-group2",
            name = "Geography",
            category = "prelims",
            icon = "Compass",
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80",
            heroQuote = "Explore Landscapes\nMaster General & Regional Geography",
            color = "from-teal-600 to-emerald-700",
            units = listOf(
                UnitModel(
                    id = "unit-geo-general-physical",
                    appId = "appsc-group2",
                    name = "General & Physical Geography",
                    subtitle = "Solar System, Earth Interior & Geomorphology",
                    description = "Solar System, Earth Interior, Plate Tectonics & Monsoons",
                    imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
                    estimatedMcqs = "20+",
                    completionPercentage = 30,
                    topics = listOf(
                        TopicModel(
                            id = "topic-geo-solar-earth",
                            appId = "appsc-group2",
                            name = "Solar System, Earth Interior & Landforms",
                            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
                            subtopics = listOf("General Practice", "Crust, Mantle, Core & Earthquakes", "Mountains & Drainage Systems")
                        )
                    )
                ),
                UnitModel(
                    id = "unit-geo-india-ap",
                    appId = "appsc-group2",
                    name = "Economic & Human Geography of India & AP",
                    subtitle = "Agriculture, Industries, Demography & Resources",
                    description = "Minerals, Water Resources, Population Distribution & AP Spatial Features",
                    imageUrl = "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?auto=format&fit=crop&w=800&q=80",
                    estimatedMcqs = "30+",
                    completionPercentage = 15,
                    topics = listOf(
                        TopicModel(
                            id = "topic-geo-india-ap-eco",
                            appId = "appsc-group2",
                            name = "Natural Resources & Demographics",
                            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
                            subtopics = listOf("General Practice", "Forest, Mineral & Energy Wealth", "AP Districts & Coastal Ecology")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-society",
            appId = "appsc-group2",
            name = "Indian society",
            category = "prelims",
            icon = "Users",
            imageUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1200&q=80",
            color = "from-orange-500 to-amber-600",
            units = listOf(
                UnitModel(
                    id = "unit-soc-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Structure of Indian Society",
                    description = "Family, Kinship, Marriage, Caste & Tribal Diversity",
                    imageUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-soc-1-1",
                            appId = "appsc-group2",
                            name = "Family, Kinship & Marriage",
                            imageUrl = "https://images.unsplash.com/photo-1511632765486-a01980e01a18?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "Joint Family System", "Kinship Rules")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-mental-ability",
            appId = "appsc-group2",
            name = "mental ability",
            category = "prelims",
            icon = "Brain",
            imageUrl = "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=1200&q=80",
            color = "from-indigo-600 to-blue-700",
            units = listOf(
                UnitModel(
                    id = "unit-ma-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Logical Reasoning & Deductive Thinking",
                    description = "Analogy, Series, Coding-Decoding, Blood Relations & Syllogisms",
                    imageUrl = "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-ma-1-1",
                            appId = "appsc-group2",
                            name = "Analogy, Series & Coding-Decoding",
                            imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "Number & Alphabet Series", "Coding Rules")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-ap-history",
            appId = "appsc-group2",
            name = "social and cultural history of Andhra Pradesh",
            category = "mains",
            icon = "Landmark",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
            color = "from-amber-600 to-orange-700",
            units = listOf(
                UnitModel(
                    id = "unit-aph-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Ancient Andhra Dynasties",
                    description = "Satavahanas, Ikshvakus, Vishnukundins & Eastern Chalukyas",
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-aph-1-1",
                            appId = "appsc-group2",
                            name = "Satavahanas & Ikshvakus of Vijayapuri",
                            imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e421a115?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "Satavahana Administration & Art", "Buddhist Centers")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-constitution",
            appId = "appsc-group2",
            name = "Indian constitution",
            category = "mains",
            icon = "Scale",
            imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=1200&q=80",
            color = "from-blue-700 to-indigo-800",
            units = listOf(
                UnitModel(
                    id = "unit-con-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Nature and Philosophy of Indian Constitution",
                    description = "Preamble, Fundamental Rights, DPSP & Basic Structure",
                    imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-con-1-1",
                            appId = "appsc-group2",
                            name = "Preamble & Salient Features",
                            imageUrl = "https://images.unsplash.com/photo-1505664194779-8beaceb93744?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "Constituent Assembly", "Fundamental Rights")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-economy",
            appId = "appsc-group2",
            name = "Indian and AP economy",
            category = "mains",
            icon = "TrendingUp",
            imageUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80",
            color = "from-emerald-600 to-teal-800",
            units = listOf(
                UnitModel(
                    id = "unit-eco-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Structure and Growth of Indian Economy",
                    description = "National Income, NITI Aayog, Monetary Policy & Banking",
                    imageUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-eco-1-1",
                            appId = "appsc-group2",
                            name = "National Income & Growth Trends",
                            imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "GDP Calculation", "Monetary Policy Committee")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-science-tech",
            appId = "appsc-group2",
            name = "Science and Technology",
            category = "mains",
            icon = "Atom",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80",
            color = "from-cyan-600 to-blue-800",
            units = listOf(
                UnitModel(
                    id = "unit-st-1",
                    appId = "appsc-group2",
                    name = "Unit 1: Space & Defence Technology",
                    description = "ISRO launch vehicles, DRDO missile development & Nuclear Tech",
                    imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
                    topics = listOf(
                        TopicModel(
                            id = "topic-st-1-1",
                            appId = "appsc-group2",
                            name = "ISRO & Space Exploration",
                            imageUrl = "https://images.unsplash.com/photo-1517976487515-5645511b8a59?auto=format&fit=crop&w=1200&q=80",
                            subtopics = listOf("General Practice", "Chandrayaan-3 & LVM3", "NavIC System")
                        )
                    )
                )
            )
        ),

        // SSC CGL App Subjects
        Subject(
            id = "subj-ssc-quant",
            appId = "ssc-cgl",
            name = "Quantitative Aptitude",
            category = "prelims",
            icon = "Calculate",
            imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&w=1200&q=80",
            color = "from-blue-600 to-indigo-800",
            units = listOf(
                UnitModel(
                    id = "unit-ssc-arithmetic",
                    appId = "ssc-cgl",
                    name = "Unit 1: Arithmetic & Commercial Maths",
                    description = "Percentages, Profit & Loss, SI/CI, Time & Work",
                    topics = listOf(
                        TopicModel(
                            id = "topic-ssc-percentages",
                            appId = "ssc-cgl",
                            name = "Percentages & Ratio Proportion",
                            subtopics = listOf("General Practice", "Successive Percentage", "Ratio Applications")
                        )
                    )
                )
            )
        ),
        Subject(
            id = "subj-ssc-reasoning",
            appId = "ssc-cgl",
            name = "General Intelligence & Reasoning",
            category = "prelims",
            icon = "Psychology",
            imageUrl = "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=1200&q=80",
            color = "from-purple-600 to-indigo-900",
            units = listOf(
                UnitModel(
                    id = "unit-ssc-logical",
                    appId = "ssc-cgl",
                    name = "Unit 1: Non-Verbal & Verbal Reasoning",
                    topics = listOf(
                        TopicModel(
                            id = "topic-ssc-syllogisms",
                            appId = "ssc-cgl",
                            name = "Syllogisms & Venn Diagrams",
                            subtopics = listOf("General Practice", "Statement & Conclusions")
                        )
                    )
                )
            )
        ),

        // Banking IBPS App Subjects
        Subject(
            id = "subj-bank-banking-awareness",
            appId = "banking-ibps",
            name = "Banking & Financial Awareness",
            category = "prelims",
            icon = "AccountBalance",
            imageUrl = "https://images.unsplash.com/photo-1565514020179-026b92b84bb6?auto=format&fit=crop&w=1200&q=80",
            color = "from-teal-600 to-emerald-800",
            units = listOf(
                UnitModel(
                    id = "unit-bank-rbi",
                    appId = "banking-ibps",
                    name = "Unit 1: RBI Functions & Monetary Policy",
                    description = "Repo Rate, Reverse Repo, CRR, SLR & Bank Types",
                    topics = listOf(
                        TopicModel(
                            id = "topic-bank-monetary",
                            appId = "banking-ibps",
                            name = "Monetary Policy & RBI Rates",
                            subtopics = listOf("General Practice", "Liquidity Adjustment Facility")
                        )
                    )
                )
            )
        )
    )

    val subjectsState = mutableStateListOf<Subject>().apply {
        addAll(initialSubjectsList)
    }

    // Filtered by active App ID
    val defaultSubjects: List<Subject>
        get() = subjectsState.filter { it.appId == currentActiveAppId.value }

    fun getSubjectsForApp(appId: String): List<Subject> {
        return subjectsState.filter { it.appId == appId }
    }

    // =========================================================================
    // 4. QUESTIONS & MCQS (MULTI-APP TENANTS)
    // =========================================================================

    val sampleQuestions = listOf(
        Question(
            id = "q-ivc-great-bath",
            appId = "appsc-group2",
            subjectId = "subj-indian-history",
            unitId = "unit-ih-ancient",
            topicId = "topic-ih-ancient-civ",
            subtopic = "General Practice",
            questionText = "The famous 'Great Bath' of the Indus Valley Civilization was discovered at which archaeological site?",
            options = mapOf(
                "A" to "Harappa",
                "B" to "Mohenjo-daro",
                "C" to "Kalibangan",
                "D" to "Lothal"
            ),
            correctOption = "B",
            explanation = "The Great Bath is one of the best-known structures among the ruins of the ancient Indus Valley Civilization at Mohenjo-daro in Sindh, Pakistan."
        ),
        Question(
            id = "q-lothal-dockyard",
            appId = "appsc-group2",
            subjectId = "subj-indian-history",
            unitId = "unit-ih-ancient",
            topicId = "topic-ih-ancient-civ",
            subtopic = "Harappan Sites, Town Planning & Economy",
            questionText = "Which Indus Valley site has yielded evidence of a tidal dockyard connected to the Gulf of Khambhat?",
            options = mapOf(
                "A" to "Dholavira",
                "B" to "Surkotada",
                "C" to "Lothal",
                "D" to "Banawali"
            ),
            correctOption = "C",
            explanation = "Lothal in Gujarat had a world-famous rectangular dockyard built of burnt bricks with an inlet channel connecting to the Sabarmati river."
        ),
        Question(
            id = "q-vedic-gayatri",
            appId = "appsc-group2",
            subjectId = "subj-indian-history",
            unitId = "unit-ih-ancient",
            topicId = "topic-ih-ancient-civ",
            subtopic = "Vedic Period & Literature",
            questionText = "The sacred 'Gayatri Mantra' is dedicated to which Vedic deity and found in which Mandala of Rigveda?",
            options = mapOf(
                "A" to "Savitr, 3rd Mandala",
                "B" to "Agni, 1st Mandala",
                "C" to "Indra, 2nd Mandala",
                "D" to "Soma, 9th Mandala"
            ),
            correctOption = "A",
            explanation = "The Gayatri Mantra was composed by Sage Vishvamitra, dedicated to solar deity Savitr, and is located in the 3rd Mandala of the Rigveda."
        ),
        Question(
            id = "q-earth-core",
            appId = "appsc-group2",
            subjectId = "subj-geography",
            unitId = "unit-geo-general-physical",
            topicId = "topic-geo-solar-earth",
            subtopic = "Crust, Mantle, Core & Earthquakes",
            questionText = "The discontinuity that separates Earth's Mantle from the Outer Core is known as what?",
            options = mapOf(
                "A" to "Mohorovicic Discontinuity",
                "B" to "Gutenberg Discontinuity",
                "C" to "Conrad Discontinuity",
                "D" to "Lehmann Discontinuity"
            ),
            correctOption = "B",
            explanation = "• Magma is primarily sourced from the Asthenosphere in the upper mantle.\n• Partial melting of rocks in this zone creates magma.\n• This magma rises through the lithosphere to erupt as lava.\n• 💡 Why not the others:\n– Lower Mantle = deeper, more rigid mantle zone, not the magma source\n– Inner Core = solid iron zone, not where magma forms\n– Outer Core = liquid metal layer, unrelated to volcanic magma source"
        ),
        Question(
            id = "q-kinship-rules",
            appId = "appsc-group2",
            subjectId = "subj-society",
            unitId = "unit-soc-1",
            topicId = "topic-soc-1-1",
            subtopic = "Kinship Rules",
            questionText = "In Indian sociological terms, a social group whose members trace descent from a common ancestor through unilineal descent is called?",
            options = mapOf(
                "A" to "Gotra / Clan",
                "B" to "Varna",
                "C" to "Jati",
                "D" to "Ashrama"
            ),
            correctOption = "A",
            explanation = "A Clan or Gotra is an exogamous kinship group of people claiming descent from a common ancestor."
        ),
        Question(
            id = "q-satavahana-gatha",
            appId = "appsc-group2",
            subjectId = "subj-ap-history",
            unitId = "unit-aph-1",
            topicId = "topic-aph-1-1",
            subtopic = "General Practice",
            questionText = "Which Satavahana ruler authored the famous Prakrit anthology of poems titled 'Gatha Saptasati'?",
            options = mapOf(
                "A" to "Simuka",
                "B" to "Satakarni I",
                "C" to "Hala",
                "D" to "Gautamiputra Satakarni"
            ),
            correctOption = "C",
            explanation = "King Hala, the 17th Satavahana ruler, composed 'Gatha Saptasati' in Maharashtri Prakrit."
        ),
        Question(
            id = "q-kesavananda-1",
            appId = "appsc-group2",
            subjectId = "subj-constitution",
            unitId = "unit-con-1",
            topicId = "topic-con-1-1",
            subtopic = "General Practice",
            questionText = "In which landmark verdict did the Supreme Court propound the 'Basic Structure Doctrine' of the Constitution?",
            options = mapOf(
                "A" to "Golaknath v. State of Punjab (1967)",
                "B" to "Kesavananda Bharati v. State of Kerala (1973)",
                "C" to "Minerva Mills v. Union of India (1980)",
                "D" to "Maneka Gandhi v. Union of India (1978)"
            ),
            correctOption = "B",
            explanation = "On April 24, 1973, a 13-judge bench ruled in Kesavananda Bharati case establishing the Basic Structure Doctrine."
        ),
        Question(
            id = "q-chandrayaan-3",
            appId = "appsc-group2",
            subjectId = "subj-science-tech",
            unitId = "unit-st-1",
            topicId = "topic-st-1-1",
            subtopic = "General Practice",
            questionText = "From which space launch vehicle did ISRO successfully launch Chandrayaan-3 on July 14, 2023?",
            options = mapOf(
                "A" to "PSLV-C56",
                "B" to "LVM3-M4",
                "C" to "GSLV-F12",
                "D" to "SSLV-D2"
            ),
            correctOption = "B",
            explanation = "ISRO launched Chandrayaan-3 using the LVM3-M4 launch vehicle from Sriharikota."
        ),
        // SSC Question
        Question(
            id = "q-ssc-profit-1",
            appId = "ssc-cgl",
            subjectId = "subj-ssc-quant",
            unitId = "unit-ssc-arithmetic",
            topicId = "topic-ssc-percentages",
            subtopic = "Successive Percentage",
            questionText = "If the price of petrol increases by 25%, by what percentage must a person reduce consumption so that expenditure remains unchanged?",
            options = mapOf(
                "A" to "20%",
                "B" to "25%",
                "C" to "16.66%",
                "D" to "30%"
            ),
            correctOption = "A",
            explanation = "Formula: [r / (100 + r)] * 100 = [25 / 125] * 100 = 20% reduction."
        ),
        // Math Compound Interest Question (Matching Reference Image 2)
        Question(
            id = "q-ssc-math-ci-1",
            appId = "ssc-cgl",
            subjectId = "subj-ssc-quant",
            unitId = "unit-ssc-arithmetic",
            topicId = "topic-ssc-interest",
            subtopic = "Compound Interest",
            questionText = "At what annual rate of compound interest will a principal of ₹10,000 amount to ₹12,100 in 2 years?",
            options = mapOf(
                "A" to "8%",
                "B" to "10%",
                "C" to "12%",
                "D" to "15%"
            ),
            correctOption = "B",
            explanation = "A/P = (11/10)² → r = 10%, n = 2 yr\n\nP = ₹10,000, A = ₹12,100, n = 2.\nFormula: A = P(1 + r/100)¹\n(1 + r/100)² = 12100 / 10000 = (11/10)²\nComparing: 1 + r/100 = 11/10, so r = 10.\n∴ Rate = 10% p.a.\nCheck: ₹10,000 × (11/10)² = ₹12,100 ✓"
        ),
        // Banking Question
        Question(
            id = "q-bank-repo-1",
            appId = "banking-ibps",
            subjectId = "subj-bank-banking-awareness",
            unitId = "unit-bank-rbi",
            topicId = "topic-bank-monetary",
            subtopic = "General Practice",
            questionText = "What rate does the Reserve Bank of India charge commercial banks for short-term collateralized borrowing?",
            options = mapOf(
                "A" to "Reverse Repo Rate",
                "B" to "Repo Rate",
                "C" to "Bank Rate",
                "D" to "MSF Rate"
            ),
            correctOption = "B",
            explanation = "Repo Rate (Repurchase Option) is the rate at which RBI lends short-term money to commercial banks against government securities."
        )
    )

    val questionsState = mutableStateListOf<Question>().apply {
        addAll(sampleQuestions)
    }

    val importHistoryState = mutableStateListOf<ImportBatchRecord>()

    fun getQuestionsCountForSubject(subjectId: String): Int {
        return questionsState.count { it.subjectId == subjectId }
    }

    fun getQuestionsCountForUnit(subjectId: String, unitId: String): Int {
        return questionsState.count { it.subjectId == subjectId && it.unitId == unitId }
    }

    fun getQuestionsCountForTopic(subjectId: String, unitId: String, topicId: String): Int {
        return questionsState.count { it.subjectId == subjectId && it.unitId == unitId && it.topicId == topicId }
    }

    fun getQuestionsForPractice(subjectId: String, unitId: String, topicId: String, subtopic: String): List<Question> {
        val exact = questionsState.filter {
            it.subjectId == subjectId && it.unitId == unitId && it.topicId == topicId && (subtopic.isBlank() || it.subtopic.equals(subtopic, true))
        }
        if (exact.isNotEmpty()) return exact
        val topicLevel = questionsState.filter { it.subjectId == subjectId && it.topicId == topicId }
        if (topicLevel.isNotEmpty()) return topicLevel
        val unitLevel = questionsState.filter { it.subjectId == subjectId && it.unitId == unitId }
        if (unitLevel.isNotEmpty()) return unitLevel
        val subjectLevel = questionsState.filter { it.subjectId == subjectId }
        if (subjectLevel.isNotEmpty()) return subjectLevel
        return questionsState.take(5)
    }

    fun getSetsForHierarchyLevel(subjectId: String, unitId: String = "", topicId: String = ""): List<PracticeSetModel> {
        val filtered = questionsState.filter {
            it.subjectId == subjectId && it.unitId == unitId && it.topicId == topicId
        }
        if (filtered.isEmpty()) return emptyList()

        val grouped = filtered.groupBy { it.setName.takeIf { name -> name.isNotBlank() } ?: "Imported Set" }

        var setNum = 1
        return grouped.map { (name, qs) ->
            PracticeSetModel(
                id = "set-${java.util.UUID.randomUUID()}",
                setNumber = setNum++,
                title = name,
                subtitle = "${qs.size} Qs",
                questionsCount = qs.size,
                startQuestionNumber = 1,
                endQuestionNumber = qs.size,
                status = "Not Started",
                isCompleted = false,
                durationMinutes = qs.size
            )
        }
    }

    fun getPracticeSetsForTopic(subjectId: String, unit: UnitModel, topic: TopicModel): List<PracticeSetModel> {
        val topicSets = getSetsForHierarchyLevel(subjectId, unit.id, topic.id)
        if (topicSets.isNotEmpty()) {
            return topicSets
        }

        val topicQuestions = questionsState.filter { 
            it.subjectId == subjectId && (it.topicId == topic.id || it.unitId == unit.id) 
        }.ifEmpty {
            questionsState.filter { it.subjectId == subjectId }.ifEmpty { questionsState }
        }

        val totalQuestionsCount = if (topicQuestions.size >= 62) topicQuestions.size else 62
        val setConfigs = listOf(
            Triple(1, "10 Qs • Q1 – Q10", 10),
            Triple(2, "10 Qs • Q11 – Q20", 10),
            Triple(3, "10 Qs • Q21 – Q30", 10),
            Triple(4, "10 Qs • Q31 – Q40", 10),
            Triple(5, "10 Qs • Q41 – Q50", 10),
            Triple(6, "10 Qs • Q51 – Q60", 10),
            Triple(7, "2 Qs • Q61 – Q62", 2)
        )

        var currentStartQ = 1
        return setConfigs.mapIndexed { index, (setNum, subtitle, qCount) ->
            val startNum = currentStartQ
            val endNum = currentStartQ + qCount - 1
            currentStartQ += qCount

            // Grab or synthesize questions for this set
            val slice = (0 until qCount).map { qIdx ->
                val globalIdx = (index * 10) + qIdx
                val existing = topicQuestions.getOrNull(globalIdx % topicQuestions.size)
                if (existing != null) {
                    existing.copy(
                        id = "q-${topic.id}-$setNum-${qIdx + 1}",
                        subtopic = topic.subtopics.getOrNull(qIdx % (topic.subtopics.size.coerceAtLeast(1))) ?: "General Practice"
                    )
                } else {
                    Question(
                        id = "q-${topic.id}-$setNum-${qIdx + 1}",
                        appId = topic.appId,
                        subjectId = subjectId,
                        unitId = unit.id,
                        topicId = topic.id,
                        subtopic = topic.subtopics.getOrNull(qIdx % (topic.subtopics.size.coerceAtLeast(1))) ?: "Core Concepts",
                        questionText = "ముదలియార్ కమిషన్/APPSC ప్రామాణిక అంశాలకు సంబంధించి (${topic.name}) క్రింది వాటిలో సరైనది ఏది?",
                        options = mapOf(
                            "A" to "1950",
                            "B" to "1956",
                            "C" to "1948",
                            "D" to "1952"
                        ),
                        correctOption = "D",
                        explanation = "సరైన సమాధానం: 1952. ఈ అంశం ముదలియార్ కమిషన్ (1952–53) యొక్క మాధ్యమిక విద్యా సంస్కరణలతో సంబంధం కలిగి ఉంది."
                    )
                }
            }

            val status = when (setNum) {
                1 -> "Completed"
                else -> "Not Started"
            }

            PracticeSetModel(
                id = "set-${topic.id}-$setNum",
                setNumber = setNum,
                title = "Set $setNum",
                subtitle = subtitle,
                questionsCount = qCount,
                startQuestionNumber = startNum,
                endQuestionNumber = endNum,
                status = status,
                isCompleted = (status == "Completed"),
                durationMinutes = if (qCount > 5) 10 else 5,
                totalMarks = qCount,
                negativeMarks = 0.33,
                difficulty = "Medium",
                badge = when (setNum) {
                    1 -> "Foundation"
                    2 -> "Concept Booster"
                    3 -> "PYQs Special"
                    4 -> "Speed & Precision"
                    5 -> "Analytical Booster"
                    6 -> "Revision Set"
                    else -> "Final Polish"
                },
                bestScore = if (setNum == 1) 10 else null,
                questions = slice
            )
        }
    }

    // =========================================================================
    // 5. CURRENT AFFAIRS & MULTI-APP SHARING
    // =========================================================================

    private val initialCurrentAffairs = listOf(
        CurrentAffair(
            id = "ca-1",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "appsc-group3", "appsc-group4"),
            month = "September 2026",
            category = "AP Current Affairs",
            title = "ఆంధ్రప్రదేశ్ కొత్త సమగ్ర ఆర్థిక మరియు పారిశ్రామిక పాలసీ 2026",
            content = "- నవ్యాంధ్ర పారిశ్రామిక అభివృద్ధి లక్ష్యంగా రాష్ట్ర మంత్రివర్గం ఆమోదం తెలిపింది.\n- గ్రీన్ ఎనర్జీ, ఐటీ హబ్‌లు మరియు ఎలక్ట్రానిక్స్ రంగాలకు ₹25,000 కోట్ల రాయితీలు కేటాయింపు.\n- విశాఖపట్నం, అమరావతి మరియు తిరుపతి కారిడార్లలో 50,000 ఉద్యోగాల సృష్టి లక్ష్యం.",
            publishedDate = "2026-09-10",
            status = "PUBLISHED",
            isSharedMultiApp = true
        ),
        CurrentAffair(
            id = "ca-2",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "ssc-cgl", "banking-ibps", "upsc-cse"),
            month = "September 2026",
            category = "National & Economy",
            title = "భారత జిడిపి వృద్ధి రేటు 7.2% నమోదు - NITI ఆయోగ్ తాజా నివేదిక",
            content = "- ప్రస్తుత ఆర్థిక సంవత్సరంలో భారతదేశం ప్రపంచంలోనే అత్యంత వేగంగా అభివృద్ధి చెందుతున్న ప్రధాన ఆర్థిక వ్యవస్థగా నిలిచింది.\n- తయారీ రంగం మరియు సేవా రంగాల్లో బలమైన పురోగతి.\n- డిజిటల్ చెల్లింపుల పరిమాణం నెలకు 15 బిలియన్ లావాదేవీలను అధిగమించింది.",
            publishedDate = "2026-09-08",
            status = "PUBLISHED",
            isSharedMultiApp = true
        ),
        CurrentAffair(
            id = "ca-3",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "ap-dsc"),
            month = "September 2026",
            category = "Government Schemes",
            title = "ఆంధ్రప్రదేశ్ విద్యా రంగానికి ప్రత్యేక కేటాయింపులు",
            content = "- పాఠశాలల్లో డిజిటల్ క్లాస్‌రూమ్‌లు మరియు ఉపాధ్యాయ శిక్షణ కోసం ప్రత్యేక గ్రాంట్లు విడుదల.\n- విద్యా దీవెన మరియు వసతి దీవెన నిధుల జమ.",
            publishedDate = "2026-09-05",
            status = "PUBLISHED",
            isSharedMultiApp = true
        ),
        CurrentAffair(
            id = "ca-4",
            appId = "ssc-cgl",
            mappedAppIds = listOf("ssc-cgl", "banking-ibps", "railway-rrb"),
            month = "September 2026",
            category = "Science & Technology",
            title = "ISRO Gaganyaan Mission Orbital Module Validation Success",
            content = "- Indian Space Research Organisation completed high-altitude flight safety abort test.\n- Vyommitra humanoid robot system tests passed critical benchmarks at Sriharikota.",
            publishedDate = "2026-09-04",
            status = "PUBLISHED",
            isSharedMultiApp = true
        )
    )

    val currentAffairsState = mutableStateListOf<CurrentAffair>().apply {
        addAll(initialCurrentAffairs)
    }

    val sampleCurrentAffairs: List<CurrentAffair>
        get() = currentAffairsState.filter { it.mappedAppIds.contains(currentActiveAppId.value) }

    fun getCurrentAffairsForApp(appId: String): List<CurrentAffair> {
        return currentAffairsState.filter { it.mappedAppIds.contains(appId) }
    }

    // Super Admin / Content Admin: Add or Publish Current Affairs with Multi-App Mapping
    fun publishCurrentAffairToApps(ca: CurrentAffair, targetAppIds: List<String>) {
        val idx = currentAffairsState.indexOfFirst { it.id == ca.id }
        val updated = ca.copy(
            mappedAppIds = targetAppIds,
            isSharedMultiApp = targetAppIds.size > 1
        )
        if (idx != -1) {
            currentAffairsState[idx] = updated
        } else {
            currentAffairsState.add(0, updated)
        }

        addAuditLog(
            actionType = if (targetAppIds.size > 1) "CA_PUBLISHED_MULTI_APP" else "CA_PUBLISHED",
            details = "Published Current Affair '${ca.title.take(30)}...' to ${targetAppIds.size} apps (${targetAppIds.joinToString(", ")})",
            category = "Current Affairs",
            appId = ca.appId,
            appName = appsListState.find { it.id == ca.appId }?.name ?: "App",
            recordCount = targetAppIds.size
        )
    }

    fun deleteCurrentAffair(caId: String) {
        val ca = currentAffairsState.find { it.id == caId }
        if (ca != null) {
            currentAffairsState.removeAll { it.id == caId }
            addAuditLog(
                actionType = "CA_DELETED",
                details = "Deleted Current Affair '${ca.title.take(30)}...'",
                category = "Current Affairs",
                appId = ca.appId
            )
        }
    }

    // =========================================================================
    // 6. MONTHLY CURRENT AFFAIRS PDFS & MULTI-APP SHARING
    // =========================================================================

    private val initialMonthlyPdfs = listOf(
        MonthlyCurrentAffairsPdf(
            id = "pdf-sep-2026",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "appsc-group3", "appsc-group4"),
            month = "September",
            year = "2026",
            title = "September 2026 AP & National Current Affairs Digest",
            pdfUrl = "https://example.com/digest/september-2026-current-affairs.pdf",
            fileSizeBytes = "5.4 MB",
            pagesCount = 52,
            language = "Telugu & English",
            description = "Complete monthly compilation of Andhra Pradesh state schemes, policies, appointments, sports & national events.",
            coverImageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
            isProOnly = false,
            publishDate = "2026-09-01",
            status = "PUBLISHED",
            downloadCount = 1420,
            isSharedMultiApp = true
        ),
        MonthlyCurrentAffairsPdf(
            id = "pdf-aug-2026",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "ssc-cgl", "banking-ibps"),
            month = "August",
            year = "2026",
            title = "August 2026 Comprehensive Monthly Round-Up",
            pdfUrl = "https://example.com/digest/august-2026-current-affairs.pdf",
            fileSizeBytes = "4.8 MB",
            pagesCount = 48,
            language = "Telugu & English",
            description = "August monthly recap: Independence Day announcements, economic indices, space missions and state budget updates.",
            coverImageUrl = "https://images.unsplash.com/photo-1506784983877-45594efa4cbe?auto=format&fit=crop&w=800&q=80",
            isProOnly = false,
            publishDate = "2026-08-01",
            status = "PUBLISHED",
            downloadCount = 2890,
            isSharedMultiApp = true
        ),
        MonthlyCurrentAffairsPdf(
            id = "pdf-jul-2026",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1"),
            month = "July",
            year = "2026",
            title = "July 2026 State & Central Government Digest",
            pdfUrl = "https://example.com/digest/july-2026-current-affairs.pdf",
            fileSizeBytes = "4.1 MB",
            pagesCount = 44,
            language = "Telugu & English",
            description = "July summary covering monsoon reports, environmental summits and AP infrastructure projects.",
            coverImageUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=800&q=80",
            isProOnly = true,
            publishDate = "2026-07-01",
            status = "PUBLISHED",
            downloadCount = 3120,
            isSharedMultiApp = true
        )
    )

    val monthlyPdfsState = mutableStateListOf<MonthlyCurrentAffairsPdf>().apply {
        addAll(initialMonthlyPdfs)
    }

    fun getPdfsForApp(appId: String): List<MonthlyCurrentAffairsPdf> {
        return monthlyPdfsState.filter { it.mappedAppIds.contains(appId) }
    }

    fun publishPdfToApps(pdf: MonthlyCurrentAffairsPdf, targetAppIds: List<String>) {
        val idx = monthlyPdfsState.indexOfFirst { it.id == pdf.id }
        val updated = pdf.copy(
            mappedAppIds = targetAppIds,
            isSharedMultiApp = targetAppIds.size > 1
        )
        if (idx != -1) {
            monthlyPdfsState[idx] = updated
        } else {
            monthlyPdfsState.add(0, updated)
        }

        addAuditLog(
            actionType = if (targetAppIds.size > 1) "PDF_PUBLISHED_MULTI_APP" else "PDF_PUBLISHED",
            details = "Published Monthly PDF '${pdf.title}' to ${targetAppIds.size} apps (${targetAppIds.joinToString(", ")})",
            category = "Monthly PDFs",
            appId = pdf.appId,
            appName = appsListState.find { it.id == pdf.appId }?.name ?: "App",
            recordCount = targetAppIds.size
        )
    }

    fun deleteMonthlyPdf(pdfId: String) {
        val pdf = monthlyPdfsState.find { it.id == pdfId }
        if (pdf != null) {
            monthlyPdfsState.removeAll { it.id == pdfId }
            addAuditLog(
                actionType = "PDF_DELETED",
                details = "Deleted Monthly PDF '${pdf.title}'",
                category = "Monthly PDFs",
                appId = pdf.appId
            )
        }
    }

    // =========================================================================
    // 7. PROMOTIONAL POSTERS & MULTI-APP SHARING
    // =========================================================================

    private val initialPostersList = listOf(
        PromotionalPosterModel(
            id = "post-1",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "appsc-group3", "appsc-group4"),
            posterType = "Festival Poster",
            title = "✨ Vinayaka Chavithi & Diwali Super Dhamaka Offer",
            imageUrl = "https://images.unsplash.com/photo-1577083552431-6e5fd01aa342?auto=format&fit=crop&w=1200&q=80",
            promotionalText = "FLAT 50% OFF • ALL 1-YEAR UNLIMITED PRO PASSES",
            ctaText = "CLAIM FESTIVAL PASS",
            offerDetails = "Use Coupon Code DIWALI50. Includes All Subjects, 15,000+ MCQs & Grand Mock Tests.",
            destinationPage = "subscription",
            targetPages = listOf("Home Screen", "Subscription Screen"),
            targetPlans = listOf("Ranker Plan", "Achiever Plan"),
            targetUserTypes = listOf("Free Tier Users", "All Users"),
            startDate = "2026-09-01",
            endDate = "2026-11-20",
            priorityOrder = 1,
            isActive = true,
            isSharedMultiApp = true
        ),
        PromotionalPosterModel(
            id = "post-2",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1"),
            posterType = "Exam Update",
            title = "🏹 Dussehra Vijayadashami Victory Batch",
            imageUrl = "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?auto=format&fit=crop&w=1200&q=80",
            promotionalText = "CONQUER APPSC GROUP 2 • VICTORY MOCK TEST SERIES",
            ctaText = "ATTEMPT MOCK TESTS",
            offerDetails = "10 Full-Length Simulation Tests strictly on latest APPSC Pattern with State Rank Predictor.",
            destinationPage = "mock_tests",
            targetPages = listOf("Home Screen", "Mock Tests"),
            targetPlans = listOf("All Plans"),
            targetUserTypes = listOf("All Users"),
            startDate = "2026-09-01",
            endDate = "2026-10-31",
            priorityOrder = 2,
            isActive = true,
            isSharedMultiApp = true
        ),
        PromotionalPosterModel(
            id = "post-3",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2"),
            posterType = "Study Material Banner",
            title = "🐘 AP History Special: Satavahanas & Freedom Movement",
            imageUrl = "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?auto=format&fit=crop&w=1200&q=80",
            promotionalText = "MASTER ANDHRA PRADESH HISTORY • HIGH YIELD MCQS",
            ctaText = "PRACTICE AP HISTORY",
            offerDetails = "Instant access to AP History units: Satavahanas, Ikshvakus, Vijayanagara & Freedom Struggle.",
            destinationPage = "subj-ap-history",
            targetPages = listOf("Home Screen", "Syllabus Explorer"),
            targetPlans = listOf("Scholar Plan", "All Plans"),
            targetUserTypes = listOf("Free Tier Users", "All Users"),
            startDate = "2026-09-01",
            endDate = "2026-10-15",
            priorityOrder = 3,
            isActive = true,
            isSharedMultiApp = false
        ),
        PromotionalPosterModel(
            id = "post-4",
            appId = "appsc-group2",
            mappedAppIds = listOf("appsc-group2", "appsc-group1", "ssc-cgl", "banking-ibps", "ap-dsc", "upsc-cse"),
            posterType = "Current Affairs Poster",
            title = "📰 2026 AP & National Current Affairs Capsule",
            imageUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?auto=format&fit=crop&w=1200&q=80",
            promotionalText = "AP SCHEMES, BUDGET 2026 & MONTHLY ROUNDUPS",
            ctaText = "READ CURRENT AFFAIRS",
            offerDetails = "Daily Telugu & English updates, Government Policies & AP Socio-Economic Survey.",
            destinationPage = "current_affairs",
            targetPages = listOf("Home Screen"),
            targetPlans = listOf("All Plans"),
            targetUserTypes = listOf("All Users"),
            startDate = "2026-09-01",
            endDate = "2026-12-31",
            priorityOrder = 4,
            isActive = true,
            isSharedMultiApp = true
        )
    )

    val postersState = mutableStateListOf<PromotionalPosterModel>().apply {
        addAll(initialPostersList)
    }

    fun getPostersForApp(appId: String): List<PromotionalPosterModel> {
        return postersState.filter { it.mappedAppIds.contains(appId) }
    }

    fun publishPosterToApps(poster: PromotionalPosterModel, targetAppIds: List<String>) {
        val idx = postersState.indexOfFirst { it.id == poster.id }
        val updated = poster.copy(
            mappedAppIds = targetAppIds,
            isSharedMultiApp = targetAppIds.size > 1
        )
        if (idx != -1) {
            postersState[idx] = updated
        } else {
            postersState.add(0, updated)
        }

        addAuditLog(
            actionType = if (targetAppIds.size > 1) "POSTER_PUBLISHED_MULTI_APP" else "POSTER_PUBLISHED",
            details = "Published Poster '${poster.title}' to ${targetAppIds.size} apps (${targetAppIds.joinToString(", ")})",
            category = "Posters",
            appId = poster.appId,
            appName = appsListState.find { it.id == poster.appId }?.name ?: "App",
            recordCount = targetAppIds.size
        )
    }

    fun deletePoster(posterId: String) {
        val poster = postersState.find { it.id == posterId }
        if (poster != null) {
            postersState.removeAll { it.id == posterId }
            addAuditLog(
                actionType = "POSTER_DELETED",
                details = "Deleted Poster '${poster.title}'",
                category = "Posters",
                appId = poster.appId
            )
        }
    }

    // =========================================================================
    // 8. SUBSCRIPTION PLANS & USER ACCOUNTS (MULTI-APP CURRENCY)
    // =========================================================================

    private val initialSubscriptionPlans = listOf(
        SubscriptionPlanModel(
            id = "sub-1",
            appId = "appsc-group2",
            name = "Basic Starter Pass",
            price = 199.0,
            promotionalPrice = 149.0,
            currencySymbol = "₹",
            duration = "Monthly",
            durationDays = 30,
            features = listOf("₹149/month Promo Rate", "Unlimited MCQ Practice", "Basic Subject Explanations", "Daily Current Affairs"),
            benefits = "Perfect for quick monthly revision.",
            introductoryOfferText = "Introductory ₹50 Off for new registrants",
            linkedCouponCodes = listOf("CHAVITHI100"),
            visibleToUserGroups = listOf("Free Users", "New Users"),
            isPopular = false,
            isActive = true,
            activeSubscribersCount = 340,
            renewalsCount = 120,
            cancellationsCount = 12,
            totalRevenueGenerated = 50660.0
        ),
        SubscriptionPlanModel(
            id = "sub-2",
            appId = "appsc-group2",
            name = "Scholar Quarter Pass",
            price = 499.0,
            promotionalPrice = 399.0,
            currencySymbol = "₹",
            duration = "Quarterly",
            durationDays = 90,
            features = listOf("₹133/month (Save 33%)", "Unlimited MCQ & PYQ Practice", "Sectional Mock Tests", "Daily Current Affairs Bulletins"),
            benefits = "3 Months systematic coverage for Prelims screening.",
            introductoryOfferText = "Save 20% Extra this month",
            linkedCouponCodes = listOf("UGADI2026"),
            visibleToUserGroups = listOf("All Users"),
            isPopular = false,
            isActive = true,
            activeSubscribersCount = 510,
            renewalsCount = 230,
            cancellationsCount = 8,
            totalRevenueGenerated = 203490.0
        ),
        SubscriptionPlanModel(
            id = "sub-3",
            appId = "appsc-group2",
            name = "Achiever Half-Yearly Pass",
            price = 799.0,
            promotionalPrice = 599.0,
            currencySymbol = "₹",
            duration = "Half-Yearly",
            durationDays = 180,
            features = listOf("₹100/month (Save 50%)", "Unlimited MCQs & PYQs", "Grand All-India Rank Mock Tests", "Current Affairs PDF Downloads", "Topicwise Study Notes & Mindmaps"),
            benefits = "Comprehensive 6-month preparation for Prelims + Mains.",
            introductoryOfferText = "Bestseller Offer: Includes Free Current Affairs PDF Pack",
            linkedCouponCodes = listOf("DIWALI50", "SANKRANTI30"),
            visibleToUserGroups = listOf("All Users"),
            isPopular = true,
            isActive = true,
            activeSubscribersCount = 1250,
            renewalsCount = 640,
            cancellationsCount = 15,
            totalRevenueGenerated = 748750.0
        ),
        SubscriptionPlanModel(
            id = "sub-4",
            appId = "appsc-group2",
            name = "Ranker 1-Year Pass",
            price = 999.0,
            promotionalPrice = 799.0,
            currencySymbol = "₹",
            duration = "Yearly",
            durationDays = 365,
            features = listOf("₹66/month (Best Value - Save 66%)", "365 Days All-Access VIP Pass", "Unlimited Mocks, PYQs & Grand Tests", "AI Personal Performance Analytics", "Priority Mentor Doubt Resolution"),
            benefits = "Ultimate 1-Year Pass covering entire APPSC syllabus.",
            introductoryOfferText = "VIP Access: Includes Personal Analytics Dashboard",
            linkedCouponCodes = listOf("DIWALI50", "FREEDOM79"),
            visibleToUserGroups = listOf("All Users"),
            isPopular = false,
            isActive = true,
            activeSubscribersCount = 890,
            renewalsCount = 410,
            cancellationsCount = 5,
            totalRevenueGenerated = 711110.0
        )
    )

    val subscriptionPlansState = mutableStateListOf<SubscriptionPlanModel>().apply {
        addAll(initialSubscriptionPlans)
    }

    fun getSubscriptionPlansForApp(appId: String): List<SubscriptionPlanModel> {
        val specific = subscriptionPlansState.filter { it.appId == appId }
        if (specific.isNotEmpty()) return specific
        val activeApp = appsListState.find { it.id == appId }
        val sym = activeApp?.currencySymbol ?: "₹"
        return subscriptionPlansState.map { it.copy(currencySymbol = sym) }
    }

    private fun addDefaultSubscriptionPlansForApp(appId: String, currencySymbol: String) {
        subscriptionPlansState.add(
            SubscriptionPlanModel(
                id = "sub-${appId}-starter",
                appId = appId,
                name = "Starter Monthly Pass",
                price = 199.0,
                promotionalPrice = 149.0,
                currencySymbol = currencySymbol,
                duration = "Monthly",
                durationDays = 30,
                features = listOf("Unlimited MCQs", "Topic Practice", "Daily Current Affairs"),
                isPopular = false,
                isActive = true
            )
        )
        subscriptionPlansState.add(
            SubscriptionPlanModel(
                id = "sub-${appId}-annual",
                appId = appId,
                name = "Ranker 1-Year Pass",
                price = 999.0,
                promotionalPrice = 799.0,
                currencySymbol = currencySymbol,
                duration = "Yearly",
                durationDays = 365,
                features = listOf("365 Days All Access", "Mock Tests", "Monthly PDFs", "Performance Analytics"),
                isPopular = true,
                isActive = true
            )
        )
    }

    val couponsState = mutableStateListOf<CouponModel>(
        CouponModel(
            id = "coup-1",
            code = "DIWALI50",
            description = "Festival Bumper Offer: 50% Flat Off on Achiever & Ranker Plans",
            discountType = CouponDiscountType.PERCENTAGE,
            discountValue = 50.0,
            minPurchaseAmount = 499.0,
            startDate = "2026-09-01",
            expiryDate = "2026-11-15",
            isActive = true
        )
    )

    val usersListState = mutableStateListOf<UserAccountRecord>(
        UserAccountRecord("usr-1", "Srinivas Rao", "srinivas.appsc2026@gmail.com", "+91 98765 43210", "APPSC Group 2", "appsc-group2", true, "Achiever Half-Yearly Pass", "2027-03-09", "System (Auto)"),
        UserAccountRecord("usr-2", "Sobha Dhanaraju", "sobhadhanaraju0@gmail.com", "+91 94400 12345", "All Applications", "appsc-group2", true, "Super Admin Pass", "Lifetime", "Super Admin"),
        UserAccountRecord("usr-3", "Rajesh Kumar", "rajesh.aspirant@gmail.com", "+91 97000 55443", "Banking IBPS & SBI", "banking-ibps", true, "Ranker 1-Year Pass", "2027-09-01", "contentadmin_banking@examportal.com"),
        UserAccountRecord("usr-4", "Priya Sharma", "priya.sharma@gmail.com", "+91 99887 76655", "SSC CGL & CHSL", "ssc-cgl", false, "Free Tier", "N/A", ""),
        UserAccountRecord("usr-5", "Koteswara Rao", "kotesh.rao@gmail.com", "+91 91234 56789", "APPSC Group 1", "appsc-group1", false, "Free Tier", "N/A", "")
    )

    var sampleUserProfile = UserProfileModel(
        name = "Srinivas Rao",
        email = "srinivas.appsc2026@gmail.com",
        phone = "+91 98765 43210",
        targetExam = "APPSC Group 2 (Executive)",
        targetAppId = "appsc-group2",
        preferredLanguage = "Telugu & English",
        dailyGoalQuestions = 30,
        streakDays = 14,
        isProSubscribed = true,
        proPlanName = "Achiever Plan (6 Months)"
    )

    // =========================================================================
    // 9. AUDIT LOGGING ENGINE
    // =========================================================================

    val auditLogsState = mutableStateListOf<AdminAuditLogModel>(
        AdminAuditLogModel(
            id = "log-1",
            timestamp = "2026-09-10 11:30",
            adminId = "SA-001",
            adminEmail = "sobhadhanaraju0@gmail.com",
            adminName = "Sobha Dhanaraju (Super Admin)",
            adminRole = "SUPER_ADMIN",
            appId = "global",
            appName = "Platform Multi-App Engine",
            actionType = "PLATFORM_INITIALIZED",
            details = "Multi-app architecture booted with 11 pre-configured exam tenants and RBAC isolation.",
            category = "System Core",
            recordCount = 11,
            status = "SUCCESS"
        ),
        AdminAuditLogModel(
            id = "log-2",
            timestamp = "2026-09-10 10:45",
            adminId = "CA-002",
            adminEmail = "contentadmin_group2@examportal.com",
            adminName = "Ravi Teja",
            adminRole = "CONTENT_ADMIN",
            appId = "appsc-group2",
            appName = "APPSC Group 2",
            actionType = "MCQS_IMPORTED_VIA_JSON",
            details = "Uploaded Indian_History.json into APPSC Group 2 syllabus. 450 MCQs validated and published.",
            category = "MCQ Management",
            recordCount = 450,
            status = "SUCCESS"
        ),
        AdminAuditLogModel(
            id = "log-3",
            timestamp = "2026-09-10 09:20",
            adminId = "SA-001",
            adminEmail = "sobhadhanaraju0@gmail.com",
            adminName = "Sobha Dhanaraju (Super Admin)",
            adminRole = "SUPER_ADMIN",
            appId = "appsc-group2",
            appName = "APPSC Group 2",
            actionType = "CA_PUBLISHED_MULTI_APP",
            details = "Shared September 2026 Current Affairs across APPSC Group 1, Group 2, Group 3, and Group 4.",
            category = "Current Affairs",
            recordCount = 4,
            status = "SUCCESS"
        )
    )

    fun addAuditLog(
        actionType: String,
        details: String,
        category: String,
        appId: String = currentActiveAppId.value,
        appName: String = appsListState.find { it.id == appId }?.name ?: "App",
        recordCount: Int = 1,
        status: String = "SUCCESS"
    ) {
        val currentAdmin = currentAdminAccount.value
        val log = AdminAuditLogModel(
            id = "log-${System.currentTimeMillis()}",
            timestamp = "2026-09-10 11:45",
            adminId = currentAdmin.id,
            adminEmail = currentAdmin.email,
            adminName = currentAdmin.name,
            adminRole = currentAdmin.role.name,
            appId = appId,
            appName = appName,
            actionType = actionType,
            details = details,
            recordCount = recordCount,
            category = category,
            status = status
        )
        auditLogsState.add(0, log)
    }

    // --- JSON BATCH IMPORT & ROLLBACK ---

    fun addQuestionsBatch(
        questionsToImport: List<Question>,
        batchRecord: ImportBatchRecord,
        replaceDuplicates: Boolean = false
    ) {
        val importedIds = mutableListOf<String>()

        questionsToImport.forEach { newQ ->
            val existingIndex = questionsState.indexOfFirst {
                it.id == newQ.id || (it.appId == newQ.appId && it.questionText.trim().equals(newQ.questionText.trim(), ignoreCase = true))
            }

            if (existingIndex != -1) {
                if (replaceDuplicates) {
                    questionsState[existingIndex] = newQ
                    importedIds.add(newQ.id)
                }
            } else {
                questionsState.add(0, newQ)
                importedIds.add(newQ.id)
            }
        }

        val updatedBatch = batchRecord.copy(importedQuestionIds = importedIds, importedCount = importedIds.size)
        importHistoryState.add(0, updatedBatch)

        addAuditLog(
            actionType = "MCQS_IMPORTED_VIA_JSON",
            details = "Imported ${importedIds.size} MCQs from '${batchRecord.fileName}' into '${batchRecord.destinationPath}' on app '${batchRecord.appId}'",
            category = "MCQ Management",
            appId = batchRecord.appId,
            appName = appsListState.find { it.id == batchRecord.appId }?.name ?: "App",
            recordCount = importedIds.size
        )
    }

    fun rollbackImportBatch(batchId: String): Boolean {
        val batchIdx = importHistoryState.indexOfFirst { it.id == batchId }
        if (batchIdx == -1) return false

        val batch = importHistoryState[batchIdx]
        if (batch.status == "Rolled Back") return false

        questionsState.removeAll { q -> q.importBatchId == batchId || batch.importedQuestionIds.contains(q.id) }
        importHistoryState[batchIdx] = batch.copy(status = "Rolled Back")

        addAuditLog(
            actionType = "MCQ_IMPORT_ROLLED_BACK",
            details = "Rolled back import batch '${batch.id}' (${batch.fileName}). Removed ${batch.importedQuestionIds.size} MCQs from '${batch.appId}'.",
            category = "MCQ Management",
            appId = batch.appId,
            appName = appsListState.find { it.id == batch.appId }?.name ?: "App",
            recordCount = batch.importedQuestionIds.size
        )
        return true
    }

    fun deleteQuestion(questionId: String) {
        val q = questionsState.find { it.id == questionId }
        if (q != null) {
            questionsState.removeAll { it.id == questionId }
            addAuditLog(
                actionType = "MCQ_DELETED",
                details = "Deleted MCQ '${q.questionText.take(30)}...'",
                category = "MCQ Management",
                appId = q.appId
            )
        }
    }

    fun grantManualSubscription(userId: String, appName: String, planName: String, durationDays: Int, adminEmail: String) {
        val idx = usersListState.indexOfFirst { it.id == userId }
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, durationDays)
        val expiryDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)
        if (idx != -1) {
            val user = usersListState[idx]
            usersListState[idx] = user.copy(
                targetApp = appName,
                isProSubscribed = true,
                proPlanName = planName,
                subscriptionExpiry = expiryDate,
                grantedByAdmin = adminEmail
            )
            if (user.email == sampleUserProfile.email) {
                sampleUserProfile = sampleUserProfile.copy(
                    isProSubscribed = true,
                    proPlanName = planName
                )
            }
            addAuditLog(
                actionType = "MANUAL_SUBSCRIPTION_GRANTED",
                details = "Granted '$planName' on app '$appName' (Valid for $durationDays days, expires $expiryDate) to user '${user.email}' by $adminEmail",
                category = "Subscriptions"
            )
        }
    }

    fun revokeManualSubscription(userId: String, adminEmail: String) {
        val idx = usersListState.indexOfFirst { it.id == userId }
        if (idx != -1) {
            val user = usersListState[idx]
            usersListState[idx] = user.copy(
                isProSubscribed = false,
                proPlanName = "Free Tier",
                subscriptionExpiry = "N/A",
                grantedByAdmin = ""
            )
            if (user.email == sampleUserProfile.email) {
                sampleUserProfile = sampleUserProfile.copy(
                    isProSubscribed = false,
                    proPlanName = "Free Tier"
                )
            }
            addAuditLog(
                actionType = "MANUAL_SUBSCRIPTION_REVOKED",
                details = "Revoked pro subscription from user '${user.email}' by $adminEmail",
                category = "Subscriptions"
            )
        }
    }

    // Other Sample Lists for Features
    val samplePackages = listOf(
        SubscriptionPackage("sub-1", "appsc-group2", "Starter Plan", 199.0, "₹", "1 Month", listOf("₹199/month", "Unlimited MCQ Practice", "Detailed Explanations"), false),
        SubscriptionPackage("sub-2", "appsc-group2", "Scholar Plan", 499.0, "₹", "3 Months", listOf("₹166/month", "Full-Length Mocks", "Daily Current Affairs"), false),
        SubscriptionPackage("sub-3", "appsc-group2", "Achiever Plan", 799.0, "₹", "6 Months", listOf("₹133/month", "Grand Mock Tests", "Current Affairs PDFs", "Topic Notes"), true),
        SubscriptionPackage("sub-4", "appsc-group2", "Ranker Plan", 999.0, "₹", "12 Months", listOf("₹83/month", "365 Days All-Access Pass", "Performance Analytics"), false)
    )

    val sampleLeaderboard = listOf(
        LeaderboardUser(1, "Srinivas Rao (You)", "1,420 pts", "98.5%", 24, "👑 State Rank #1", true, "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80"),
        LeaderboardUser(2, "Anitha Reddy", "1,385 pts", "96.2%", 22, "🔥 Streak 28d", false, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80"),
        LeaderboardUser(3, "Vamsi Krishna", "1,310 pts", "94.8%", 20, "⚡ Speed Master", false, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80"),
        LeaderboardUser(4, "Kavitha Priya", "1,250 pts", "92.1%", 19, "🎯 High Accuracy", false, "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=200&q=80"),
        LeaderboardUser(5, "Rajesh Varma", "1,190 pts", "89.5%", 18, "📚 Dedicated", false, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80")
    )

    val samplePyqPapers = listOf(
        PyqPaper(
            id = "pyq-2024-prelims",
            appId = "appsc-group2",
            year = "2024",
            title = "APPSC Group 2 Prelims Official Paper",
            examStage = "Prelims General Studies",
            totalQuestions = 150,
            durationMinutes = 150,
            description = "Official Question Paper with detailed Telugu & English keys for Indian History, Geography, Indian Society, Current Affairs & Mental Ability."
        )
    )

    val sampleMockTests = listOf(
        MockTestModel(
            id = "mock-grand-1",
            appId = "appsc-group2",
            title = "APPSC Group 2 Prelims Grand Mock Test 01",
            paperType = "Prelims Full Syllabus",
            durationMinutes = 150,
            totalQuestions = 150,
            totalMarks = 150,
            difficulty = "Exam Standard",
            description = "Strictly simulated on APPSC blueprint: 30 Qs History, 30 Qs Geography, 30 Qs Indian Society, 30 Qs Current Affairs, 30 Qs Mental Ability.",
            isFree = true
        ),
        MockTestModel(
            id = "mock-speed-mental",
            appId = "appsc-group2",
            title = "Speed Booster Test: Mental Ability & Reasoning",
            paperType = "Prelims Sectional",
            durationMinutes = 45,
            totalQuestions = 50,
            totalMarks = 50,
            difficulty = "Medium",
            description = "Rapid fire questions on Number Series, Analogy, Syllogisms, Coding-Decoding & Data Interpretation.",
            isFree = true
        )
    )

    val sampleStudyNotes = listOf(
        StudyNoteModel(
            id = "sn-ap-bifurcation",
            appId = "appsc-group2",
            subjectId = "subj-ap-history",
            title = "AP Reorganisation Act 2014 Key Sections & Provisions",
            category = "Act Summary",
            readTime = "10 min read",
            content = "The Andhra Pradesh Reorganisation Act, 2014 was passed by Parliament in Feb 2014. Key parts:\n- Part I: Preliminary definitions.\n- Part II: Reorganisation of AP state into Telangana and AP.\n- Section 3 & 4: Boundaries.\n- Section 8: Special responsibility of Governor for Hyderabad safety.\n- Section 46: Special package for Rayalaseema & North Coastal AP (7 districts).\n- Schedule 9 & 10: Division of State Companies, Corporations & Institutions.",
            highlights = listOf(
                "108 total sections, 13 schedules",
                "Section 8 Governor special powers",
                "7 Backward Districts under Section 46",
                "Common High Court & Capital provisions"
            )
        )
    )

    val sampleDailyQuestions = listOf(
        Question(
            id = "dq-1",
            appId = "appsc-group2",
            subjectId = "subj-indian-history",
            unitId = "unit-ih-ancient",
            topicId = "topic-ih-ancient-civ",
            subtopic = "Vedic Period",
            questionText = "In which Mandal of Rigveda is the famous 'Purusha Sukta' mentioning the Varna system found?",
            options = mapOf(
                "A" to "3rd Mandal",
                "B" to "7th Mandal",
                "C" to "9th Mandal",
                "D" to "10th Mandal"
            ),
            correctOption = "D",
            explanation = "The 10th Mandal of Rigveda contains Purusha Sukta, which outlines the origin of the four Varnas."
        )
    )
}
