package com.reels.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis()
)
