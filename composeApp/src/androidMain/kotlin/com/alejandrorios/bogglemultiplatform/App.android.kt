package com.alejandrorios.bogglemultiplatform

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appStorage = filesDir.path
        setContent { App() }
    }
}

actual val store: KStore<BoggleUiState> by lazy {
    storeOf(Path("${appStorage}/saved.json"), BoggleUiState())
}

actual val currentPlatform: KotlinPlatform
    get() = KotlinPlatform.ANDROID
