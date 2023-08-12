import com.alejandrorios.bogglemultiplatform.App
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        BrowserViewportWindow("Boggle Multiplatform") {
            App()
        }
    }
}
