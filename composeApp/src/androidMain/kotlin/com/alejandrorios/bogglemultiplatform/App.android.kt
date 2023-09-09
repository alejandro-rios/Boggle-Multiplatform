package com.alejandrorios.bogglemultiplatform

import android.app.Application
import android.graphics.BlurMaskFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    )
}

@Preview
@Composable
fun BoggleDiePreview() {
    BoggleDie(letter = "B", modifier = Modifier.padding(4.dp))
}

