package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Exact Theme Colors from Reference Image
val AppLightBg = Color(0xFFF0F2F5) // Premium soft light gray background
val AppLightSurface = Color(0xFFFFFFFF) // Pure white card & sheet container
val AppLightSurfaceElevated = Color(0xFFF8FAFC) // Soft elevated surface
val AppLightBorder = Color(0xFFE2E8F0) // Subtle card border
val AppBlueBorder = Color(0xFF2563EB) // Bright vibrant royal blue card border (as in screenshot)
val AppBlueBorderLight = Color(0xFF3B82F6) // Electric blue highlight

// Typography Colors
val AppTextDark = Color(0xFF0F172A) // Bold dark navy/slate headings
val AppTextSecondary = Color(0xFF475569) // Dark slate for body and subtitles
val AppTextMuted = Color(0xFF64748B) // Medium slate grey for captions

// Vibrant Accents from Screenshot
val AppOrangePrimary = Color(0xFFFF7A00) // Vibrant Energetic Orange (Daily Quiz & CTA)
val AppOrangeLight = Color(0xFFFFEDE5) // Soft orange pill container
val AppOrangeAmber = Color(0xFFF59E0B) // Rich golden amber
val AppOrangeContainer = Color(0xFFFF7A00).copy(alpha = 0.15f)

val AppPurplePrimary = Color(0xFF7C3AED) // Royal Purple ("8 Subjects Available", "⭐ 4 Pro Plans")
val AppPurpleLight = Color(0xFFEDE9FE) // Soft lavender container
val AppPurpleBorderColor = Color(0xFF8B5CF6) // Violet border for Pro Plans button

val AppGreenAccent = Color(0xFF22C55E) // Emerald green support/chat bubble
val AppGreenLight = Color(0xFFDCFCE7)
val AppSkyBlueAccent = Color(0xFF0EA5E9) // Sky blue avatar icon
val AppSkyBlueLight = Color(0xFFE0F2FE)

val AppGoldPrimary = Color(0xFFF59E0B) // Warm Amber Gold ("Explore →", Medal icon)
val AppGoldLight = Color(0xFFFEF3C7) // Champagne Gold pill

// Hero Banner Gradient Colors
val AppHeroGradientStart = Color(0xFFE8EFFF)
val AppHeroGradientMid = Color(0xFFEDE9FE)
val AppHeroGradientEnd = Color(0xFFD6E4FF)

// Backward Compatibility Aliases so all existing screens adopt this exact bright theme
val AppPurpleBg = AppLightBg
val AppPurpleBgDeep = AppLightBg
val AppPurpleCard = AppLightSurface
val AppPurpleCardElevated = AppLightSurfaceElevated
val AppPurpleBorder = AppBlueBorder.copy(alpha = 0.4f)
val AppPurpleAppBar = AppLightSurface
val AppPurpleDrawerBg = AppLightSurface

val PremiumNavyBg = AppLightBg
val PremiumNavyBgDeep = AppLightSurface
val PremiumNavyCard = AppLightSurface
val PremiumNavyCardElevated = AppLightSurfaceElevated
val PremiumNavyBorder = AppBlueBorder
val PremiumNavyAppBar = AppLightSurface

val PremiumHeroGradientStart = AppHeroGradientStart
val PremiumHeroGradientMid = AppHeroGradientMid
val PremiumHeroGradientEnd = AppHeroGradientEnd

val PremiumGoldPrimary = AppOrangePrimary
val PremiumGoldLight = AppGoldPrimary
val PremiumGoldAmber = AppOrangeAmber
val PremiumGoldContainer = AppOrangeContainer

val PremiumEmerald = AppGreenAccent
val PremiumEmeraldLight = Color(0xFF059669)
val PremiumEmeraldCard = AppGreenLight
val PremiumEmeraldBorder = AppGreenAccent.copy(alpha = 0.5f)

val PremiumCyan = AppSkyBlueAccent
val PremiumCyanLight = Color(0xFF0284C7)
val PremiumCyanCard = AppSkyBlueLight

val PremiumRuby = Color(0xFFEF4444)
val PremiumRubyLight = Color(0xFFDC2626)
val PremiumRubyCard = Color(0xFFFEE2E2)

val Slate900 = AppTextDark
val Slate800 = AppTextDark
val Slate700 = AppTextSecondary
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)

val AmberPrimary = AppGoldPrimary
val AmberDark = Color(0xFFD97706)
val AmberLight = AppGoldLight

val Emerald600 = AppGreenAccent
val Emerald50 = AppGreenLight

val Rose600 = Color(0xFFEF4444)
val Rose50 = Color(0xFFFEF2F2)

val SoftBlueBg = Color(0xFFEFF6FF)
val SoftBlueBorder = Color(0xFFBFDBFE)
val ParchmentYellow = Color(0xFFFFFBEB)
val ParchmentBorder = Color(0xFFFDE68A)
val SoftLavender = Color(0xFFF5F3FF)
val QuestionOrangeGradientStart = Color(0xFFFF7A00)
val QuestionOrangeGradientEnd = Color(0xFFFF5200)
