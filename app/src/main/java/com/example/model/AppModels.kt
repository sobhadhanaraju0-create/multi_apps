package com.example.model

/**
 * Platform Multi-App Tenant Model
 * Single codebase powers multiple applications (e.g. APPSC Group 1, Group 2, SSC, Banking, etc.)
 */
data class AppTenantModel(
    val id: String,
    val name: String,
    val code: String,
    val examCategory: String, // "State PSC", "Central SSC", "Banking & Insurance", "Teaching (DSC/TET)", "UPSC Civil Services", "Railway RRB"
    val tagline: String,
    val iconName: String = "AccountBalance",
    val primaryColorHex: Long = 0xFF2563EB,
    val secondaryColorHex: Long = 0xFF1E40AF,
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val isActive: Boolean = true,
    val assignedAdminEmail: String = "",
    val assignedAdminName: String = "",
    val allowedLanguages: List<String> = listOf("Telugu", "English"),
    val featuresEnabled: List<String> = listOf("MCQs", "Current Affairs", "Monthly PDFs", "Posters", "Subscriptions", "Daily Quiz", "Mock Tests"),
    val createdAt: String = "2026-09-01",
    val updatedAt: String = "2026-09-10",
    val description: String = ""
)

data class Subject(
    val id: String,
    val appId: String = "appsc-group2",
    val name: String,
    val category: String = "prelims", // "prelims", "mains", "both"
    val icon: String = "BookOpen",
    val imageUrl: String = "",
    val heroQuote: String = "Explore the Past\nBuild a Better Future",
    val color: String = "from-amber-600 to-orange-700",
    val units: List<UnitModel> = emptyList()
)

data class UnitModel(
    val id: String,
    val appId: String = "appsc-group2",
    val name: String,
    val unitNumber: Int = 1,
    val subtitle: String? = null,
    val tagline: String? = null,
    val quote: String? = null,
    val slogan: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val estimatedMcqs: String? = null,
    val completionPercentage: Int? = null,
    val topics: List<TopicModel> = emptyList()
)

data class TopicModel(
    val id: String,
    val appId: String = "appsc-group2",
    val name: String,
    val subtitle: String? = null,
    val tagBadge: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val estimatedMcqs: String? = null,
    val practiceSetsCount: Int = 3,
    val difficulty: String = "Medium",
    val completionPercentage: Int = 0,
    val notesContent: String? = null,
    val subtopics: List<String> = emptyList()
)

data class PracticeSetModel(
    val id: String,
    val setNumber: Int = 1,
    val title: String = "Set 1",
    val subtitle: String = "10 Qs • Q1 – Q10",
    val questionsCount: Int = 10,
    val startQuestionNumber: Int = 1,
    val endQuestionNumber: Int = 10,
    val status: String = "Not Started", // "Completed", "In Progress", "Not Started"
    val isCompleted: Boolean = false,
    val durationMinutes: Int = 10,
    val totalMarks: Int = 10,
    val negativeMarks: Double = 0.33,
    val difficulty: String = "Medium",
    val badge: String = "High Yield",
    val bestScore: Int? = null,
    val questions: List<Question> = emptyList()
)

data class Question(
    val id: String,
    val appId: String = "appsc-group2",
    val subjectId: String,
    val unitId: String = "",
    val topicId: String = "",
    val subtopic: String = "",
    val questionText: String,
    val options: Map<String, String>, // "A" -> text, "B" -> text, etc.
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String? = null,
    val setName: String = "",
    val difficulty: String = "Medium",
    val tags: List<String> = emptyList(),
    val language: String = "English / Telugu",
    val imageUrl: String? = null,
    val year: String? = null,
    val exam: String? = null,
    val marks: Double = 1.0,
    val negativeMarks: Double = 0.33,
    val customFields: Map<String, String> = emptyMap(),
    val importBatchId: String? = null
)

data class ImportBatchRecord(
    val id: String,
    val appId: String = "appsc-group2",
    val fileName: String,
    val adminEmail: String = "sobhadhanaraju0@gmail.com",
    val timestamp: String,
    val destinationPath: String,
    val totalFound: Int,
    val importedCount: Int,
    val duplicateCount: Int,
    val errorCount: Int,
    val importedQuestionIds: List<String>,
    val status: String = "Completed" // "Completed", "Rolled Back"
)

data class CurrentAffair(
    val id: String,
    val appId: String = "appsc-group2",
    val mappedAppIds: List<String> = listOf("appsc-group2"), // Supports Super Admin Multi-App Sharing
    val month: String,
    val category: String = "AP Current Affairs", // "AP Current Affairs", "National", "International", "Economy", "Polity", "Science & Technology", "Environment", "Sports", "Schemes", "Defence", "Reports & Indexes"
    val title: String,
    val content: String,
    val sourceUrl: String? = null,
    val imageUrl: String? = null,
    val explanation: String? = null,
    val publishedDate: String,
    val status: String = "PUBLISHED", // "PUBLISHED", "DRAFT", "SCHEDULED"
    val isSharedMultiApp: Boolean = false,
    val attachedMcqs: List<Question> = emptyList()
)

