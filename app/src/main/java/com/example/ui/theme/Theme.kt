package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

val LocalAppConfig = staticCompositionLocalOf<DynamicAppConfig> {
    DynamicAppConfig(
        appId = "appsc-group2",
        appName = "APPSC Group 2 MCQ Portal",
        appCode = "APPSC-G2",
        examCategory = "State PSC",
        tagline = "Complete Group 2 Preparation & MCQ Arena"
    )
}

val LocalDynamicTheme = staticCompositionLocalOf<DynamicThemeConfig> {
    DynamicThemeConfig()
}

val LocalDynamicCurrency = staticCompositionLocalOf<DynamicCurrencyConfig> {
    DynamicCurrencyConfig()
}

/**
 * Resolves a FontFamily token string into a Jetpack Compose FontFamily.
 */
fun resolveFontFamily(familyToken: String): FontFamily {
    return when (familyToken.lowercase().trim()) {
        "serif", "playfair", "editorial", "times" -> FontFamily.Serif
        "monospace", "mono", "roboto_mono", "fira_code" -> FontFamily.Monospace
        "cursive", "handwriting", "script" -> FontFamily.Cursive
        "sansserif", "sans_serif", "sans", "inter", "poppins", "outfit", "jakarta", "roboto" -> FontFamily.SansSerif
        else -> FontFamily.SansSerif
    }
}

/**
 * Builds a dynamic Jetpack Compose Typography system based on the backend theme configuration.
 */
fun buildDynamicTypography(themeConfig: DynamicThemeConfig): Typography {
    val headingFamily = when (themeConfig.fontPreset) {
        FontPreset.EDITORIAL_SERIF -> FontFamily.Serif
        FontPreset.TECHNICAL_MONO -> FontFamily.Monospace
        FontPreset.BOLD_GEOMETRIC -> FontFamily.SansSerif
        FontPreset.ROUNDED_FRIENDLY -> FontFamily.SansSerif
        FontPreset.MODERN_SANS -> resolveFontFamily(themeConfig.headingFontFamily)
        FontPreset.CLASSIC_SYSTEM -> FontFamily.Default
    }

    val bodyFamily = when (themeConfig.fontPreset) {
        FontPreset.TECHNICAL_MONO -> FontFamily.Monospace
        FontPreset.EDITORIAL_SERIF -> FontFamily.Serif
        else -> resolveFontFamily(themeConfig.bodyFontFamily)
    }

    val headingWeight = when (themeConfig.fontPreset) {
        FontPreset.BOLD_GEOMETRIC -> FontWeight.ExtraBold
        FontPreset.EDITORIAL_SERIF -> FontWeight.Bold
        FontPreset.ROUNDED_FRIENDLY -> FontWeight.SemiBold
        else -> FontWeight.Bold
    }

    val letterSpacingDelta = when (themeConfig.fontPreset) {
        FontPreset.BOLD_GEOMETRIC -> 0.2f
        FontPreset.TECHNICAL_MONO -> 0.5f
        FontPreset.EDITORIAL_SERIF -> 0.1f
        else -> 0.0f
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = headingFamily,
            fontWeight = headingWeight,
            fontSize = 32.sp * themeConfig.fontScaleRatio,
            lineHeight = 40.sp,
            letterSpacing = (-0.5f + letterSpacingDelta).sp
        ),
        displayMedium = TextStyle(
            fontFamily = headingFamily,
            fontWeight = headingWeight,
            fontSize = 26.sp * themeConfig.fontScaleRatio,
            lineHeight = 34.sp,
            letterSpacing = (-0.25f + letterSpacingDelta).sp
        ),
        displaySmall = TextStyle(
            fontFamily = headingFamily,
            fontWeight = headingWeight,
            fontSize = 22.sp * themeConfig.fontScaleRatio,
            lineHeight = 28.sp,
            letterSpacing = (0f + letterSpacingDelta).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = headingFamily,
            fontWeight = headingWeight,
            fontSize = 20.sp * themeConfig.fontScaleRatio,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp * themeConfig.fontScaleRatio,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp * themeConfig.fontScaleRatio,
            lineHeight = 22.sp,
            letterSpacing = 0.15.sp
        ),
        titleLarge = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp * themeConfig.fontScaleRatio,
            lineHeight = 23.sp,
            letterSpacing = 0.1.sp
        ),
        titleMedium = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp * themeConfig.fontScaleRatio,
            lineHeight = 20.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp * themeConfig.fontScaleRatio,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp * themeConfig.fontScaleRatio,
            lineHeight = 22.sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp * themeConfig.fontScaleRatio,
            lineHeight = 19.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp * themeConfig.fontScaleRatio,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp * themeConfig.fontScaleRatio,
            lineHeight = 18.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp * themeConfig.fontScaleRatio,
            lineHeight = 15.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = headingFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp * themeConfig.fontScaleRatio,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
        )
    )
}

