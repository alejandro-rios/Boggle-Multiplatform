package com.alejandrorios.bogglemultiplatform

import androidx.compose.ui.graphics.NativePaint
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import kotlin.random.Random

internal actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

internal actual val isAndroid: Boolean
    get() = false

actual fun randomUUID(): String = Random.Default.toString()
