package com.example.taskmaster.ui.theme
import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), secondary = Color(0xFF03DAC6), tertiary = Color(0xFF3700B3),
    background = Color(0xFF121212), surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black, onSecondary = Color.Black, onBackground = Color.White, onSurface = Color.White,
)

@Composable
fun TaskMasterTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkColorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
