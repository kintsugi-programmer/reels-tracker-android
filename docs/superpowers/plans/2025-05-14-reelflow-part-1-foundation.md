# ReelFlow Part 1: Foundation & Core Tracking

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Setup the database and the Accessibility Service to detect swipes.

---

### Task 1: Data Persistence Layer

**Files:**
- Create: `app/src/main/java/com/reels/tracker/data/ReelEntry.kt`
- Create: `app/src/main/java/com/reels/tracker/data/ReelDao.kt`
- Create: `app/src/main/java/com/reels/tracker/data/ReelDatabase.kt`

- [ ] **Step 1: Create ReelEntry Entity**
```kotlin
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
```

- [ ] **Step 2: Create ReelDao**
```kotlin
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
```

- [ ] **Step 3: Create ReelDatabase**
```kotlin
package com.reels.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReelEntry::class], version = 1)
abstract class ReelDatabase : RoomDatabase() {
    abstract fun reelDao(): ReelDao

    companion object {
        @Volatile
        private var INSTANCE: ReelDatabase? = null

        fun getDatabase(context: Context): ReelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReelDatabase::class.java,
                    "reels_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### Task 2: Core Tracking Engine

**Files:**
- Create: `app/src/main/java/com/reels/tracker/service/ReelFlowService.kt`
- Create: `app/src/main/res/xml/accessibility_service_config.xml`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create Accessibility Config**
Create `app/src/main/res/xml/accessibility_service_config.xml`:
```xml
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeViewScrolled|typeWindowStateChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault|flagIncludeNotImportantViews|flagRetrieveInteractiveWindows"
    android:canRetrieveWindowContent="true"
    android:description="@string/accessibility_description" />
```

- [ ] **Step 2: Implement ReelFlowService**
```kotlin
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
```

- [ ] **Step 3: Update Manifest**
Register the service inside `<application>` tag.
