# ReelFlow Part 2: Side Popup Overlay

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Show a Material 3 "Pill" popup on the side of the screen when a reel is counted.

---

### Task 1: Overlay UI & Manager

**Files:**
- Create: `app/src/main/java/com/reels/tracker/overlay/OverlayPillManager.kt`
- Modify: `app/src/main/java/com/reels/tracker/service/ReelFlowService.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Add SYSTEM_ALERT_WINDOW permission**
Add `<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />` to the manifest.

- [ ] **Step 2: Implement OverlayPillManager**
This class handles adding a ComposeView to the WindowManager.
```kotlin
package com.reels.tracker.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.reels.tracker.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class OverlayPillManager(private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null

    fun showPill() {
        if (composeView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
            x = 0
            y = 0
        }

        composeView = ComposeView(context).apply {
            // Necessary for Compose in WindowManager
            val lifecycleOwner = object : LifecycleOwner {
                override val lifecycle = LifecycleRegistry(this).apply {
                    handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                }
            }
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(context as? SavedStateRegistryOwner)
            
            setContent {
                MyApplicationTheme {
                    PillContent {
                        removePill()
                    }
                }
            }
        }

        windowManager.addView(composeView, params)
    }

    private fun removePill() {
        composeView?.let {
            windowManager.removeView(it)
            composeView = null
        }
    }
}

@Composable
fun PillContent(onTimeout: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1500)
        visible = false
        delay(500) // wait for animation
        onTimeout()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "+1 Reel",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
```

- [ ] **Step 3: Integrate with ReelFlowService**
Initialize `OverlayPillManager` in `onCreate` of the service and call `showPill()` in `onAccessibilityEvent`.
