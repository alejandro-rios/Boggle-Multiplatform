package com.alejandrorios.bogglemultiplatform

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleScreen
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual val store: KStore<BoggleUiState> by lazy {
    storeOf(Path("${appStorage}/saved.json"), BoggleUiState())
}

@Preview
@Composable
fun DefaultPreview() {
    BoggleScreen(
        uiState = BoggleUiState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        onGameStart = {},
        onCreateNewGame = {},
        onCloseDialog = {},
        onCloseDefinitionDialog = {},
        onCloseHintDefinitionDialog = {},
        onAddWord = {},
        onEvaluateWord = { _, _ -> },
        onUseAPI = {},
        onChangeLanguage = {},
        onGetHint = {},
        onGetWordDefinition = {},
    )
}

actual val currentPlatform: KotlinPlatform
    get() = KotlinPlatform.DESKTOP
