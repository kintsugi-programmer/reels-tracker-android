package com.reels.tracker.service

import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.AccessibilityService
import com.reels.tracker.data.ReelDatabase
import com.reels.tracker.data.ReelEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReelFlowService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var lastScrollTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastScrollTime > 500) {
                val pkg = event.packageName?.toString() ?: ""
                if (isReelPlatform(pkg)) {
                    saveReel(pkg)
                    lastScrollTime = currentTime
                }
            }
        }
    }

    private fun isReelPlatform(pkg: String): Boolean {
        return pkg.contains("instagram") || pkg.contains("youtube") || pkg.contains("facebook")
    }

    private fun saveReel(pkg: String) {
        scope.launch {
            val db = ReelDatabase.getDatabase(applicationContext)
            db.reelDao().insert(ReelEntry(platform = pkg, packageName = pkg))
        }
    }

    override fun onInterrupt() {}
}
