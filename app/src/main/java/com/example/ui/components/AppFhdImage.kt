package com.example.ui.components

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.model.MockTestModel
import com.example.model.PromotionalPosterModel
import com.example.model.Subject
import com.example.model.TopicModel
import com.example.model.UnitModel

/**
 * Universal FHD Image Engine for APPSC Prep Platform
 * Guarantees zero blank images, high-speed cached loading, crossfade transitions,
 * and high-fidelity thematic vector artworks for Subjects, Units, Topics, and Posters.
 */

@Composable
fun AppFhdImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    themeCategory: String = "general",
    title: String = "",
    subtitle: String = "",
    tagBadge: String? = null
) {
    val context = LocalContext.current
    val cleanUrl = remember(imageUrl) {
        imageUrl?.trim()?.takeIf { it.isNotEmpty() }
    }

    val imageRequest = remember(cleanUrl) {
        if (cleanUrl != null) {
            ImageRequest.Builder(context)
                .data(cleanUrl)
                .crossfade(true)
                .allowHardware(false)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .listener(
                    onError = { _, result ->
                        Log.w("AppFhdImage", "Failed to load image from URL ($cleanUrl): ${result.throwable.message}")
                    }
                )
                .build()
        } else null
    }

    if (imageRequest != null) {
        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            loading = {
                FhdImageShimmer(modifier = Modifier.fillMaxSize())
            },
            error = {
                FhdThematicArtwork(
                    category = themeCategory,
                    title = title,
                    subtitle = subtitle,
                    tagBadge = tagBadge,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    } else {
        FhdThematicArtwork(
            category = themeCategory,
            title = title,
            subtitle = subtitle,
            tagBadge = tagBadge,
            modifier = modifier
        )
    }
}

/**
 * Shimmer Loading Placeholder
 */
@Composable
fun FhdImageShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFF1E293B),
        Color(0xFF334155),
        Color(0xFF1E293B)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Loading image",
            tint = Color.White.copy(alpha = 0.25f),
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Rich Thematic Vector Graphic Fallback for FHD Displays
 * Renders clean thematic background art without internal text overlays,
 * leaving text rendering cleanly to enclosing card UI elements.
 */
@Composable
fun FhdThematicArtwork(
    category: String,
    title: String,
    subtitle: String,
    tagBadge: String?,
    modifier: Modifier = Modifier
) {
    val theme = resolveThemePalette(category, title)

    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = theme.gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .fillMaxSize()
    ) {
        // Decorative geometric mesh on canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Abstract ambient glow circles
            drawCircle(
                color = theme.accentGlow.copy(alpha = 0.22f),
                radius = width * 0.45f,
                center = Offset(width * 0.85f, height * 0.15f)
            )
            drawCircle(
                color = theme.secondaryGlow.copy(alpha = 0.18f),
                radius = width * 0.35f,
                center = Offset(width * 0.15f, height * 0.85f)
            )

            // Dynamic diagonal light streak
            val path = Path().apply {
                moveTo(0f, height * 0.65f)
                cubicTo(
                    width * 0.35f, height * 0.35f,
                    width * 0.65f, height * 0.85f,
                    width, height * 0.2f
                )
            }
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.12f),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Clean Thematic Emblem Watermark
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Icon Watermark in background right
            Icon(
                imageVector = theme.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.20f),
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 12.dp)
            )

            // Centered subtle icon badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = theme.icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private data class ThematicStyle(
    val gradientColors: List<Color>,
    val accentGlow: Color,
    val secondaryGlow: Color,
    val icon: ImageVector
)

