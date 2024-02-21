package com.alejandrorios.bogglemultiplatform

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.models.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath

actual val store: KStore<BoggleUiState> by lazy {
    storeOf("${appStorage}/saved.json".toPath(), BoggleUiState())
}

@Preview
@Composable
fun DefaultPreview() {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    )
}
