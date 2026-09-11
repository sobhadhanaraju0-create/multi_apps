package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

data class AppThemeConfig(
    val primaryBackground: Color,
    val secondaryBackground: Color,
    val surfaceColor: Color,
    val onBackground: Color,
    val onSurface: Color
) {
    companion object {
        val Slate = AppThemeConfig(
            primaryBackground = Color(0xFFF1F5F9), // Slate 100
            secondaryBackground = Color(0xFFE2E8F0), // Slate 200
            surfaceColor = Color(0xFFFFFFFF),
            onBackground = Color(0xFF0F172A), // Slate 900
            onSurface = Color(0xFF0F172A)
        )
        
        val Teal = AppThemeConfig(
            primaryBackground = Color(0xFFF0FDF4), // Green 50
            secondaryBackground = Color(0xFFDCFCE7), // Green 100
            surfaceColor = Color(0xFFFFFFFF),
            onBackground = Color(0xFF064E3B), // Emerald 900
            onSurface = Color(0xFF064E3B)
        )
        
        val SoftCharcoal = AppThemeConfig(
            primaryBackground = Color(0xFF1E293B), // Slate 800
            secondaryBackground = Color(0xFF334155), // Slate 700
            surfaceColor = Color(0xFF0F172A), // Slate 900
            onBackground = Color(0xFFF8FAFC), // Slate 50
            onSurface = Color(0xFFF8FAFC)
        )
    }
}

object ThemeManager {
    /**
     * Applies the premium background hues to the given ColorScheme.
     */
    fun applyTheme(baseScheme: ColorScheme, config: AppThemeConfig): ColorScheme {
        return baseScheme.copy(
            background = config.primaryBackground,
            surface = config.surfaceColor,
            surfaceVariant = config.secondaryBackground,
            onBackground = config.onBackground,
            onSurface = config.onSurface
        )
    }
}
