package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alejandrorios.bogglemultiplatform.di.commonModule
import com.alejandrorios.bogglemultiplatform.di.databaseModule
import com.alejandrorios.bogglemultiplatform.di.networkModule
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleScreen
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.theme.AppTheme
import io.github.xxfast.kstore.KStore
import org.koin.compose.KoinApplication

@Composable
internal fun App() {
    KoinApplication(application = {
        modules(commonModule(), networkModule(), databaseModule())
    }) {
        AppTheme {
            BoggleScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF9F9F9)),
            )
        }
    }
}

var appStorage: String? = ""

expect val store: KStore<BoggleUiState>

expect val isAndroid: Boolean
