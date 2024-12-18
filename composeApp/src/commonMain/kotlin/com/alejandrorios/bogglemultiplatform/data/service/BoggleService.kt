package com.alejandrorios.bogglemultiplatform.data.service

import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.utils.handleResponse
import com.alejandrorios.bogglemultiplatform.utils.OpenForMokkery
import io.ktor.client.HttpClient
import io.ktor.client.request.get

@OpenForMokkery
class BoggleService(private val client: HttpClient) {
    suspend fun fetchWordsFromAPI(board: List<String>) = client.get(
        urlString = "https://api.codebox.org.uk/boggle/${board.joinToString(separator = "")}"
    ).handleResponse<List<String>>()

    suspend fun getDefinition(word: String) = client.get(
        urlString = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
    ).handleResponse<List<DictionaryResponse>>()
}
