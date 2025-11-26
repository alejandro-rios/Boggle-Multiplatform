import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.alejandrorios.bogglemultiplatform.App
import com.alejandrorios.bogglemultiplatform.appStorage
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import net.harawata.appdirs.AppDirsFactory

fun main() {
    application {
        val windowState: WindowState = rememberWindowState(width = 800.dp, height = 800.dp)
        appStorage = AppDirsFactory.getInstance()
            .getUserDataDir("com.alejandrorios.bogglemultiplatform", "1.0.0", "alejandrorios")

        with(SystemFileSystem) {
            if (!exists(Path(appStorage))) createDirectories(Path("${appStorage}/saved.json"))
        }

        Window(
            title = "Boggle Multiplatform",
            state = windowState,
            onCloseRequest = ::exitApplication,
            transparent = false,
            resizable = false,
        ) { App() }
    }
}
