package com.example.model

import androidx.compose.ui.graphics.Color
import java.text.DecimalFormat

/**
 * Currency Symbol Position (e.g. "₹599" vs "599 kr")
 */
enum class CurrencyPosition {
    PREFIX,
    SUFFIX
}

/**
 * Font Preset Types supported by the dynamic theme engine
 */
enum class FontPreset {
    MODERN_SANS,       // Clean geometric sans-serif (Inter/Jakarta style)
    EDITORIAL_SERIF,   // Prestigious academic editorial serif (UPSC/Civil Services style)
    TECHNICAL_MONO,    // High-precision tabular monospace (Data/Aptitude style)
    BOLD_GEOMETRIC,    // High-impact athletic modern display (SSC/Defence style)
    ROUNDED_FRIENDLY,  // Approachable modern rounded (Teaching/TET/DSC style)
    CLASSIC_SYSTEM     // Standard Android system default
}

/**
 * Dynamic Theme Configuration fetched from the backend.
 * Defines colors, typography branding, font families, hero styling, and shapes for the app tenant.
 */
data class DynamicThemeConfig(
    val primaryColorHex: Long = 0xFF2563EB,
    val primaryDarkColorHex: Long = 0xFF1D4ED8,
    val secondaryColorHex: Long = 0xFF7C3AED,
    val accentColorHex: Long = 0xFFF59E0B,
    val surfaceColorHex: Long = 0xFFFFFFFF,
    val backgroundColorHex: Long = 0xFFF0F2F5, // Premium soft light gray
    val borderHighlightColorHex: Long = 0xFF2563EB,
    val appBarGradientStartHex: Long = 0xFF1E3A8A,
    val appBarGradientEndHex: Long = 0xFF0F172A,
    val heroGradientStartHex: Long = 0xFFEFF6FF,
    val heroGradientMidHex: Long = 0xFFEDE9FE,
    val heroGradientEndHex: Long = 0xFFDBEAFE,
    val isDarkThemeAllowed: Boolean = true,
    val fontPreset: FontPreset = FontPreset.MODERN_SANS,
    val headingFontFamily: String = "SansSerif", // "SansSerif", "Serif", "Monospace", "Cursive"
    val bodyFontFamily: String = "SansSerif",    // "SansSerif", "Serif", "Monospace"
    val fontScaleRatio: Float = 1.0f,
    val cardCornerRadiusDp: Int = 16,
    val buttonCornerRadiusDp: Int = 12,
    val chipCornerRadiusDp: Int = 8,
    val logoUrl: String? = null,
    val heroBannerUrl: String? = null,
    val iconBadgeName: String = "AccountBalance"
) {
    fun getPrimaryComposeColor(): Color = Color(primaryColorHex)
    fun getSecondaryComposeColor(): Color = Color(secondaryColorHex)
    fun getAccentComposeColor(): Color = Color(accentColorHex)
    fun getSurfaceComposeColor(): Color = Color(surfaceColorHex)
    fun getBackgroundComposeColor(): Color = Color(backgroundColorHex)
    fun getBorderHighlightComposeColor(): Color = Color(borderHighlightColorHex)
    fun getHeroStartComposeColor(): Color = Color(heroGradientStartHex)
    fun getHeroMidComposeColor(): Color = Color(heroGradientMidHex)
    fun getHeroEndComposeColor(): Color = Color(heroGradientEndHex)
    fun getAppBarStartComposeColor(): Color = Color(appBarGradientStartHex)
    fun getAppBarEndComposeColor(): Color = Color(appBarGradientEndHex)
}

/**
 * Dynamic Currency Configuration fetched from the backend.
 * Enables automatic currency symbol, position, formatting, and international price rendering.
 */
