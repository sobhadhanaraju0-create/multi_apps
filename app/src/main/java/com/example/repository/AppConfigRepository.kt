package com.example.repository

import android.content.Context
import android.util.Log
import com.example.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Backend Remote Service Contract for Dynamic App Configuration
 */
interface AppConfigRemoteDataSource {
    suspend fun getAppConfig(appId: String): Result<DynamicAppConfig>
    suspend fun getAllAppConfigs(): Result<List<DynamicAppConfig>>
    suspend fun saveOrUpdateAppConfig(config: DynamicAppConfig): Result<DynamicAppConfig>
}

/**
 * In-memory & Persistent Local Data Source Contract
 */
interface AppConfigLocalDataSource {
    suspend fun getCachedConfig(appId: String): DynamicAppConfig?
    suspend fun getCachedAllConfigs(): List<DynamicAppConfig>
    suspend fun saveConfig(config: DynamicAppConfig)
    suspend fun saveAllConfigs(configs: List<DynamicAppConfig>)
    suspend fun getActiveAppId(): String
    suspend fun setActiveAppId(appId: String)
}

/**
 * Mock / Production Remote Backend Implementation
 * Simulates a high-performance REST / GraphQL Cloud configuration endpoint
 * returning JSON payloads with ETag cache-validation headers.
 */
class DefaultRemoteAppConfigDataSource : AppConfigRemoteDataSource {

    // Simulated remote cloud database of app configurations
    private val remoteCloudConfigDatabase = mutableMapOf<String, DynamicAppConfig>()

    init {
        // Seed default multi-app tenant configs on the simulated backend server
        seedDefaultRemoteConfigs()
    }

