package com.alejandrorios.bogglemultiplatform.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Immutable
data class BoggleTypography(
    val boardLetterLarge: TextStyle,
    val boardLetterMedium: TextStyle,
    val gameStatsLarge: TextStyle,
    val gameStatsMedium: TextStyle,
    val button: TextStyle,
    val counter: TextStyle,
    val foundWord: TextStyle
)

fun BoggleTypography.boardLetter(isQ: Boolean = false): TextStyle {
    return if (isQ) boardLetterMedium else boardLetterLarge
}

internal fun typography(): BoggleTypography {
    return BoggleTypography(
        boardLetterLarge = TextStyle(
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        boardLetterMedium = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        gameStatsLarge = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        gameStatsMedium = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal
        ),
        button = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        ),
        counter = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        foundWord = TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start
        )
    )
}

internal val LocalTypography = staticCompositionLocalOf { typography() }