/**
 * Builds dynamic Material 3 Shapes based on corner radius configurations.
 */
fun buildDynamicShapes(themeConfig: DynamicThemeConfig): Shapes {
    val cardRadius = themeConfig.cardCornerRadiusDp.dp
    val buttonRadius = themeConfig.buttonCornerRadiusDp.dp
    val chipRadius = themeConfig.chipCornerRadiusDp.dp

    return Shapes(
        extraSmall = RoundedCornerShape(chipRadius / 2),
        small = RoundedCornerShape(chipRadius),
        medium = RoundedCornerShape(buttonRadius),
        large = RoundedCornerShape(cardRadius),
        extraLarge = RoundedCornerShape((cardRadius.value + 8).dp)
    )
}

/**
 * Dynamic MaterialTheme Wrapper.
 * Dynamically binds remote backend theme parameters (colors, gradients, typography, font families, and shapes)
 * into standard MaterialTheme tokens and CompositionLocals.
 */
@Composable
fun DynamicAppTheme(
    config: DynamicAppConfig,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = config.theme
    val primaryColor = Color(theme.primaryColorHex)
    val primaryDarkColor = Color(theme.primaryDarkColorHex)
    val secondaryColor = Color(theme.secondaryColorHex)
    val accentColor = Color(theme.accentColorHex)
    val backgroundColor = Color(theme.backgroundColorHex)
    val surfaceColor = Color(theme.surfaceColorHex)
    val borderHighlightColor = Color(theme.borderHighlightColorHex)

    val colorScheme = if (darkTheme && theme.isDarkThemeAllowed) {
        val baseDark = darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryDarkColor,
            onPrimaryContainer = Color.White,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = secondaryColor.copy(alpha = 0.2f),
            onSecondaryContainer = Color.White,
            tertiary = accentColor,
            onTertiary = Color.White,
            background = Color(0xFF0F172A),
            onBackground = Color(0xFFF8FAFC),
            surface = Color(0xFF1E293B),
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFF334155),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF475569),
            outlineVariant = borderHighlightColor.copy(alpha = 0.3f)
        )
        ThemeManager.applyTheme(baseDark, AppThemeConfig.SoftCharcoal)
    } else {
        val baseLight = lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.12f),
            onPrimaryContainer = primaryDarkColor,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = secondaryColor.copy(alpha = 0.12f),
            onSecondaryContainer = secondaryColor,
            tertiary = accentColor,
            onTertiary = Color.White,
            tertiaryContainer = accentColor.copy(alpha = 0.15f),
            onTertiaryContainer = Color(0xFF78350F),
            background = backgroundColor,
            onBackground = Color(0xFF0F172A),
            surface = surfaceColor,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFE2E8F0),
            outlineVariant = borderHighlightColor
        )
        ThemeManager.applyTheme(baseLight, AppThemeConfig.Slate)
    }

    val dynamicTypography = remember(theme) {
        buildDynamicTypography(theme)
    }

    val dynamicShapes = remember(theme) {
        buildDynamicShapes(theme)
    }

    CompositionLocalProvider(
        LocalAppConfig provides config,
        LocalDynamicTheme provides config.theme,
        LocalDynamicCurrency provides config.currency
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = dynamicTypography,
            shapes = dynamicShapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val defaultLightScheme = lightColorScheme(
        primary = AppOrangePrimary,
        onPrimary = Color.White,
        primaryContainer = AppOrangeLight,
        onPrimaryContainer = AppOrangeAmber,
        secondary = AppPurplePrimary,
        onSecondary = Color.White,
        tertiary = AppBlueBorder,
        background = AppLightBg,
        onBackground = AppTextDark,
        surface = AppLightSurface,
        onSurface = AppTextDark
    )
    
    val colorScheme = ThemeManager.applyTheme(defaultLightScheme, AppThemeConfig.Slate)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
