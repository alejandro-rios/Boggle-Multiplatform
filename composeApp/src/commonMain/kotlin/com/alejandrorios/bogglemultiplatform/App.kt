package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    )
}

internal expect fun NativePaint.setMaskFilter(blurRadius: Float)

internal expect val isAndroid: Boolean

internal expect fun randomUUID(): String