data class DynamicCurrencyConfig(
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val currencyPosition: CurrencyPosition = CurrencyPosition.PREFIX,
    val decimalPlaces: Int = 0,
    val thousandsSeparator: String = ",",
    val decimalSeparator: String = ".",
    val defaultTaxPercentage: Double = 18.0
) {
    /**
     * Formats a raw numeric price according to the app instance's currency rule.
     * Example: 599.0 -> "₹599" or "$7.99"
     */
    fun formatPrice(amount: Double): String {
        val pattern = if (decimalPlaces > 0) {
            "#,##0." + "0".repeat(decimalPlaces)
        } else {
            "#,##0"
        }
        val decimalFormat = DecimalFormat(pattern)
        val formattedNumber = decimalFormat.format(amount)

        return when (currencyPosition) {
            CurrencyPosition.PREFIX -> "$currencySymbol$formattedNumber"
            CurrencyPosition.SUFFIX -> "$formattedNumber $currencySymbol"
        }
    }
}

/**
 * Dynamic Feature Toggles Config.
 * Allows backend to turn features on/off per exam tenant dynamically without releasing new APKs.
 */
data class DynamicFeatureConfig(
    val isMcqPracticeEnabled: Boolean = true,
    val isDailyQuizEnabled: Boolean = true,
    val isMockTestsEnabled: Boolean = true,
    val isCurrentAffairsEnabled: Boolean = true,
    val isMonthlyPdfEnabled: Boolean = true,
    val isPromotionalPostersEnabled: Boolean = true,
    val isSubscriptionsEnabled: Boolean = true,
    val isCouponsEnabled: Boolean = true,
    val isScratchpadEnabled: Boolean = true,
    val isPerformanceAnalyticsEnabled: Boolean = true,
    val isLeaderboardEnabled: Boolean = true,
    val isSavedQuestionsEnabled: Boolean = true,
    val isAiExplanationEnabled: Boolean = true
)

/**
 * Dynamic Localization & Language Options
 */
data class DynamicLocalizationConfig(
    val defaultLanguage: String = "English",
    val supportedLanguages: List<String> = listOf("Telugu", "English"),
    val fallbackLanguage: String = "English"
)

/**
 * Dynamic Support & Legal Info
 */
data class DynamicSupportConfig(
    val supportEmail: String = "support@examportal.com",
    val helpdeskUrl: String = "https://help.examportal.com",
    val whatsappContact: String = "+919876543210",
    val privacyPolicyUrl: String = "https://examportal.com/privacy",
    val termsOfServiceUrl: String = "https://examportal.com/terms"
)

/**
 * Dynamic App Versioning and Force Update Control
 */
data class DynamicVersionConfig(
    val minSupportedVersionCode: Int = 1,
    val latestVersionCode: Int = 1,
    val forceUpdateRequired: Boolean = false,
    val updateUrl: String = "https://play.google.com/store/apps",
    val releaseNotes: String = "Bug fixes, new syllabus updates, and performance improvements."
)

/**
 * Metadata about backend synchronization and caching TTL
 */
data class ConfigSyncMetadata(
    val configVersion: Long = 1L,
    val lastFetchedTimestamp: Long = System.currentTimeMillis(),
    val etag: String = "v1-init",
    val cacheTtlSeconds: Long = 3600L,
    val isFromCache: Boolean = false
)

/**
 * Root Data Model: Dynamic App Configuration
 * This model encapsulates all properties needed by a single codebase to dynamically instantiate
 * and customize an application tenant on the fly.
 */
data class DynamicAppConfig(
    val appId: String,
    val appName: String,
    val appCode: String,
    val examCategory: String, // e.g. "State PSC", "Central SSC", "Banking & Insurance", "Teaching (DSC/TET)", "UPSC"
    val tagline: String,
    val description: String = "",
    val packageName: String = "com.aistudio.examportal",
    val isActive: Boolean = true,
    val theme: DynamicThemeConfig = DynamicThemeConfig(),
    val currency: DynamicCurrencyConfig = DynamicCurrencyConfig(),
    val features: DynamicFeatureConfig = DynamicFeatureConfig(),
    val localization: DynamicLocalizationConfig = DynamicLocalizationConfig(),
    val support: DynamicSupportConfig = DynamicSupportConfig(),
    val versioning: DynamicVersionConfig = DynamicVersionConfig(),
    val syncMetadata: ConfigSyncMetadata = ConfigSyncMetadata(),
    val assignedAdminEmail: String = "",
    val assignedAdminName: String = "",
    val extraMetadata: Map<String, String> = emptyMap()
)
