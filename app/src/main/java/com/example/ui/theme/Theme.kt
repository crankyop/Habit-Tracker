package com.example.ui.theme

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

// Editorial theme color token definitions
val EditorialBackground = Color(0xFFFEF7FF)
val EditorialSurface = Color(0xFFFFFFFF)
val EditorialPrimary = Color(0xFF6750A4)
val EditorialOnPrimary = Color(0xFFFFFFFF)
val EditorialPrimaryContainer = Color(0xFFEADDFF)
val EditorialOnPrimaryContainer = Color(0xFF21005D)
val EditorialSecondary = Color(0xFF625B71)
val EditorialOnSecondary = Color(0xFFFFFFFF)
val EditorialSecondaryContainer = Color(0xFFE8DEF8)
val EditorialOnSecondaryContainer = Color(0xFF1D192B)
val EditorialSurfaceVariant = Color(0xFFF3EDF7)
val EditorialOnSurfaceVariant = Color(0xFF49454F)
val EditorialOutline = Color(0xFFCAC4D0)
val EditorialOnBackground = Color(0xFF1D1B20)
val EditorialOnSurface = Color(0xFF1D192B)

data class ThemePalette(
    val name: String,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val accent: Color
)

val THEMES = listOf(
    ThemePalette(
        name = "Elegant Purple",
        primary = Color(0xFF6750A4),
        primaryContainer = Color(0xFFEADDFF),
        onPrimaryContainer = Color(0xFF21005D),
        secondary = Color(0xFF625B71),
        secondaryContainer = Color(0xFFE8DEF8),
        onSecondaryContainer = Color(0xFF1D192B),
        background = Color(0xFFFEF7FF),
        onBackground = Color(0xFF1D1B20),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1D192B),
        surfaceVariant = Color(0xFFF3EDF7),
        onSurfaceVariant = Color(0xFF49454F),
        outline = Color(0xFFCAC4D0),
        accent = Color(0xFF21005D)
    ),
    ThemePalette(
        name = "Emerald Serenade",
        primary = Color(0xFF2E7D32),
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF0F5113),
        secondary = Color(0xFF455A64),
        secondaryContainer = Color(0xFFCFD8DC),
        onSecondaryContainer = Color(0xFF263238),
        background = Color(0xFFF1F8F6),
        onBackground = Color(0xFF1A2321),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1A2321),
        surfaceVariant = Color(0xFFE5EFEC),
        onSurfaceVariant = Color(0xFF3F4946),
        outline = Color(0xFFBEC9C5),
        accent = Color(0xFF0F5113)
    ),
    ThemePalette(
        name = "Oceanic Blue",
        primary = Color(0xFF1565C0),
        primaryContainer = Color(0xFFBBDEFB),
        onPrimaryContainer = Color(0xFF0D47A1),
        secondary = Color(0xFF37474F),
        secondaryContainer = Color(0xFFECEFF1),
        onSecondaryContainer = Color(0xFF21272A),
        background = Color(0xFFF4F7FC),
        onBackground = Color(0xFF10141A),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF10141A),
        surfaceVariant = Color(0xFFE2E7F0),
        onSurfaceVariant = Color(0xFF3E434F),
        outline = Color(0xFFBDC2CF),
        accent = Color(0xFF0D47A1)
    ),
    ThemePalette(
        name = "Sunset Clay",
        primary = Color(0xFFD84315),
        primaryContainer = Color(0xFFFFCCBC),
        onPrimaryContainer = Color(0xFF4E342E),
        secondary = Color(0xFF5D4037),
        secondaryContainer = Color(0xFFEFEBE9),
        onSecondaryContainer = Color(0xFF2D1E1B),
        background = Color(0xFFFFFBF9),
        onBackground = Color(0xFF221512),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF221512),
        surfaceVariant = Color(0xFFF8ECE9),
        onSurfaceVariant = Color(0xFF50413E),
        outline = Color(0xFFD4C1BD),
        accent = Color(0xFF4E342E)
    ),
    ThemePalette(
        name = "Midnight Gold",
        primary = Color(0xFFEED078),
        primaryContainer = Color(0xFF312812),
        onPrimaryContainer = Color(0xFFFFEAAB),
        secondary = Color(0xFFC5BFA5),
        secondaryContainer = Color(0xFF2C2A24),
        onSecondaryContainer = Color(0xFFE2DDD5),
        background = Color(0xFF141311),
        onBackground = Color(0xFFEBE6DF),
        surface = Color(0xFF1D1B18),
        onSurface = Color(0xFFEBE6DF),
        surfaceVariant = Color(0xFF2E2A24),
        onSurfaceVariant = Color(0xFFC5BFA5),
        outline = Color(0xFF7E786D),
        accent = Color(0xFFFFEAAB)
    )
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeIndex: Int = 0,
    content: @Composable () -> Unit,
) {
    val selectedTheme = THEMES.getOrElse(themeIndex) { THEMES[0] }
    
    // If the theme itself is dark (like Midnight Gold which is index 4), use dark system colors
    val isDark = darkTheme || themeIndex == 4

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = selectedTheme.primary,
            onPrimary = if (themeIndex == 4) Color(0xFF141311) else Color.White,
            primaryContainer = selectedTheme.primaryContainer,
            onPrimaryContainer = selectedTheme.onPrimaryContainer,
            secondary = selectedTheme.secondary,
            secondaryContainer = selectedTheme.secondaryContainer,
            onSecondaryContainer = selectedTheme.onSecondaryContainer,
            background = selectedTheme.background,
            onBackground = selectedTheme.onBackground,
            surface = selectedTheme.surface,
            onSurface = selectedTheme.onSurface,
            surfaceVariant = selectedTheme.surfaceVariant,
            onSurfaceVariant = selectedTheme.onSurfaceVariant,
            outline = selectedTheme.outline
        )
    } else {
        lightColorScheme(
            primary = selectedTheme.primary,
            onPrimary = Color.White,
            primaryContainer = selectedTheme.primaryContainer,
            onPrimaryContainer = selectedTheme.onPrimaryContainer,
            secondary = selectedTheme.secondary,
            secondaryContainer = selectedTheme.secondaryContainer,
            onSecondaryContainer = selectedTheme.onSecondaryContainer,
            background = selectedTheme.background,
            onBackground = selectedTheme.onBackground,
            surface = selectedTheme.surface,
            onSurface = selectedTheme.onSurface,
            surfaceVariant = selectedTheme.surfaceVariant,
            onSurfaceVariant = selectedTheme.onSurfaceVariant,
            outline = selectedTheme.outline
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
