package com.alejandrorios.bogglemultiplatform.di

import com.alejandrorios.bogglemultiplatform.data.repository.LocalRepositoryImpl
import com.alejandrorios.bogglemultiplatform.domain.repository.LocalRepository
import com.alejandrorios.bogglemultiplatform.store
import org.koin.dsl.module

fun databaseModule() = module {
    single { createKStore() }
    single<LocalRepository> { LocalRepositoryImpl(get(), get()) }
}

fun createKStore() = store
