package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val appId: String = "appsc-group2",
    val subjectId: String,
    val unitId: String = "",
    val topicId: String = "",
    val subtopic: String = "",
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String = "",
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: String = "appsc-group2",
    val testTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val unanswered: Int,
    val scorePercentage: Float,
    val accuracy: String,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_streak")
data class UserStreakEntity(
    @PrimaryKey val id: Int = 1,
    val streakDays: Int = 14,
    val totalXp: Int = 1420,
    val dailyTargetQuestions: Int = 30,
    val lastActiveDate: String = "2026-09-09"
)
