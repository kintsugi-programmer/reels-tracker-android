package com.reels.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Insert
    suspend fun insert(entry: ReelEntry)

    @Query("SELECT COUNT(*) FROM reels WHERE timestamp >= :startOfDay")
    fun getTodayCount(startOfDay: Long): Flow<Int>
}