data class MonthlyCurrentAffairsPdf(
    val id: String,
    val appId: String = "appsc-group2",
    val mappedAppIds: List<String> = listOf("appsc-group2"), // Supports Super Admin Multi-App Sharing
    val month: String,
    val year: String = "2026",
    val title: String,
    val pdfUrl: String,
    val fileSizeBytes: String = "4.2 MB",
    val pagesCount: Int = 48,
    val language: String = "Telugu & English",
    val description: String = "",
    val coverImageUrl: String = "",
    val isProOnly: Boolean = false,
    val publishDate: String = "2026-09-01",
    val status: String = "PUBLISHED", // "PUBLISHED", "DRAFT", "SCHEDULED"
    val downloadCount: Int = 1240,
    val isSharedMultiApp: Boolean = false
)

data class SubscriptionPackage(
    val id: String,
    val appId: String = "appsc-group2",
    val name: String,
    val price: Double,
    val currencySymbol: String = "₹",
    val billingCycle: String = "Monthly",
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val score: String,
    val accuracy: String,
    val mockTests: Int,
    val badge: String,
    val isUser: Boolean = false,
    val avatarUrl: String = ""
)

data class TestAttemptHistory(
    val id: String,
    val title: String,
    val scoreText: String,
    val accuracy: String,
    val date: String
)

data class TestResult(
    val id: String,
    val testName: String,
    val totalQuestions: Int,
    val score: Int,
    val percentage: Int,
    val accuracy: String,
    val date: String
)

data class PyqPaper(
    val id: String,
    val appId: String = "appsc-group2",
    val year: String,
    val title: String,
    val examStage: String, // "Prelims", "Mains Paper 1", etc.
    val totalQuestions: Int,
    val durationMinutes: Int,
    val description: String,
    val questions: List<Question> = emptyList()
)

data class MockTestModel(
    val id: String,
    val appId: String = "appsc-group2",
    val title: String,
    val paperType: String, // "Paper I - GS", "Paper II - AP History & Polity", etc.
    val durationMinutes: Int,
    val totalQuestions: Int,
    val totalMarks: Int,
    val difficulty: String, // "Easy", "Medium", "Hard", "Exam Standard"
    val description: String,
    val isFree: Boolean = true
)

data class StudyNoteModel(
    val id: String,
    val appId: String = "appsc-group2",
    val subjectId: String,
    val title: String,
    val category: String, // "AP Schemes", "History Table", "Act Summary", "Econ Data"
    val readTime: String,
    val content: String,
    val highlights: List<String> = emptyList()
)

data class UserProfileModel(
    val name: String,
    val email: String,
    val phone: String,
    val targetExam: String,
    val targetAppId: String = "appsc-group2",
    val preferredLanguage: String,
    val dailyGoalQuestions: Int,
    val streakDays: Int,
    val isProSubscribed: Boolean,
    val proPlanName: String
)

enum class CouponDiscountType {
    PERCENTAGE, FIXED
}

enum class PromotionStatus {
    ACTIVE, SCHEDULED, EXPIRED, DISABLED
}

data class CouponModel(
    val id: String,
    val appId: String = "appsc-group2",
    val mappedAppIds: List<String> = listOf("appsc-group2"),
    val code: String,
    val description: String,
    val discountType: CouponDiscountType = CouponDiscountType.PERCENTAGE,
    val discountValue: Double,
    val minPurchaseAmount: Double = 0.0,
    val startDate: String,
    val startTime: String = "00:00",
    val expiryDate: String,
    val expiryTime: String = "23:59",
    val isActive: Boolean = true,
    val usageLimitTotal: Int = -1,
    val usageLimitPerUser: Int = 1,
    val timesUsed: Int = 0,
    val restrictedPlanIds: List<String> = emptyList(),
    val restrictedUserGroups: List<String> = listOf("All Users"),
    val festivalTag: String = "General Offer"
) {
    fun getStatus(currentDateStr: String = "2026-09-10", currentTimeStr: String = "11:05"): PromotionStatus {
        if (!isActive) return PromotionStatus.DISABLED
        if (usageLimitTotal > 0 && timesUsed >= usageLimitTotal) return PromotionStatus.EXPIRED
        if (currentDateStr < startDate || (currentDateStr == startDate && currentTimeStr < startTime)) {
            return PromotionStatus.SCHEDULED
        }
        if (currentDateStr > expiryDate || (currentDateStr == expiryDate && currentTimeStr > expiryTime)) {
            return PromotionStatus.EXPIRED
        }
        return PromotionStatus.ACTIVE
    }

    fun calculateDiscount(cartAmount: Double): Double {
        if (cartAmount < minPurchaseAmount) return 0.0
        return when (discountType) {
            CouponDiscountType.PERCENTAGE -> (cartAmount * (discountValue / 100.0)).coerceAtMost(cartAmount)
            CouponDiscountType.FIXED -> discountValue.coerceAtMost(cartAmount)
        }
    }
}

