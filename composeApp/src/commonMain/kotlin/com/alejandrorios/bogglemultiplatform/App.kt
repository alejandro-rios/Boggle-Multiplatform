package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandrorios.bogglemultiplatform.di.commonModule
import com.alejandrorios.bogglemultiplatform.di.databaseModule
import com.alejandrorios.bogglemultiplatform.di.networkModule
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleScreen
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleViewModel
import com.alejandrorios.bogglemultiplatform.ui.theme.AppTheme
import io.github.xxfast.kstore.KStore
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun App() {
    KoinApplication(application = {
        modules(commonModule(), networkModule(), databaseModule())
    }) {
        AppTheme {
            val boggleViewModel: BoggleViewModel = koinViewModel()
            val boggleUiState by boggleViewModel.uiState.collectAsStateWithLifecycle()

            BoggleScreen(
                uiState = boggleUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF9F9F9)),
                onGameStart = boggleViewModel::gameStart,
                onCreateNewGame = boggleViewModel::createNewGame,
                onCloseDialog = boggleViewModel::closeDialog,
                onCloseDefinitionDialog = boggleViewModel::closeDefinitionDialog,
                onCloseHintDefinitionDialog = boggleViewModel::closeHintDefinitionDialog,
                onAddWord = boggleViewModel::addWord,
                onEvaluateWord = boggleViewModel::evaluateWord,
                onUseAPI = boggleViewModel::useAPI,
                onChangeLanguage = boggleViewModel::changeLanguage,
                onGetHint = boggleViewModel::getHint,
                onGetWordDefinition = boggleViewModel::getWordDefinition,
            )
        }
    }
}

var appStorage: String? = ""

enum class KotlinPlatform {
    ANDROID, IOS ,WASM, JS, DESKTOP;

    val isMobile get() = this == ANDROID || this == IOS

    val isWeb get() = this == JS || this == WASM

    val isWasm get() = this == WASM

    val isAndroid get() = this == ANDROID
}

expect val currentPlatform: KotlinPlatform

expect val store: KStore<BoggleUiState>
