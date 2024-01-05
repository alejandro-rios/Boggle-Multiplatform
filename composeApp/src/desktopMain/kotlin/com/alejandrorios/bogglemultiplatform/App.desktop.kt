package com.alejandrorios.bogglemultiplatform

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal actual val isAndroid: Boolean
    get() = false

@Preview
@Composable
fun DefaultPreview() {
    BoardScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    )
}
