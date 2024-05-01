package com.alejandrorios.bogglemultiplatform.di

import com.alejandrorios.bogglemultiplatform.data.repository.BoggleRepositoryImpl
import com.alejandrorios.bogglemultiplatform.data.service.BoggleService
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun networkModule(enableNetworkLogs: Boolean = false) = module {
    single { createJson() }
    single { createHttpClient(get(), enableNetworkLogs = enableNetworkLogs) }
    single { BoggleService(get()) }
    single<BoggleRepository> { BoggleRepositoryImpl(get(), get()) }
}

@OptIn(ExperimentalSerializationApi::class)
fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false }

fun createHttpClient(json: Json, enableNetworkLogs: Boolean) = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }
}
