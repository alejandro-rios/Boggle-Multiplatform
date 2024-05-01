package com.alejandrorios.bogglemultiplatform.data.utils

sealed interface CallResponse<T> {
    data class Success<T>(val data: T) : CallResponse<T>
    data class Failure<T>(val t: Throwable) : CallResponse<T>
}
