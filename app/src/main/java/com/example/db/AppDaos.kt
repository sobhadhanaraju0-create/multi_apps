package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE appId = :appId ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(appId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :questionId AND appId = :appId")
    suspend fun deleteBookmarkById(questionId: String, appId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :questionId AND appId = :appId)")
    suspend fun isBookmarked(questionId: String, appId: String): Boolean

    @Query("SELECT COUNT(*) FROM bookmarks WHERE appId = :appId")
    suspend fun getBookmarkCount(appId: String): Int
}

@Dao
interface TestResultDao {
    @Query("SELECT * FROM test_results WHERE appId = :appId ORDER BY timestamp DESC")
    fun getAllResults(appId: String): Flow<List<TestResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResultEntity)

    @Query("SELECT COUNT(*) FROM test_results WHERE appId = :appId")
    fun getTestCount(appId: String): Flow<Int>

    @Query("DELETE FROM test_results WHERE appId = :appId")
    suspend fun clearHistory(appId: String)
}

@Dao
interface UserStreakDao {
    @Query("SELECT * FROM user_streak WHERE id = 1")
    fun getStreak(): Flow<UserStreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStreak(streak: UserStreakEntity)
}
