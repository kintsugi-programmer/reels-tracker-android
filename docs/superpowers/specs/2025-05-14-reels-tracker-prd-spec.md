# PRD & Technical Spec: Reels Tracker (ReelFlow)

## 1. Product Overview
ReelFlow is a Material 3 Android application designed to track short-form video consumption (Reels, Shorts) across multiple platforms. It provides immediate feedback via overlays and long-term insights via a dashboard and home-screen widgets.

## 2. Requirements
- **Tracking**: Automatically detect swipes in IG, YT, FB, and Chrome.
- **Feedback**: Side-aligned M3 Pill popup showing current session count.
- **Analytics**: 
    - Total today.
    - Rolling 7-day average.
    - Platform distribution.
- **Widgets**: M3 Glance widgets for Today's Total and Weekly Trend.

## 3. Technical Architecture

### A. Accessibility Service (`ReelFlowService`)
The core background component.
- **Events**: Listens for `AccessibilityEvent.TYPE_VIEW_SCROLLED`.
- **Validation**:
    - Check package name against a whitelist.
    - For Chrome, inspect the URL bar for `reels` or `shorts` keywords.
- **Throttling**: Ignores events within 500ms of the last count to prevent jitter.

### B. Persistence (Room)
- `reels_table`: Stores individual swipe events with platform and time.
- `ReelDao`:
    - `getTodayCount()`
    - `getWeeklyAverage()`
    - `getPlatformStats()`

### C. UI (Jetpack Compose + Material 3)
- **Theme**: Dynamic Color (Material You) support.
- **Components**:
    - `StatCard`: Large M3 card for primary metrics.
    - `PlatformChart`: Visual breakdown of sources.
    - `SideOverlay`: Custom `WindowManager` overlay with `ComposeView`.

### D. Widget (Glance)
- `TodayWidget`: 2x1 grid showing total count.
- `WeeklyWidget`: 4x2 grid showing a trend line.

## 4. Security & Permissions
- **Accessibility Service**: Required for scroll detection.
- **Display over other apps**: Required for the side popup.
- **Usage Stats**: Optional, for cross-referencing app usage time.

## 5. Success Metrics
- Average reels/day visible within 1 tap.
- Minimal battery footprint (Accessibility node tree traversal optimized).
