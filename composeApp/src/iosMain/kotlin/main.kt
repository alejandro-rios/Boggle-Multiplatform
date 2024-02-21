import androidx.compose.ui.window.ComposeUIViewController
import com.alejandrorios.bogglemultiplatform.App
import com.alejandrorios.bogglemultiplatform.appStorage
import io.github.xxfast.kstore.file.utils.DocumentDirectory
import io.github.xxfast.kstore.utils.ExperimentalKStoreApi
import platform.Foundation.NSFileManager
import platform.UIKit.UIViewController

@OptIn(ExperimentalKStoreApi::class)
fun MainViewController(): UIViewController = ComposeUIViewController {
    appStorage = NSFileManager.defaultManager.DocumentDirectory?.relativePath

    App()
}

