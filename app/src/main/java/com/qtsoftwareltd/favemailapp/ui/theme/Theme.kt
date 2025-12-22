package com.qtsoftwareltd.favemailapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Enhanced color schemes with modern colors and better contrast
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,  // Light purple
    secondary = PurpleGrey80,  // Blue-purple
    tertiary = Pink80,  // Pink accent
    background = Color(0xFF0F172A),  // Dark slate background
    surface = Color(0xFF1E293B),  // Slate surface
    surfaceVariant = Color(0xFF334155),  // Lighter slate for variants
    onPrimary = Color(0xFF1E293B),  // Dark text on light primary
    onSecondary = Color(0xFF1E293B),  // Dark text on light secondary
    onBackground = Color(0xFFF1F5F9),  // Light text on dark background
    onSurface = Color(0xFFF1F5F9),  // Light text on dark surface
    onSurfaceVariant = Color(0xFFCBD5E1),  // Lighter text for variants
    error = Color(0xFFEF4444),  // Modern red
    onError = Color(0xFFFFFFFF),  // White on error
    errorContainer = Color(0xFF7F1D1D),  // Dark red container
    onErrorContainer = Color(0xFFFEE2E2),  // Light text on error container
    primaryContainer = Color(0xFF4C1D95),  // Deep purple container
    onPrimaryContainer = Color(0xFFE9D5FF),  // Light text on primary container
    secondaryContainer = Color(0xFF3730A3),  // Indigo container
    onSecondaryContainer = Color(0xFFE0E7FF)  // Light text on secondary container
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,  // Indigo primary
    secondary = PurpleGrey40,  // Light indigo
    tertiary = Pink40,  // Pink accent
    background = Color(0xFFFFFBFE),  // Off-white background
    surface = Color(0xFFFFFFFF),  // Pure white surface
    surfaceVariant = Color(0xFFF3F4F6),  // Light gray variant
    onPrimary = Color(0xFFFFFFFF),  // White on primary
    onSecondary = Color(0xFFFFFFFF),  // White on secondary
    onBackground = Color(0xFF1F2937),  // Dark gray text
    onSurface = Color(0xFF1F2937),  // Dark gray text
    onSurfaceVariant = Color(0xFF6B7280),  // Medium gray for variants
    error = Color(0xFFDC2626),  // Modern red
    onError = Color(0xFFFFFFFF),  // White on error
    errorContainer = Color(0xFFFEE2E2),  // Light red container
    onErrorContainer = Color(0xFF991B1B),  // Dark text on error container
    primaryContainer = Color(0xFFE0E7FF),  // Light indigo container
    onPrimaryContainer = Color(0xFF312E81),  // Dark text on primary container
    secondaryContainer = Color(0xFFEDE9FE),  // Light purple container
    onSecondaryContainer = Color(0xFF5B21B6)  // Dark text on secondary container
)

@Composable
fun FavEmailAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine if we should use dark theme based on theme mode
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}