data class PromotionalPosterModel(
    val id: String,
    val appId: String = "appsc-group2",
    val mappedAppIds: List<String> = listOf("appsc-group2"), // Super Admin Multi-App Sharing
    val posterType: String = "Home Screen Poster", // "Home Screen Poster", "Current Affairs Poster", "Subscription Poster", "Exam Update", "Festival Poster", "Announcement", "Promotional Banner", "Study Material Banner"
    val title: String,
    val imageUrl: String,
    val promotionalText: String,
    val ctaText: String = "Claim Offer",
    val offerDetails: String = "",
    val destinationPage: String = "subscription",
    val targetPages: List<String> = listOf("Home Screen", "Subscription Screen"),
    val targetPlans: List<String> = listOf("All Plans"),
    val targetUserTypes: List<String> = listOf("All Users"),
    val startDate: String = "2026-09-01",
    val startTime: String = "00:00",
    val endDate: String = "2026-12-31",
    val endTime: String = "23:59",
    val priorityOrder: Int = 1,
    val isActive: Boolean = true,
    val isSharedMultiApp: Boolean = false
) {
    fun getStatus(currentDateStr: String = "2026-09-10", currentTimeStr: String = "11:05"): PromotionStatus {
        if (!isActive) return PromotionStatus.DISABLED
        if (currentDateStr < startDate || (currentDateStr == startDate && currentTimeStr < startTime)) {
            return PromotionStatus.SCHEDULED
        }
        if (currentDateStr > endDate || (currentDateStr == endDate && currentTimeStr > endTime)) {
            return PromotionStatus.EXPIRED
        }
        return PromotionStatus.ACTIVE
    }
}

data class ScheduledPriceChange(
    val newPrice: Double,
    val effectiveDate: String,
    val effectiveTime: String = "00:00",
    val note: String = ""
)

data class SubscriptionPlanModel(
    val id: String,
    val appId: String = "appsc-group2",
    val name: String,
    val price: Double,
    val promotionalPrice: Double? = null,
    val currencySymbol: String = "₹",
    val duration: String = "Monthly",
    val durationDays: Int = 30,
    val features: List<String> = emptyList(),
    val benefits: String = "",
    val introductoryOfferText: String? = null,
    val linkedCouponCodes: List<String> = emptyList(),
    val visibleToUserGroups: List<String> = listOf("All Users"),
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val scheduledPriceChange: ScheduledPriceChange? = null,
    val activeSubscribersCount: Int = 120,
    val renewalsCount: Int = 45,
    val cancellationsCount: Int = 3,
    val totalRevenueGenerated: Double = 95880.0
)

data class AdminAuditLogModel(
    val id: String,
    val timestamp: String,
    val adminId: String = "SA-001",
    val adminEmail: String,
    val adminName: String = "Sobha Dhanaraju (Super Admin)",
    val adminRole: String = "SUPER_ADMIN",
    val appId: String = "appsc-group2",
    val appName: String = "APPSC Group 2",
    val actionType: String,
    val details: String,
    val recordCount: Int = 1,
    val category: String,
    val status: String = "SUCCESS" // "SUCCESS", "FAILED", "WARNING"
)

data class UserAccountRecord(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val targetApp: String = "APPSC Group 2",
    val targetAppId: String = "appsc-group2",
    val isProSubscribed: Boolean = false,
    val proPlanName: String = "Free Tier",
    val subscriptionExpiry: String = "N/A",
    val grantedByAdmin: String = ""
)

enum class AdminRole {
    SUPER_ADMIN, CONTENT_ADMIN, PROMOTION_ADMIN, CONTENT_MANAGER, READ_ONLY
}

data class AdminUserAccount(
    val id: String,
    val email: String,
    val name: String,
    val role: AdminRole,
    val assignedAppId: String? = null, // null for SUPER_ADMIN (global), "appsc-group2" for Content Admin
    val assignedAppName: String? = null,
    val status: String = "ACTIVE", // "ACTIVE", "DISABLED", "PENDING_RESET"
    val lastLogin: String = "2026-09-10 11:30",
    val allowedPermissions: List<String> = listOf(
        "SUBJECT_MANAGEMENT",
        "JSON_UPLOAD",
        "CURRENT_AFFAIRS_MANAGEMENT",
        "POSTER_MANAGEMENT"
    )
)
