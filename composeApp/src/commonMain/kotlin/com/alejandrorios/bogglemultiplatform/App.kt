package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alejandrorios.bogglemultiplatform.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF9F9F9))
    )
}

internal expect val isAndroid: Boolean
