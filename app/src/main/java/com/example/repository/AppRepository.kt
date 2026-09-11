package com.example.repository

import com.example.db.*
import com.example.model.Question
import com.example.model.TestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(private val db: AppDatabase, private val appId: String) {

    val allBookmarks: Flow<List<Question>> = db.bookmarkDao().getAllBookmarks(appId).map { entities ->
        entities.map { entity ->
            Question(
                id = entity.id,
                subjectId = entity.subjectId,
                unitId = entity.unitId,
                topicId = entity.topicId,
                subtopic = entity.subtopic,
                questionText = entity.questionText,
                options = mapOf(
                    "A" to entity.optionA,
                    "B" to entity.optionB,
                    "C" to entity.optionC,
                    "D" to entity.optionD
                ),
                correctOption = entity.correctOption,
                explanation = entity.explanation
            )
        }
    }

    suspend fun addBookmark(q: Question) {
        val entity = BookmarkEntity(
            id = q.id,
            appId = appId,
            subjectId = q.subjectId,
            unitId = q.unitId,
            topicId = q.topicId,
            subtopic = q.subtopic,
            questionText = q.questionText,
            optionA = q.options["A"] ?: "",
            optionB = q.options["B"] ?: "",
            optionC = q.options["C"] ?: "",
            optionD = q.options["D"] ?: "",
            correctOption = q.correctOption,
            explanation = q.explanation ?: ""
        )
        db.bookmarkDao().insertBookmark(entity)
    }

    suspend fun removeBookmark(questionId: String) {
        db.bookmarkDao().deleteBookmarkById(questionId, appId)
    }

    suspend fun isBookmarked(questionId: String): Boolean {
        return db.bookmarkDao().isBookmarked(questionId, appId)
    }

    suspend fun getBookmarkCount(): Int {
        return db.bookmarkDao().getBookmarkCount(appId)
    }

    val allTestResults: Flow<List<TestResult>> = db.testResultDao().getAllResults(appId).map { list ->
        list.map { entity ->
            TestResult(
                id = entity.id.toString(),
                testName = entity.testTitle,
                totalQuestions = entity.totalQuestions,
                score = entity.correctAnswers,
                percentage = entity.scorePercentage.toInt(),
                accuracy = entity.accuracy,
                date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(entity.timestamp))
            )
        }
    }

    suspend fun saveTestResult(
        testTitle: String,
        totalQuestions: Int,
        correctCount: Int,
        wrongCount: Int,
        unansweredCount: Int,
        percentage: Float,
        accuracyStr: String,
        timeSeconds: Int
    ) {
        db.testResultDao().insertResult(
            TestResultEntity(
                appId = appId,
                testTitle = testTitle,
                totalQuestions = totalQuestions,
                correctAnswers = correctCount,
                wrongAnswers = wrongCount,
                unanswered = unansweredCount,
                scorePercentage = percentage,
                accuracy = accuracyStr,
                timeTakenSeconds = timeSeconds
            )
        )
    }

    val userStreak: Flow<UserStreakEntity?> = db.userStreakDao().getStreak()

    suspend fun updateStreak(streak: UserStreakEntity) {
        db.userStreakDao().updateStreak(streak)
    }
}
