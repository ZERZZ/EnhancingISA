package com.example.enhancingisa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.enhancingisa.R

// Your existing color schemes:
private val DarkColorScheme = darkColorScheme(
    primary   = Purple80,
    secondary = PurpleGrey80,
    tertiary  = Pink80
)
private val LightColorScheme = lightColorScheme(
    primary      = Purple40,
    secondary    = PurpleGrey40,
    tertiary     = Pink40,
    background   = Color.White,
    surface      = Color.White,
    onBackground = Color.Black,
    onSurface    = Color.Black
)

// Inter font family loaded from res/font/inter_regular.ttf
private val AppFontFamily = FontFamily(
    Font(
        resId = R.font.inter_regular,
        weight = FontWeight.Normal
    )
)

// Dyslexia font family loaded from res/font/open_dyslexic_regular.ttf
private val DyslexiaFontFamily = FontFamily(
    Font(
        resId = R.font.open_dyslexic_regular,
        weight = FontWeight.Normal
    )
)

@Composable
fun EnhancingIsaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    fontScale: Float = 1f,
    dyslexia: Boolean = false,    // toggle for Dyslexia vs Inter
    content: @Composable () -> Unit
) {
    // 1) Pick color scheme exactly as before:
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx)
            else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // 2) Determine which font family to use
    val base = Typography
    val chosenFamily = if (dyslexia) DyslexiaFontFamily else AppFontFamily

    // 3) Scale each style, applying chosenFamily
    val scaledTypography = Typography(
        displayLarge    = base.displayLarge.copy(fontSize   = base.displayLarge.fontSize   * fontScale,
            fontFamily = chosenFamily),
        displayMedium   = base.displayMedium.copy(fontSize  = base.displayMedium.fontSize  * fontScale,
            fontFamily = chosenFamily),
        displaySmall    = base.displaySmall.copy(fontSize   = base.displaySmall.fontSize   * fontScale,
            fontFamily = chosenFamily),
        headlineLarge   = base.headlineLarge.copy(fontSize  = base.headlineLarge.fontSize  * fontScale,
            fontFamily = chosenFamily),
        headlineMedium  = base.headlineMedium.copy(fontSize = base.headlineMedium.fontSize * fontScale,
            fontFamily = chosenFamily),
        headlineSmall   = base.headlineSmall.copy(fontSize  = base.headlineSmall.fontSize  * fontScale,
            fontFamily = chosenFamily),
        titleLarge      = base.titleLarge.copy(fontSize     = base.titleLarge.fontSize     * fontScale,
            fontFamily = chosenFamily),
        titleMedium     = base.titleMedium.copy(fontSize    = base.titleMedium.fontSize    * fontScale,
            fontFamily = chosenFamily),
        titleSmall      = base.titleSmall.copy(fontSize     = base.titleSmall.fontSize     * fontScale,
            fontFamily = chosenFamily),
        bodyLarge       = base.bodyLarge.copy(fontSize      = base.bodyLarge.fontSize      * fontScale,
            fontFamily = chosenFamily),
        bodyMedium      = base.bodyMedium.copy(fontSize     = base.bodyMedium.fontSize     * fontScale,
            fontFamily = chosenFamily),
        bodySmall       = base.bodySmall.copy(fontSize      = base.bodySmall.fontSize      * fontScale,
            fontFamily = chosenFamily),
        labelLarge      = base.labelLarge.copy(fontSize     = base.labelLarge.fontSize     * fontScale,
            fontFamily = chosenFamily),
        labelMedium     = base.labelMedium.copy(fontSize    = base.labelMedium.fontSize    * fontScale,
            fontFamily = chosenFamily),
        labelSmall      = base.labelSmall.copy(fontSize     = base.labelSmall.fontSize     * fontScale,
            fontFamily = chosenFamily)
    )

    MaterialTheme(
        colorScheme  = colorScheme,
        typography   = scaledTypography,
        content      = content
    )
}
