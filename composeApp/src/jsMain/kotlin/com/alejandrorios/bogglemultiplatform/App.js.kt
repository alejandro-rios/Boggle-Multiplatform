package com.alejandrorios.bogglemultiplatform

import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf

actual val store: KStore<BoggleUiState> by lazy {
    storeOf("saved", BoggleUiState())
}
actual val isAndroid: Boolean
    get() = false
