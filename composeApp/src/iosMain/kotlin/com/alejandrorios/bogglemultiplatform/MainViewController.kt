package com.alejandrorios.bogglemultiplatform

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSHomeDirectory
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
    appStorage = NSHomeDirectory()

    App()
}

