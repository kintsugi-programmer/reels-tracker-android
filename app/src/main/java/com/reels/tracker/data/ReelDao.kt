package com.reels.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class PlatformStat(
    val platform: String,
    val count: Int
)

@Dao
interface ReelDao {
    @Insert
    suspend fun insert(entry: ReelEntry)

    @Query("SELECT COUNT(*) FROM reels WHERE timestamp >= :startOfDay")
    fun getTodayCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM reels WHERE timestamp >= :since")
    fun getCountSince(since: Long): Flow<Int>

    @Query("SELECT platform, COUNT(*) as count FROM reels GROUP BY platform")
    fun getPlatformDistribution(): Flow<List<PlatformStat>>
}
