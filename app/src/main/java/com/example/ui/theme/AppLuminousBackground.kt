package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

// Exact Background Color Palette extracted from the User's Uploaded Wallpaper Image
val LuminousCreamTop = Color(0xFFFFFDF9)
val LuminousCreamCenter = Color(0xFFFFF7ED)
val LuminousTealTopLeft = Color(0xFF7EE8FA)
val LuminousCyanGlow = Color(0xFF38BDF8)
val LuminousOceanBlue = Color(0xFF0284C7)
val LuminousDeepBlue = Color(0xFF0066FF)
val LuminousGoldenAccent = Color(0xFFFFB703)
val LuminousAmberGlow = Color(0xFFFF8A00)

/**
 * App-wide dynamic glowing wallpaper background matching the user's uploaded image.
 * Perfectly reproduces the luminous teal, cyan, and ocean blue waves, golden-amber glowing edge lines,
 * and warm cream-off-white center from the reference image.
 * 
 * Sits safely beneath all UI components, ensuring that subject images, posters,
 * unit images, topic images, and subscription amount cards are preserved with 100% clarity.
 */
@Composable
fun AppLuminousBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LuminousCreamTop,
                        LuminousCreamCenter,
                        Color(0xFFE0F2FE),
                        Color(0xFFCCFBF1)
                    )
                )
            )
    ) {
        // High-fidelity vector Canvas rendering the glowing cyan/teal/blue waves & golden curves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. TOP-LEFT TEAL/CYAN RADIAL GLOW
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LuminousTealTopLeft.copy(alpha = 0.65f),
                        LuminousCyanGlow.copy(alpha = 0.45f),
                        LuminousGoldenAccent.copy(alpha = 0.30f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.05f, height * 0.02f),
                    radius = width * 0.80f
                ),
                radius = width * 0.80f,
                center = Offset(width * 0.05f, height * 0.02f)
            )

            // 2. BOTTOM-RIGHT OCEAN BLUE & CYAN RADIAL GLOW
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LuminousOceanBlue.copy(alpha = 0.55f),
                        LuminousDeepBlue.copy(alpha = 0.40f),
                        LuminousCyanGlow.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.95f, height * 0.88f),
                    radius = width * 0.95f
                ),
                radius = width * 0.95f,
                center = Offset(width * 0.95f, height * 0.88f)
            )

            // 3. UPPER LEFT TEAL/GOLD WAVE
            val upperWavePath = Path().apply {
                moveTo(-50f, height * 0.35f)
                cubicTo(
                    width * 0.22f, height * 0.24f,
                    width * 0.35f, height * 0.12f,
                    width * 0.42f, -20f
                )
                lineTo(-50f, -20f)
                close()
            }
            drawPath(
                path = upperWavePath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        LuminousTealTopLeft.copy(alpha = 0.40f),
                        LuminousCyanGlow.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(width * 0.42f, height * 0.35f)
                ),
                style = Fill
            )

            // Upper Wave Golden Edge Stroke
            val upperWaveEdge = Path().apply {
                moveTo(-50f, height * 0.35f)
                cubicTo(
                    width * 0.22f, height * 0.24f,
                    width * 0.35f, height * 0.12f,
                    width * 0.42f, -20f
                )
            }
            drawPath(
                path = upperWaveEdge,
                brush = Brush.linearGradient(
                    colors = listOf(
                        LuminousGoldenAccent,
                        LuminousAmberGlow,
                        LuminousTealTopLeft
                    ),
                    start = Offset(-50f, height * 0.35f),
                    end = Offset(width * 0.42f, -20f)
                ),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // 4. BOTTOM-RIGHT SWEEPING OCEAN BLUE WAVES
            val midWavePath = Path().apply {
                moveTo(-50f, height * 0.68f)
                cubicTo(
                    width * 0.18f, height * 0.52f,
                    width * 0.45f, height * 0.48f,
                    width * 0.78f, height * 0.62f
                )
                cubicTo(
                    width * 0.92f, height * 0.68f,
                    width * 0.98f, height * 0.76f,
                    width + 50f, height * 0.84f
                )
                lineTo(width + 50f, height + 50f)
                lineTo(-50f, height + 50f)
                close()
            }
            drawPath(
                path = midWavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LuminousCyanGlow.copy(alpha = 0.20f),
                        LuminousOceanBlue.copy(alpha = 0.35f),
                        LuminousDeepBlue.copy(alpha = 0.50f)
                    ),
                    startY = height * 0.48f,
                    endY = height
                ),
                style = Fill
            )

            // Glowing Golden/Cyan Edge Stroke for the lower wave
            val midWaveEdge = Path().apply {
                moveTo(-50f, height * 0.68f)
                cubicTo(
                    width * 0.18f, height * 0.52f,
                    width * 0.45f, height * 0.48f,
                    width * 0.78f, height * 0.62f
                )
                cubicTo(
                    width * 0.92f, height * 0.68f,
                    width * 0.98f, height * 0.76f,
                    width + 50f, height * 0.84f
                )
            }
            drawPath(
                path = midWaveEdge,
                brush = Brush.linearGradient(
                    colors = listOf(
                        LuminousGoldenAccent,
                        LuminousTealTopLeft,
                        LuminousCyanGlow,
                        LuminousOceanBlue
                    ),
                    start = Offset(-50f, height * 0.68f),
                    end = Offset(width + 50f, height * 0.84f)
                ),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }

        // Child content (Scaffold, LazyColumn, cards, images, dialogs)
        content()
    }
}
