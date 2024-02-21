package com.alejandrorios.bogglemultiplatform

import com.alejandrorios.bogglemultiplatform.models.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath

actual val store: KStore<BoggleUiState> by lazy {
    storeOf("${appStorage}/saved.json".toPath(), BoggleUiState())
}
