import androidx.compose.ui.window.ComposeUIViewController
import com.alejandrorios.bogglemultiplatform.App
import com.alejandrorios.bogglemultiplatform.appStorage
import platform.Foundation.NSHomeDirectory
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    appStorage = NSHomeDirectory()

    App()
}