private fun resolveThemePalette(category: String, title: String): ThematicStyle {
    val lower = "$category $title".lowercase()
    return when {
        lower.contains("history") || lower.contains("ancient") || lower.contains("vedic") || lower.contains("indus") || lower.contains("buddhism") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF831843), Color(0xFF9D174D), Color(0xFF500724)),
                accentGlow = Color(0xFFF59E0B),
                secondaryGlow = Color(0xFFEC4899),
                icon = Icons.Default.AccountBalance
            )
        }
        lower.contains("medieval") || lower.contains("sultanate") || lower.contains("mughal") || lower.contains("monument") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF9A3412), Color(0xFFC2410C), Color(0xFF431407)),
                accentGlow = Color(0xFFFBBF24),
                secondaryGlow = Color(0xFFEA580C),
                icon = Icons.Default.Fort
            )
        }
        lower.contains("modern") || lower.contains("freedom") || lower.contains("1857") || lower.contains("movement") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8), Color(0xFF0F172A)),
                accentGlow = Color(0xFF38BDF8),
                secondaryGlow = Color(0xFF60A5FA),
                icon = Icons.Default.Flag
            )
        }
        lower.contains("geo") || lower.contains("climate") || lower.contains("river") || lower.contains("earth") || lower.contains("physical") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF022C22)),
                accentGlow = Color(0xFF34D399),
                secondaryGlow = Color(0xFF10B981),
                icon = Icons.Default.Public
            )
        }
        lower.contains("polity") || lower.contains("constitution") || lower.contains("article") || lower.contains("governance") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF312E81), Color(0xFF4338CA), Color(0xFF1E1B4B)),
                accentGlow = Color(0xFFA78BFA),
                secondaryGlow = Color(0xFF818CF8),
                icon = Icons.Default.Gavel
            )
        }
        lower.contains("economy") || lower.contains("finance") || lower.contains("budget") || lower.contains("planning") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF14532D), Color(0xFF15803D), Color(0xFF052E16)),
                accentGlow = Color(0xFF4ADE80),
                secondaryGlow = Color(0xFF22C55E),
                icon = Icons.Default.TrendingUp
            )
        }
        lower.contains("science") || lower.contains("tech") || lower.contains("space") || lower.contains("isro") || lower.contains("it") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF0E7490), Color(0xFF0891B2), Color(0xFF164E63)),
                accentGlow = Color(0xFF22D3EE),
                secondaryGlow = Color(0xFF06B6D4),
                icon = Icons.Default.Biotech
            )
        }
        lower.contains("society") || lower.contains("welfare") || lower.contains("tribe") || lower.contains("caste") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF701A75), Color(0xFF86198F), Color(0xFF4A044E)),
                accentGlow = Color(0xFFE879F9),
                secondaryGlow = Color(0xFFC026D3),
                icon = Icons.Default.Groups
            )
        }
        lower.contains("affair") || lower.contains("current") || lower.contains("news") || lower.contains("daily") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFFBE123C), Color(0xFFE11D48), Color(0xFF4C0519)),
                accentGlow = Color(0xFFFB7185),
                secondaryGlow = Color(0xFFFDA4AF),
                icon = Icons.Default.Bolt
            )
        }
        lower.contains("mock") || lower.contains("test") || lower.contains("grand") || lower.contains("series") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFF3730A3), Color(0xFF0F172A)),
                accentGlow = Color(0xFFF59E0B),
                secondaryGlow = Color(0xFF818CF8),
                icon = Icons.Default.EmojiEvents
            )
        }
        lower.contains("pro") || lower.contains("plan") || lower.contains("vip") || lower.contains("subscription") || lower.contains("poster") -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFF1C1917)),
                accentGlow = Color(0xFFFBBF24),
                secondaryGlow = Color(0xFFF59E0B),
                icon = Icons.Default.WorkspacePremium
            )
        }
        else -> {
            ThematicStyle(
                gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617)),
                accentGlow = Color(0xFF38BDF8),
                secondaryGlow = Color(0xFF64748B),
                icon = Icons.Default.MenuBook
            )
        }
    }
}

/**
 * High-Convenience Composable for Subject Cards
 */
@Composable
fun SubjectFhdCardImage(
    subject: Subject,
    modifier: Modifier = Modifier
) {
    AppFhdImage(
        imageUrl = subject.imageUrl,
        contentDescription = subject.name,
        modifier = modifier,
        themeCategory = subject.name,
        title = subject.name,
        subtitle = subject.heroQuote.replace("\n", " • ")
    )
}

/**
 * High-Convenience Composable for Unit Cards & Unit Hero Headers
 */
@Composable
fun UnitFhdCardImage(
    unit: UnitModel,
    subjectName: String = "",
    modifier: Modifier = Modifier
) {
    AppFhdImage(
        imageUrl = unit.imageUrl,
        contentDescription = unit.name,
        modifier = modifier,
        themeCategory = "$subjectName ${unit.name}",
        title = unit.name,
        subtitle = unit.subtitle ?: unit.tagline ?: "",
        tagBadge = "Unit ${unit.unitNumber}"
    )
}

/**
 * High-Convenience Composable for Topic Cards
 */
@Composable
fun TopicFhdCardImage(
    topic: TopicModel,
    modifier: Modifier = Modifier
) {
    AppFhdImage(
        imageUrl = topic.imageUrl,
        contentDescription = topic.name,
        modifier = modifier,
        themeCategory = "${topic.tagBadge} ${topic.name}",
        title = topic.name,
        subtitle = topic.subtitle ?: "",
        tagBadge = topic.tagBadge
    )
}

/**
 * High-Convenience Composable for Promotional Posters
 */
@Composable
fun PosterFhdCardImage(
    poster: PromotionalPosterModel,
    modifier: Modifier = Modifier
) {
    AppFhdImage(
        imageUrl = poster.imageUrl,
        contentDescription = poster.title,
        modifier = modifier,
        themeCategory = "poster ${poster.title}",
        title = poster.title,
        subtitle = poster.promotionalText,
        tagBadge = poster.posterType
    )
}

/**
 * High-Convenience Composable for Mock Test Cards
 */
@Composable
fun MockTestFhdCardImage(
    mockTest: MockTestModel,
    modifier: Modifier = Modifier
) {
    AppFhdImage(
        imageUrl = "",
        contentDescription = mockTest.title,
        modifier = modifier,
        themeCategory = "mock test ${mockTest.paperType}",
        title = mockTest.title,
        subtitle = "${mockTest.totalQuestions} Questions • ${mockTest.durationMinutes} Mins",
        tagBadge = mockTest.difficulty
    )
}
