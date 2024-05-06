package com.alejandrorios.bogglemultiplatform

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleScreen
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath

actual val store: KStore<BoggleUiState> by lazy {
    println(appStorage)
    storeOf("${appStorage}/saved.json".toPath(), BoggleUiState())
}

@Preview
@Composable
fun DefaultPreview() {
    BoggleScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    )
}

actual val isAndroid: Boolean
    get() = false
