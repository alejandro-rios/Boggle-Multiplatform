package com.alejandrorios.bogglemultiplatform.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BoggleDimensions(
    /**
     * ```
     * xxs = 4.dp
     * xs = 8.dp
     * sm = 12.dp
     * md = 16.dp
     * lg = 20.dp
     * xl = 24.dp
     * xxl = 32.dp
     * xxxl = 40.dp
     * xxxxl = 48.dp
     * huge = 56.dp
     * mega = 64.dp
     * ```
     */
    val spacing: Spacing,
    /**
     * ```
     * md = 16.dp
     * full = 100.dp
     * ```
     */
    val cornerRadius: CornerRadius,
    /**
     * ```
     * wordCounterProgress = 6.dp
     * borderBox = 10.dp
     * ```
     */
    val strokeWidth: StrokeWidth
)

data class Spacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
    val xxxxl: Dp = 48.dp,
    val xxxxxl: Dp = 56.dp,
    val xxxxxxl: Dp = 64.dp,
)

data class CornerRadius(
    val md: Dp = 16.dp,
    val full: Dp = 100.dp,
)

data class StrokeWidth(
    val wordCounterProgress: Dp = 6.dp,
    val borderBox: Dp = 10.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { dimensions() }

internal fun dimensions() = BoggleDimensions(
    spacing = Spacing(),
    cornerRadius = CornerRadius(),
    strokeWidth = StrokeWidth(),
)
