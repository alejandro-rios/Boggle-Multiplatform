package com.alejandrorios.bogglemultiplatform.data.repository

import com.alejandrorios.bogglemultiplatform.data.service.BoggleService
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import kotlinx.coroutines.flow.flow

class BoggleRepositoryImpl(
    private val service: BoggleService
) : BoggleRepository {
    override suspend fun fetchWordsFromAPI(board: List<String>) = flow {
        emit(service.fetchWordsFromAPI(board))
    }

    override suspend fun getDefinition(word: String) = flow {
        emit(service.getDefinition(word))
    }
}
