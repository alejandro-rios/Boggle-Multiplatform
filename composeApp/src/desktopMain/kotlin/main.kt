import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.alejandrorios.bogglemultiplatform.App

fun main() {
    application {
        val windowState: WindowState = rememberWindowState(width = 800.dp, height = 800.dp)

        Window(
            title = "Boggle Multiplatform",
            state = windowState,
            onCloseRequest = ::exitApplication,
            transparent = false,
            resizable = false,
        ) { App() }
    }
}
