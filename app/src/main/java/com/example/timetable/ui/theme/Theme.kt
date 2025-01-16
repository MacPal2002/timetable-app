package com.example.timetable.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.timetable.R

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEB5E28), // Stonowany, ciepły pomarańczowy
    secondary = Color(0xFF018786), // Stonowany miętowy
    tertiary = Color(0xFFFF7043), // Ciepły pomarańczowy do akcentów
    background = Color(0xFF121212), // Typowe ciemne tło
    surface = Color(0xFF232027), // Jaśniejszy odcień dla powierzchni
    onPrimary = Color.Black, // Kontrastowy tekst na pomarańczowym tle
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)



private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF009688), // Ciepły pomarańczowy
    secondary = Color(0xFF00BFA5), // Kontrastujący miętowy
    tertiary = Color(0xFFFFD54F), // Ciepły żółty
    background = Color(0xFFFFF8E1), // Jasno kremowy, przyjazny dla oka
    surface = Color(0xFFFFF3E0), // Blady pomarańczowy dla powierzchni
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F), // Ciemny tekst dla kontrastu
    onSurface = Color(0xFF1C1B1F) // Ciemny tekst dla powierzchni
)





@Composable
fun TimetableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
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
//        typography = Typography,
        typography = CustomTypography, // Ustawienie niestandardowego Typography
        content = content
    )
}

// Zdefiniowanie niestandardowej czcionki
val ChocoCookyFont = FontFamily(
    Font(R.font.choco_cooky, FontWeight.Normal)
)

// Zdefiniowanie Typography z niestandardową czcionką
val CustomTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = ChocoCookyFont),
    displayMedium = Typography().displayMedium.copy(fontFamily = ChocoCookyFont),
    displaySmall = Typography().displaySmall.copy(fontFamily = ChocoCookyFont),

    headlineLarge = Typography().headlineLarge.copy(fontFamily = ChocoCookyFont),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = ChocoCookyFont),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = ChocoCookyFont),

    titleLarge = Typography().titleLarge.copy(fontFamily = ChocoCookyFont),
    titleMedium = Typography().titleMedium.copy(fontFamily = ChocoCookyFont),
    titleSmall = Typography().titleSmall.copy(fontFamily = ChocoCookyFont),

    bodyLarge = Typography().bodyLarge.copy(fontFamily = ChocoCookyFont),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = ChocoCookyFont),
    bodySmall = Typography().bodySmall.copy(fontFamily = ChocoCookyFont),

    labelLarge = Typography().labelLarge.copy(fontFamily = ChocoCookyFont),
    labelMedium = Typography().labelMedium.copy(fontFamily = ChocoCookyFont),
    labelSmall = Typography().labelSmall.copy(fontFamily = ChocoCookyFont)
)