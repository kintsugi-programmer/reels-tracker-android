package com.reels.tracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reels.tracker.data.PlatformStat
import com.reels.tracker.data.ReelDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class DashboardViewModel(private val reelDao: ReelDao) : ViewModel() {

    private val startOfToday: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private val startOfSevenDaysAgo: Long
        get() = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    val todayCount: StateFlow<Int> = reelDao.getTodayCount(startOfToday)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyAverage: StateFlow<Double> = reelDao.getCountSince(startOfSevenDaysAgo)
        .map { total -> total / 7.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val platformStats: StateFlow<List<PlatformStat>> = reelDao.getPlatformDistribution()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
