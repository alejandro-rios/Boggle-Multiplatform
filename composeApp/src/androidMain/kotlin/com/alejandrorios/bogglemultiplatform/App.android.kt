package com.alejandrorios.bogglemultiplatform

import android.app.Application
import android.graphics.BlurMaskFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.NativePaint
import java.util.UUID

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

        setContent { App() }
    }
}

internal actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
}

internal actual val isAndroid: Boolean
    get() = true

actual fun randomUUID() = UUID.randomUUID().toString()

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//
//    BoardScreen(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp),
//    )
//}

