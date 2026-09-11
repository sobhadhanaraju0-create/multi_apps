package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.SampleData
import com.example.db.AppDatabase
import com.example.model.*
import com.example.repository.AppRepository
import com.example.repository.AppConfigRepository
import com.example.ui.components.PremiumScreenSurface
import com.example.ui.screens.*
import com.example.ui.theme.DynamicAppTheme
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val configRepository = remember { AppConfigRepository.getInstance() }
            val appConfig by configRepository.currentAppConfig.collectAsStateWithLifecycle()

            DynamicAppTheme(config = appConfig) {
                PremiumScreenSurface {
                    val context = LocalContext.current
                    val database = remember { AppDatabase.getDatabase(context) }
                    val repository = remember(appConfig.appId) { AppRepository(database, appConfig.appId) }
                    val coroutineScope = rememberCoroutineScope()

                    val testResults by repository.allTestResults.collectAsStateWithLifecycle(initialValue = emptyList())
                    val bookmarkedQuestions by repository.allBookmarks.collectAsStateWithLifecycle(initialValue = emptyList())

                    // Seed initial bookmarks if empty
                    LaunchedEffect(Unit) {
                        if (repository.getBookmarkCount() == 0) {
                            SampleData.sampleQuestions.take(3).forEach { q ->
                                repository.addBookmark(q)
                            }
                        }
                    }

                    val navController = rememberNavController()

                    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
                    var selectedUnit by remember { mutableStateOf<UnitModel?>(null) }
                    var selectedTopic by remember { mutableStateOf<TopicModel?>(null) }
                    var activePracticeQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
                    var activePracticeTitle by remember { mutableStateOf("Practice Arena") }

                    // Test Results State
                    var testScore by remember { mutableDoubleStateOf(0.0) }
                    var testCorrect by remember { mutableIntStateOf(0) }
                    var testWrong by remember { mutableIntStateOf(0) }
                    var testUserAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
                    var testBookmarkedIds by remember { mutableStateOf<Set<String>>(emptySet()) }

                    // Scratchpad State
                    var showScratchpad by remember { mutableStateOf(false) }

                    if (showScratchpad) {
                        ScratchpadDialog(onDismiss = { showScratchpad = false })
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onNavigateToSubject = { subject ->
                                    selectedSubject = subject
                                    navController.navigate("syllabus")
                                },
                                onNavigateToCurrentAffairs = { navController.navigate("current_affairs") },
                                onNavigateToPerformance = { navController.navigate("performance") },
                                onNavigateToLeaderboard = { navController.navigate("leaderboard") },
                                onNavigateToSubscription = { navController.navigate("subscription") },
                                onNavigateToScratchpad = { showScratchpad = true },
                                onNavigateToPrivacy = { navController.navigate("privacy") },
                                onNavigateToMockTests = { navController.navigate("mock_tests") },
                                onNavigateToDailyQuiz = { navController.navigate("daily_quiz") },
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToSavedQuestions = { navController.navigate("saved_questions") }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("syllabus") {
                            val subj = selectedSubject ?: SampleData.defaultSubjects[0]
                            SyllabusExplorerScreen(
                                subject = subj,
                                onBack = { navController.popBackStack() },
                                onSelectTopic = { unit, topic ->
                                    selectedUnit = unit
                                    selectedTopic = topic
                                    navController.navigate("sets")
                                },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onNavigateToCurrentAffairs = { navController.navigate("current_affairs") },
                                onNavigateToMockTests = { navController.navigate("mock_tests") },
                                onNavigateToDailyQuiz = { navController.navigate("daily_quiz") },
                                onNavigateToSavedQuestions = { navController.navigate("saved_questions") },
                                onNavigateToSubscription = { navController.navigate("subscription") },
                                onNavigateToPerformance = { navController.navigate("performance") },
                                onNavigateToLeaderboard = { navController.navigate("leaderboard") },
                                onNavigateToProfile = { navController.navigate("profile") }
                            )
                        }

                        composable("sets") {
                            val subj = selectedSubject ?: SampleData.defaultSubjects[0]
                            val u = selectedUnit ?: subj.units.firstOrNull() ?: UnitModel(id = "unit-1", name = "Unit 1")
                            val t = selectedTopic ?: u.topics.firstOrNull() ?: TopicModel(id = "topic-1", name = "Topic 1")
                            TopicSetsScreen(
                                subject = subj,
                                unit = u,
                                topic = t,
                                onBack = { navController.popBackStack() },
                                onStartSet = { pSet ->
                                    activePracticeQuestions = pSet.questions.ifEmpty {
                                        SampleData.getQuestionsForPractice(subj.id, u.id, t.id, "")
                                    }
                                    activePracticeTitle = "${t.name} • ${pSet.title}"
                                    navController.navigate("practice")
                                },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onNavigateToCurrentAffairs = { navController.navigate("current_affairs") },
                                onNavigateToMockTests = { navController.navigate("mock_tests") },
                                onNavigateToDailyQuiz = { navController.navigate("daily_quiz") },
                                onNavigateToSavedQuestions = { navController.navigate("saved_questions") },
                                onNavigateToSubscription = { navController.navigate("subscription") },
                                onNavigateToProfile = { navController.navigate("profile") }
                            )
                        }

                        composable("practice") {
                            PracticeArenaScreen(
                                questions = activePracticeQuestions,
                                title = activePracticeTitle,
                                onBack = { navController.popBackStack() },
                                initialBookmarkedIds = bookmarkedQuestions.map { it.id }.toSet(),
                                onSubmitTest = { correct, wrong, score, userAnswers, bookmarkedIds ->
                                    testCorrect = correct
                                    testWrong = wrong
                                    testScore = score
                                    testUserAnswers = userAnswers
                                    testBookmarkedIds = bookmarkedIds
                                    coroutineScope.launch {
                                        val total = activePracticeQuestions.size
                                        val unanswered = (total - (correct + wrong)).coerceAtLeast(0)
                                        val pct = if (total > 0) ((correct * 100f) / total) else 0f
                                        val accuracy = if (correct + wrong > 0) "${((correct * 100) / (correct + wrong))}%" else "0%"
                                        repository.saveTestResult(
                                            testTitle = activePracticeTitle,
                                            totalQuestions = total,
                                            correctCount = correct,
                                            wrongCount = wrong,
                                            unansweredCount = unanswered,
                                            percentage = pct,
                                            accuracyStr = accuracy,
                                            timeSeconds = 180
                                        )
                                    }
                                    navController.navigate("result") {
                                        popUpTo("practice") { inclusive = true }
                                    }
                                },
                                onToggleBookmark = { question ->
                                    coroutineScope.launch {
                                        if (repository.isBookmarked(question.id)) {
                                            repository.removeBookmark(question.id)
                                        } else {
                                            repository.addBookmark(question)
                                        }
                                    }
                                }
                            )
                        }

                        composable("result") {
                            ResultScreen(
                                questions = activePracticeQuestions,
                                userAnswers = testUserAnswers,
                                bookmarkedIds = testBookmarkedIds,
                                correctCount = testCorrect,
                                wrongCount = testWrong,
                                score = testScore,
                                onPracticeAgain = {
                                    navController.navigate("practice")
                                },
                                onBackToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onToggleBookmark = { question ->
                                    coroutineScope.launch {
                                        if (repository.isBookmarked(question.id)) {
                                            repository.removeBookmark(question.id)
                                        } else {
                                            repository.addBookmark(question)
                                        }
                                    }
                                }
                            )
                        }

                        composable("saved_questions") {
                            SavedQuestionsScreen(
                                savedQuestions = bookmarkedQuestions,
                                onBack = { navController.popBackStack() },
                                onRemoveBookmark = { questionId ->
                                    coroutineScope.launch {
                                        repository.removeBookmark(questionId)
                                    }
                                },
                                onPracticeSaved = { questions, title ->
                                    activePracticeQuestions = questions
                                    activePracticeTitle = title
                                    navController.navigate("practice")
                                },
                                onNavigateToExplore = {
                                    navController.navigate("home")
                                }
                            )
                        }

                        composable("current_affairs") {
                            CurrentAffairsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("performance") {
                            PerformanceBoardScreen(
                                results = testResults,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("leaderboard") {
                            LeaderboardScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("subscription") {
                            SubscriptionScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("privacy") {
                            PrivacyTermsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("mock_tests") {
                            MockTestSeriesScreen(
                                onBack = { navController.popBackStack() },
                                onStartMockTest = { mock ->
                                    activePracticeQuestions = SampleData.sampleQuestions
                                    activePracticeTitle = mock.title
                                    navController.navigate("practice")
                                },
                                onNavigateToSubscription = {
                                    navController.navigate("subscription")
                                }
                            )
                        }

                        composable("daily_quiz") {
                            DailyQuizScreen(
                                onBack = { navController.popBackStack() },
                                onStartDailyQuiz = { questions ->
                                    activePracticeQuestions = questions
                                    activePracticeTitle = "Daily Quiz Challenge Sprint"
                                    navController.navigate("practice")
                                }
                            )
                        }

                        composable("profile") {
                            UserProfileScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToSubscription = {
                                    navController.navigate("subscription")
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                },
                                onNavigateToSavedQuestions = {
                                    navController.navigate("saved_questions")
                                }
                            )
                        }

                        composable("admin") {
                            AdminDashboardScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
