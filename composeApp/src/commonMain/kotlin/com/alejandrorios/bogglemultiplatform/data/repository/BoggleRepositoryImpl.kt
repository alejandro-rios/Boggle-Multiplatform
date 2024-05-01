package com.alejandrorios.bogglemultiplatform.data.repository

import com.alejandrorios.bogglemultiplatform.data.service.BoggleService
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import com.alejandrorios.bogglemultiplatform.domain.utils.dispatchers.AppCoroutineDispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BoggleRepositoryImpl(
    private val service: BoggleService,
    private val appCoroutineDispatchers: AppCoroutineDispatchers
): BoggleRepository {
    override suspend fun fetchWordsFromAPI(board: List<String>) = flow {
        emit(service.fetchWordsFromAPI(board))
    }.flowOn(appCoroutineDispatchers.io)

    override suspend fun getDefinition(word: String) = flow {
        emit(service.getDefinition(word))
    }.flowOn(appCoroutineDispatchers.io)
}
