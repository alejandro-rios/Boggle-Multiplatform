package com.alejandrorios.bogglemultiplatform.domain.repository

import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse
import kotlinx.coroutines.flow.Flow

interface BoggleRepository {

    suspend fun fetchWordsFromAPI(board: List<String>) : Flow<CallResponse<List<String>>>

    suspend fun getDefinition(word: String) : Flow<CallResponse<List<DictionaryResponse>>>
}
