package com.alejandrorios.bogglemultiplatform

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter

internal actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

internal actual val isAndroid: Boolean
    get() = false

@Preview
@Composable
fun DefaultPreview() {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    )
}

@Preview
@Composable
fun BoggleDiePreview() {
    BoggleDie(letter = "B", modifier = Modifier.padding(4.dp))
}
