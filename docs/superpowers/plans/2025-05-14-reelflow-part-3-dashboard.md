# ReelFlow Part 3: Dashboard & Analytics

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Replace the placeholder UI with a Material 3 Dashboard showing reel statistics.

---

### Task 1: Analytics Data Logic

**Files:**
- Modify: `app/src/main/java/com/reels/tracker/data/ReelDao.kt`
- Create: `app/src/main/java/com/reels/tracker/ui/DashboardViewModel.kt`

- [ ] **Step 1: Add Weekly Analytics Queries to DAO**
Update `ReelDao.kt` with methods to get weekly counts and platform breakdowns.
```kotlin
@Query("SELECT COUNT(*) FROM reels WHERE timestamp >= :since")
fun getCountSince(since: Long): Flow<Int>

@Query("SELECT platform, COUNT(*) as count FROM reels GROUP BY platform")
fun getPlatformDistribution(): Flow<List<PlatformStat>>
```

- [ ] **Step 2: Create PlatformStat Data Class**
```kotlin
data class PlatformStat(val platform: String, val count: Int)
```

- [ ] **Step 3: Implement DashboardViewModel**
Fetch today's count, the 7-day average, and platform stats.

---

### Task 2: Material 3 Dashboard UI

**Files:**
- Modify: `app/src/main/java/com/reels/tracker/MainActivity.kt`
- Create: `app/src/main/java/com/reels/tracker/ui/DashboardScreen.kt`

- [ ] **Step 1: Design Stat Cards**
Create an `ElevatedCard` for "Today's Reels" and a secondary card for "Weekly Average".
- [ ] **Step 2: Platform Distribution List**
Show a list of apps (IG, YT, FB) with their respective counts.
- [ ] **Step 3: Permission Status Section**
Add a section showing if the Accessibility Service is enabled, with a button to open Settings if it's not.
- [ ] **Step 4: Update MainActivity**
Replace `BakingScreen()` with `DashboardScreen()`.
