package com.alejandrorios.bogglemultiplatform.data.service

import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class BoggleService(private val client: HttpClient) {

    suspend fun fetchWordsFromAPI(board: List<String>) = client.get("https://api.codebox.org.uk/boggle/${board.joinToString(separator = "")}").body<List<String>>()

    suspend fun getDefinition(word: String) = client.get("https://api.dictionaryapi.dev/api/v2/entries/en/$word").body<List<DictionaryResponse>>()
}
