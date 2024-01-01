package com.alejandrorios.bogglemultiplatform

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

internal actual val isAndroid: Boolean
    get() = true

@Preview(showBackground = true)
@Composable
fun NormalDie() {
    BoggleDie(letter = "B", selected = false, modifier = Modifier.padding(10.dp))
}

@Preview(showBackground = true)
@Composable
fun SelectedDie() {
    BoggleDie(letter = "B", selected = true, modifier = Modifier.padding(10.dp))
}

@Preview(showBackground = true)
@Composable
fun WordDie() {
    BoggleDie(letter = "B", selected = true, isAWord = true, modifier = Modifier.padding(10.dp))
}

