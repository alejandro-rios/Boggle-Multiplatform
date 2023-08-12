import androidx.compose.ui.window.ComposeUIViewController
import com.alejandrorios.bogglemultiplatform.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
