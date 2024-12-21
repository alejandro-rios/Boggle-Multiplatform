package com.alejandrorios.bogglemultiplatform

import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual val store: KStore<BoggleUiState> by lazy {
    storeOf(Path("${appStorage}/saved.json"), BoggleUiState())
}

actual val currentPlatform: KotlinPlatform
    get() = KotlinPlatform.IOS
