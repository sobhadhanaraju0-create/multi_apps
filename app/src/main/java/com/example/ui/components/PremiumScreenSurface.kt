package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppThemeConfig

/**
 * A custom Surface wrapper that uses dynamic color properties from AppThemeConfig.
 * Ensures the background color applies cleanly to the screen container without
 * bleeding into content-heavy areas like images or cards, which rely on the surface color.
 */
@Composable
fun PremiumScreenSurface(
    modifier: Modifier = Modifier,
    config: AppThemeConfig? = null,
    content: @Composable () -> Unit
) {
    // Uses the provided config's background or falls back to the globally applied material theme background
    val bgColor = config?.primaryBackground ?: MaterialTheme.colorScheme.background
    val fgColor = config?.onBackground ?: MaterialTheme.colorScheme.onBackground

    Surface(
        modifier = modifier.fillMaxSize(),
        color = bgColor,
        contentColor = fgColor
    ) {
        content()
    }
}
