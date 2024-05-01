package com.alejandrorios.bogglemultiplatform.di

import com.alejandrorios.bogglemultiplatform.data.BoardGenerator
import com.alejandrorios.bogglemultiplatform.data.utils.dispatchers.AppCoroutineDispatchersImpl
import com.alejandrorios.bogglemultiplatform.domain.utils.dispatchers.AppCoroutineDispatchers
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin() = initKoin(enableNetworkLogs = false) {}

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(), networkModule(enableNetworkLogs = enableNetworkLogs))
    }

fun commonModule() = module {
    single<AppCoroutineDispatchers> { AppCoroutineDispatchersImpl() }
    single { createBoardGenerator() }
    single { BoggleViewModel(get() , get(), get()) }
}

fun createBoardGenerator() = BoardGenerator()
