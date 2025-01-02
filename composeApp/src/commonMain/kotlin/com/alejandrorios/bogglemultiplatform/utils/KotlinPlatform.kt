package com.alejandrorios.bogglemultiplatform.utils

enum class KotlinPlatform {
    ANDROID, IOS ,WASM, JS, DESKTOP;

    val isMobile get() = this == ANDROID || this == IOS

    val isWeb get() = this == JS || this == WASM

    val isWasm get() = this == WASM

    val isAndroid get() = this == ANDROID
}
