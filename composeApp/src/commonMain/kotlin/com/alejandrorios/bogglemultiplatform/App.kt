package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alejandrorios.bogglemultiplatform.models.BoggleUiState
import com.alejandrorios.bogglemultiplatform.theme.AppTheme
import io.github.xxfast.kstore.KStore

@Composable
internal fun App() = AppTheme {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF9F9F9)),
    )
}

var appStorage: String? = ""

expect val store: KStore<BoggleUiState>
