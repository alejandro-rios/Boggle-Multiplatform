package com.alejandrorios.bogglemultiplatform.data.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpResponse.handleResponse(): CallResponse<T> {
    return try {
        CallResponse.Success(this.body<T>())
    }catch (e: Exception) {
//        CallResponse.Failure(e.message)
        CallResponse.Failure(Exception())
    }
}