    override suspend fun getAppConfig(appId: String): Result<DynamicAppConfig> {
        return withContext(Dispatchers.IO) {
            try {
                // Simulate network latency (e.g. 50ms)
                kotlinx.coroutines.delay(40)
                val config = remoteCloudConfigDatabase[appId]
                    ?: seedFallbackConfig(appId)

                Result.success(
                    config.copy(
                        syncMetadata = config.syncMetadata.copy(
                            lastFetchedTimestamp = System.currentTimeMillis(),
                            isFromCache = false
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("AppConfigRemote", "Failed to fetch remote config for $appId: ${e.message}")
                Result.failure(e)
            }
        }
    }

    override suspend fun getAllAppConfigs(): Result<List<DynamicAppConfig>> {
        return withContext(Dispatchers.IO) {
            try {
                kotlinx.coroutines.delay(60)
                Result.success(remoteCloudConfigDatabase.values.toList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveOrUpdateAppConfig(config: DynamicAppConfig): Result<DynamicAppConfig> {
        return withContext(Dispatchers.IO) {
            try {
                kotlinx.coroutines.delay(50)
                val updated = config.copy(
                    syncMetadata = config.syncMetadata.copy(
                        configVersion = config.syncMetadata.configVersion + 1,
                        lastFetchedTimestamp = System.currentTimeMillis(),
                        etag = "v${System.currentTimeMillis()}"
                    )
                )
                remoteCloudConfigDatabase[config.appId] = updated
                Result.success(updated)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun seedDefaultRemoteConfigs() {
        val defaultConfigs = listOf(
            DynamicAppConfig(
                appId = "appsc-group2",
                appName = "APPSC Group 2 MCQ Portal",
                appCode = "APPSC-G2",
                examCategory = "State PSC",
                tagline = "Complete Group 2 Preparation & MCQ Arena",
                description = "Dedicated APPSC Group 2 examination preparation portal with paper 1 & 2 topic-wise tests.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF2563EB,
                    primaryDarkColorHex = 0xFF1D4ED8,
                    secondaryColorHex = 0xFF7C3AED,
                    accentColorHex = 0xFFFF7A00,
                    borderHighlightColorHex = 0xFF2563EB,
                    appBarGradientStartHex = 0xFF1E3A8A,
                    appBarGradientEndHex = 0xFF0F172A,
                    heroGradientStartHex = 0xFFEFF6FF,
                    heroGradientMidHex = 0xFFEDE9FE,
                    heroGradientEndHex = 0xFFDBEAFE,
                    fontPreset = FontPreset.MODERN_SANS,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "SansSerif",
                    cardCornerRadiusDp = 20,
                    buttonCornerRadiusDp = 12,
                    iconBadgeName = "AccountBalance"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "Telugu",
                    supportedLanguages = listOf("Telugu", "English")
                ),
                assignedAdminEmail = "contentadmin_group2@examportal.com",
                assignedAdminName = "Ravi Teja"
            ),
            DynamicAppConfig(
                appId = "appsc-group1",
                appName = "APPSC Group 1 Civil Services",
                appCode = "APPSC-G1",
                examCategory = "State PSC",
                tagline = "Executive & Administrative Cadre MCQ Mastery",
                description = "Comprehensive APPSC Group 1 exam prep with descriptive & MCQ modules.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF4F46E5,
                    primaryDarkColorHex = 0xFF3730A3,
                    secondaryColorHex = 0xFF06B6D4,
                    accentColorHex = 0xFFEC4899,
                    borderHighlightColorHex = 0xFF4F46E5,
                    appBarGradientStartHex = 0xFF312E81,
                    appBarGradientEndHex = 0xFF1E1B4B,
                    heroGradientStartHex = 0xFFEEF2FF,
                    heroGradientMidHex = 0xFFECFEFF,
                    heroGradientEndHex = 0xFFE0E7FF,
                    fontPreset = FontPreset.BOLD_GEOMETRIC,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "SansSerif",
                    cardCornerRadiusDp = 16,
                    buttonCornerRadiusDp = 10,
                    iconBadgeName = "WorkspacePremium"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "Telugu",
                    supportedLanguages = listOf("Telugu", "English")
                ),
                assignedAdminEmail = "contentadmin_group1@examportal.com",
                assignedAdminName = "Kavitha Reddy"
            ),
            DynamicAppConfig(
                appId = "ap-dsc",
                appName = "AP DSC Teacher Recruitment",
                appCode = "AP-DSC",
                examCategory = "Teaching (DSC/TET)",
                tagline = "School Assistant & SGT Practice Portal",
                description = "Teacher recruitment exam preparation for SGT and School Assistant candidates.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF0D9488,
                    primaryDarkColorHex = 0xFF0F766E,
                    secondaryColorHex = 0xFF0284C7,
                    accentColorHex = 0xFFF97316,
                    borderHighlightColorHex = 0xFF0D9488,
                    appBarGradientStartHex = 0xFF134E4A,
                    appBarGradientEndHex = 0xFF042F2E,
                    heroGradientStartHex = 0xFFF0FDFA,
                    heroGradientMidHex = 0xFFE0F2FE,
                    heroGradientEndHex = 0xFFCCFBF1,
                    fontPreset = FontPreset.ROUNDED_FRIENDLY,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "SansSerif",
                    cardCornerRadiusDp = 22,
                    buttonCornerRadiusDp = 14,
                    iconBadgeName = "School"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "Telugu",
                    supportedLanguages = listOf("Telugu", "English")
                ),
                assignedAdminEmail = "contentadmin_apdsc@examportal.com",
                assignedAdminName = "Nageswara Rao"
            ),
            DynamicAppConfig(
                appId = "ssc-cgl",
                appName = "SSC CGL & CHSL Exam Arena",
                appCode = "SSC-CGL",
                examCategory = "Central SSC",
                tagline = "Tier-1 & Tier-2 Sectional Question Bank",
                description = "Staff Selection Commission Combined Graduate Level examination test series.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFFEA580C,
                    primaryDarkColorHex = 0xFFC2410C,
                    secondaryColorHex = 0xFF2563EB,
                    accentColorHex = 0xFF10B981,
                    borderHighlightColorHex = 0xFFEA580C,
                    appBarGradientStartHex = 0xFF7C2D12,
                    appBarGradientEndHex = 0xFF431407,
                    heroGradientStartHex = 0xFFFFF7ED,
                    heroGradientMidHex = 0xFFEFF6FF,
                    heroGradientEndHex = 0xFFFFEDD5,
                    fontPreset = FontPreset.BOLD_GEOMETRIC,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "SansSerif",
                    cardCornerRadiusDp = 14,
                    buttonCornerRadiusDp = 8,
                    iconBadgeName = "MilitaryTech"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "English",
                    supportedLanguages = listOf("English", "Hindi")
                ),
                assignedAdminEmail = "contentadmin_ssc@examportal.com",
                assignedAdminName = "Vikram Singh"
            ),
            DynamicAppConfig(
                appId = "banking-ibps",
                appName = "Banking IBPS & SBI PO/Clerk",
                appCode = "BANK-IBPS",
                examCategory = "Banking & Insurance",
                tagline = "Speed & Accuracy Daily Drills & Mock Tests",
                description = "Banking exams preparation with daily quantitative aptitude and reasoning tests.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF059669,
                    primaryDarkColorHex = 0xFF047857,
                    secondaryColorHex = 0xFF6366F1,
                    accentColorHex = 0xFFF59E0B,
                    borderHighlightColorHex = 0xFF059669,
                    appBarGradientStartHex = 0xFF064E3B,
                    appBarGradientEndHex = 0xFF022C22,
                    heroGradientStartHex = 0xFFECFDF5,
                    heroGradientMidHex = 0xFFEEF2FF,
                    heroGradientEndHex = 0xFFD1FAE5,
                    fontPreset = FontPreset.TECHNICAL_MONO,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "Monospace",
                    cardCornerRadiusDp = 12,
                    buttonCornerRadiusDp = 8,
                    iconBadgeName = "AccountBalanceWallet"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "English",
                    supportedLanguages = listOf("English", "Hindi")
                ),
                assignedAdminEmail = "contentadmin_banking@examportal.com",
                assignedAdminName = "Ananya Roy"
            ),
            DynamicAppConfig(
                appId = "upsc-cse",
                appName = "UPSC Civil Services Prelims",
                appCode = "UPSC-CSE",
                examCategory = "UPSC Civil Services",
                tagline = "General Studies & CSAT Standard MCQs",
                description = "India's premier civil services examination test engine.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF7C3AED,
                    primaryDarkColorHex = 0xFF6D28D9,
                    secondaryColorHex = 0xFF2563EB,
                    accentColorHex = 0xFFE11D48,
                    borderHighlightColorHex = 0xFF7C3AED,
                    appBarGradientStartHex = 0xFF4C1D95,
                    appBarGradientEndHex = 0xFF2E1065,
                    heroGradientStartHex = 0xFFFAF5FF,
                    heroGradientMidHex = 0xFFEFF6FF,
                    heroGradientEndHex = 0xFFF3E8FF,
                    fontPreset = FontPreset.EDITORIAL_SERIF,
                    headingFontFamily = "Serif",
                    bodyFontFamily = "Serif",
                    cardCornerRadiusDp = 18,
                    buttonCornerRadiusDp = 12,
                    iconBadgeName = "LocalPolice"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 0
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "English",
                    supportedLanguages = listOf("English", "Hindi")
                ),
                assignedAdminEmail = "contentadmin_upsc@examportal.com",
                assignedAdminName = "Dr. Amit Sharma"
            ),
            DynamicAppConfig(
                appId = "us-sat-act",
                appName = "Global SAT & ACT Prep",
                appCode = "SAT-ACT",
                examCategory = "International Standardized",
                tagline = "Math & Reading Dynamic Question Vault",
                description = "International college admissions prep supporting USD pricing.",
                theme = DynamicThemeConfig(
                    primaryColorHex = 0xFF0284C7,
                    primaryDarkColorHex = 0xFF0369A1,
                    secondaryColorHex = 0xFF4F46E5,
                    accentColorHex = 0xFF10B981,
                    borderHighlightColorHex = 0xFF0284C7,
                    appBarGradientStartHex = 0xFF075985,
                    appBarGradientEndHex = 0xFF0C4A6E,
                    heroGradientStartHex = 0xFFF0F9FF,
                    heroGradientMidHex = 0xFFEEF2FF,
                    heroGradientEndHex = 0xFFE0F2FE,
                    fontPreset = FontPreset.MODERN_SANS,
                    headingFontFamily = "SansSerif",
                    bodyFontFamily = "SansSerif",
                    cardCornerRadiusDp = 16,
                    buttonCornerRadiusDp = 10,
                    iconBadgeName = "Public"
                ),
                currency = DynamicCurrencyConfig(
                    currencyCode = "USD",
                    currencySymbol = "$",
                    currencyPosition = CurrencyPosition.PREFIX,
                    decimalPlaces = 2
                ),
                features = DynamicFeatureConfig(),
                localization = DynamicLocalizationConfig(
                    defaultLanguage = "English",
                    supportedLanguages = listOf("English", "Spanish")
                ),
                assignedAdminEmail = "contentadmin_sat@examportal.com",
                assignedAdminName = "Sarah Jenkins"
            )
        )

        defaultConfigs.forEach { remoteCloudConfigDatabase[it.appId] = it }
    }

    private fun seedFallbackConfig(appId: String): DynamicAppConfig {
        return DynamicAppConfig(
            appId = appId,
            appName = appId.replace("-", " ").replaceFirstChar { it.uppercase() },
            appCode = appId.take(6).uppercase(),
            examCategory = "General Competitive",
            tagline = "Interactive Practice & Examination Portal"
        )
    }
}

/**
 * In-Memory Local Cache Implementation
 */
class InMemoryLocalAppConfigDataSource : AppConfigLocalDataSource {
    private val localCache = mutableMapOf<String, DynamicAppConfig>()
    private var activeAppId: String = "appsc-group2"

    override suspend fun getCachedConfig(appId: String): DynamicAppConfig? {
        return localCache[appId]
    }

    override suspend fun getCachedAllConfigs(): List<DynamicAppConfig> {
        return localCache.values.toList()
    }

    override suspend fun saveConfig(config: DynamicAppConfig) {
        localCache[config.appId] = config.copy(
            syncMetadata = config.syncMetadata.copy(isFromCache = true)
        )
    }

    override suspend fun saveAllConfigs(configs: List<DynamicAppConfig>) {
        configs.forEach { saveConfig(it) }
    }

    override suspend fun getActiveAppId(): String {
        return activeAppId
    }

    override suspend fun setActiveAppId(appId: String) {
        activeAppId = appId
    }
}

/**
 * AppConfigRepository: High-level repository for fetching, caching, and updating dynamic app configurations.
 * Ensures a single Android codebase can effortlessly support multiple distinct app instances
 * with remote theming, currency, feature toggling, and branding.
 */
class AppConfigRepository(
    private val remoteDataSource: AppConfigRemoteDataSource = DefaultRemoteAppConfigDataSource(),
    private val localDataSource: AppConfigLocalDataSource = InMemoryLocalAppConfigDataSource(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _currentAppConfig = MutableStateFlow(
        DynamicAppConfig(
            appId = "appsc-group2",
            appName = "APPSC Group 2 MCQ Portal",
            appCode = "APPSC-G2",
            examCategory = "State PSC",
            tagline = "Complete Group 2 Preparation & MCQ Arena",
            theme = DynamicThemeConfig(primaryColorHex = 0xFF2563EB),
            currency = DynamicCurrencyConfig(currencyCode = "INR", currencySymbol = "₹")
        )
    )
    val currentAppConfig: StateFlow<DynamicAppConfig> = _currentAppConfig.asStateFlow()

    private val _allAppConfigs = MutableStateFlow<List<DynamicAppConfig>>(emptyList())
    val allAppConfigs: StateFlow<List<DynamicAppConfig>> = _allAppConfigs.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    init {
        // Automatically fetch initial app configs
        CoroutineScope(ioDispatcher).launch {
            initialize()
        }
    }

    /**
     * Initializes repository by loading local cache, then refreshing from remote backend.
     */
    suspend fun initialize() = withContext(ioDispatcher) {
        _isSyncing.value = true
        try {
            val activeId = localDataSource.getActiveAppId()
            val cached = localDataSource.getCachedConfig(activeId)
            if (cached != null) {
                _currentAppConfig.value = cached
            }

            val cachedAll = localDataSource.getCachedAllConfigs()
            if (cachedAll.isNotEmpty()) {
                _allAppConfigs.value = cachedAll
            }

            // Sync from remote
            val remoteAllResult = remoteDataSource.getAllAppConfigs()
            if (remoteAllResult.isSuccess) {
                val list = remoteAllResult.getOrThrow()
                localDataSource.saveAllConfigs(list)
                _allAppConfigs.value = list

                val activeConfig = list.find { it.appId == activeId } ?: list.firstOrNull()
                if (activeConfig != null) {
                    _currentAppConfig.value = activeConfig
                    localDataSource.saveConfig(activeConfig)
                }
            }
            _syncError.value = null
        } catch (e: Exception) {
            _syncError.value = e.message
            Log.e("AppConfigRepo", "Initialization error: ${e.message}")
        } finally {
            _isSyncing.value = false
        }
    }

    /**
     * Fetches configuration for a specific application tenant.
     * Uses offline cache-first strategy with automatic background refresh.
     */
    suspend fun fetchAppConfig(appId: String, forceRefresh: Boolean = false): Result<DynamicAppConfig> {
        return withContext(ioDispatcher) {
            _isSyncing.value = true
            try {
                // If not forcing refresh, check local cache first
                if (!forceRefresh) {
                    val cached = localDataSource.getCachedConfig(appId)
                    if (cached != null) {
                        _isSyncing.value = false
                        return@withContext Result.success(cached)
                    }
                }

                // Fetch from remote backend
                val remoteResult = remoteDataSource.getAppConfig(appId)
                if (remoteResult.isSuccess) {
                    val config = remoteResult.getOrThrow()
                    localDataSource.saveConfig(config)
                    if (appId == _currentAppConfig.value.appId) {
                        _currentAppConfig.value = config
                    }
                    _syncError.value = null
                    Result.success(config)
                } else {
                    val cached = localDataSource.getCachedConfig(appId)
                    if (cached != null) {
                        Result.success(cached)
                    } else {
                        Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error"))
                    }
                }
            } catch (e: Exception) {
                _syncError.value = e.message
                Result.failure(e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    /**
     * Switches the currently active application instance.
     * Triggers dynamic updates to theme, currency, app name, and feature availability.
     */
    suspend fun switchActiveAppTenant(appId: String): Result<DynamicAppConfig> {
        return withContext(ioDispatcher) {
            localDataSource.setActiveAppId(appId)
            val result = fetchAppConfig(appId, forceRefresh = false)
            if (result.isSuccess) {
                _currentAppConfig.value = result.getOrThrow()
            }
            result
        }
    }

    /**
     * Saves or updates an application tenant configuration (Used by Super Admin).
     */
    suspend fun updateAppConfig(updatedConfig: DynamicAppConfig): Result<DynamicAppConfig> {
        return withContext(ioDispatcher) {
            _isSyncing.value = true
            try {
                val remoteResult = remoteDataSource.saveOrUpdateAppConfig(updatedConfig)
                if (remoteResult.isSuccess) {
                    val saved = remoteResult.getOrThrow()
                    localDataSource.saveConfig(saved)
                    if (saved.appId == _currentAppConfig.value.appId) {
                        _currentAppConfig.value = saved
                    }
                    // Refresh all configs list
                    val currentList = _allAppConfigs.value.toMutableList()
                    val index = currentList.indexOfFirst { it.appId == saved.appId }
                    if (index != -1) {
                        currentList[index] = saved
                    } else {
                        currentList.add(saved)
                    }
                    _allAppConfigs.value = currentList
                    _syncError.value = null
                    Result.success(saved)
                } else {
                    Result.failure(remoteResult.exceptionOrNull() ?: Exception("Failed to update config"))
                }
            } catch (e: Exception) {
                _syncError.value = e.message
                Result.failure(e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    /**
     * Helper to format a monetary amount using the active app instance's currency rules.
     */
    fun formatPrice(amount: Double): String {
        return _currentAppConfig.value.currency.formatPrice(amount)
    }

    /**
     * Helper to check if a feature is enabled on the current app instance.
     */
    fun isFeatureEnabled(predicate: (DynamicFeatureConfig) -> Boolean): Boolean {
        return predicate(_currentAppConfig.value.features)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppConfigRepository? = null

        fun getInstance(): AppConfigRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppConfigRepository().also { INSTANCE = it }
            }
        }
    }
}
